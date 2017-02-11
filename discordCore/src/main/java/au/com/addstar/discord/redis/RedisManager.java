package au.com.addstar.discord.redis;

import au.com.addstar.discord.ILog;
import au.com.addstar.discord.messages.*;
import au.com.addstar.discord.messages.identifiers.CommandType;
import au.com.addstar.discord.messages.identifiers.MessageStatus;
import au.com.addstar.discord.messages.identifiers.MessageType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;
import com.lambdaworks.redis.pubsub.api.async.RedisPubSubAsyncCommands;
import com.lambdaworks.redis.pubsub.api.sync.RedisPubSubCommands;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static au.com.addstar.discord.messages.identifiers.MessageStatus.OK;

/**
 * Created for the AddstarMC Server Network
 * Created by Narimm on 1/02/2017.
 */
public class RedisManager {
    private final static String REDISKEY = "dBot";
    private final String receiverId;
    private RedisClient client;
    private final String senderId;
    private final ILog log;
    private RedisPubSubAsyncCommands<String, AMessage> subscribeConnection;
    private RedisPubSubCommands<String, AMessage> publishConnection;
    private final ChannelHandler handler;
    private final Map<CommandType, IncomingCommandHandler> commandHandlers;
    private final ListMultimap<String, WaitFuture> waitingFutures;

    public long getNextCommandId() {
        return nextCommandId++;
    }

    private long nextCommandId;

    public RedisManager(String myId, String rId, ILog logwrapper) {
        senderId = myId;
        this.receiverId = rId;
        this.log = logwrapper;
        this.handler = new ChannelHandler();
        waitingFutures = ArrayListMultimap.create();
        nextCommandId = 0;
        commandHandlers = Maps.newHashMap();
    }

    public boolean initialize(String host, int port, String password){
        if(senderId == null || receiverId == null){
            //With no sender and reciever ID this cant work so no point configuring
            return false;
        }
        RedisURI uri = new RedisURI(host,port,30, TimeUnit.SECONDS);
        if(password != null || password.length()>0)uri.setPassword(password);
        client = RedisClient.create(uri);
        RedisCodec<String, AMessage> codec = new IMessageObjectCodec();
        StatefulRedisPubSubConnection<String, AMessage> connection = client.connectPubSub(codec);
        subscribeConnection = connection.async();
        subscribeConnection.addListener(handler);
        subscribeConnection.psubscribe(REDISKEY + "." + receiverId);
        connection = client.connectPubSub(codec);
        publishConnection = connection.sync();
        return true;
    }

    public void terminate(){
        subscribeConnection.punsubscribe(REDISKEY + "." + receiverId);
        subscribeConnection.removeListener(handler);
        subscribeConnection.close();
        publishConnection.close();
        client.shutdown();
        client = null;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void registerCommandHandler(IncomingCommandHandler handler, CommandType type) {
            commandHandlers.put(type, handler);
    }

    private void send(String targetId, AMessage message) {
        publishConnection.publish(String.format("%s.%s>%s", REDISKEY, senderId, targetId), message);
    }

    public ListenableFuture<AMessage> sendCommand(String targetId, AMessage message) {

        // Send it
        WaitFuture future = new WaitFuture(message.getMessageId());
        synchronized (waitingFutures) {
            waitingFutures.put(targetId, future);
        }
        send(targetId, message);
        return future;
    }

    public boolean ping(){
        String result = publishConnection.ping();
        return result!=null;
    }

    public ILog getLog() {
        return log;
    }


    private void handleCommand(String serverId, long commandID, AMessage message) {
        IncomingCommandHandler handler = null;
        if(message.getMessageType() == MessageType.Command){
            AbstractMessage commandMessage = (AbstractMessage) message;
             handler = commandHandlers.get(commandMessage.getCommandType());
        }
        AMessage response;
        if (handler == null) {
            response =  new ResponseMessage(message.getCommandType(),serverId,commandID, MessageStatus.FAIL,"No Handlers Found");
        } else {
            response = handler.onCommand(message);
        }
        // Reply
        send(serverId, response);
    }

    private void handleReply(String fromId, long messageId, AMessage response) {
        synchronized (waitingFutures) {
            List<WaitFuture> futures = waitingFutures.get(fromId);

            // Find and handle the correct future
            for (WaitFuture future : futures) {
                if (future.getMessageId() == messageId) {
                    // All done
                    if (response.getStatus() == OK) {
                        future.set(response);
                    } else {
                        future.setException(new CommandException(response.getMessage()));
                    }

                    futures.remove(future);
                    break;
                }
            }

            // Remove expired futures
            timeoutOldQueries();
        }
    }

    /**
     * Times out all old queries
     */
    private void timeoutOldQueries() {
        synchronized (waitingFutures) {
            Iterator<WaitFuture> it = waitingFutures.values().iterator();
            while (it.hasNext()) {
                WaitFuture future = it.next();
                if (future.isOld()) {
                    future.setException(new CommandException("Timeout"));
                    it.remove();
                }
            }
        }
    }



    private class ChannelHandler implements RedisPubSubListener<String, AMessage> {


        @Override
        public void message(String channel, AMessage message) {

        }

        @Override
        public void message(String pattern, String channel, AMessage message) {
            String sourceId;
            if (pattern.startsWith(REDISKEY)) {
                // Server to server communication
                int start = channel.lastIndexOf('.') + 1;
                int end = channel.indexOf('>');
                sourceId = channel.substring(start, end);
            } else {
                return;
            }
            if (sourceId.equals(senderId)){
                return;
            }
            if(!(message.getMessageType() == MessageType.Response)) {
                if(sourceId !=  receiverId){
                    log.info("Command Message recieved from :"+sourceId+".");
                    log.info("This RedisManager only handles commands from a source ID= " + receiverId);
                    return;
                }
                handleCommand(sourceId, message.getMessageId(), message);
            }else{
                handleReply(sourceId,message.getMessageId(), message);
            }

        }

        @Override
        public void subscribed(String channel, long count) {

        }

        @Override
        public void psubscribed(String pattern, long count) {

        }

        @Override
        public void unsubscribed(String channel, long count) {

        }

        @Override
        public void punsubscribed(String pattern, long count) {

        }
    }

    private static class WaitFuture extends AbstractFuture<AMessage> {
        private final long commandID;
        private final long initializeTime;

        public WaitFuture(long commandID) {
            this.commandID = commandID;
            initializeTime = System.currentTimeMillis();
        }

        public long getMessageId() {
            return commandID;
        }

        /**
         * Is this future too old (> 10 seconds)
         * @return True if too old
         */
        public boolean isOld() {
            return System.currentTimeMillis() > initializeTime + 10000;
        }

        @Override
        public boolean set(AMessage value) {
            return super.set(value);
        }

        @Override
        public boolean setException(Throwable throwable) {
            return super.setException(throwable);
        }
    }
}
