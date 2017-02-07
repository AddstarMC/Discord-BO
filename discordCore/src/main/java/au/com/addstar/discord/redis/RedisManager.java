package au.com.addstar.discord.redis;

import au.com.addstar.discord.messages.*;
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

import static au.com.addstar.discord.messages.ResponseMessage.ResponseTypes.FAIL;
import static au.com.addstar.discord.messages.ResponseMessage.ResponseTypes.OK;

/**
 * Created for the AddstarMC Server Network
 * Created by Narimm on 1/02/2017.
 */
public class RedisManager {
    public final static String REDISKEY = "dBot";
    private RedisClient client;
    private static String serverID = null;
    private RedisPubSubAsyncCommands<String, IMessage> subscribeConnection;
    private RedisPubSubCommands<String, IMessage> publishConnection;
    private ChannelHandler handler;
    private Map<CommandType, IncomingCommandHandler> commandHandlers;
    private final ListMultimap<String, WaitFuture> waitingFutures;

    public long getNextCommandId() {
        return nextCommandId++;
    }

    private long nextCommandId;

    public RedisManager(String id) {
        serverID = id;
        this.handler = new ChannelHandler();
        waitingFutures = ArrayListMultimap.create();
        nextCommandId = 0;
        commandHandlers = Maps.newHashMap();
    }

    public void initialize(String host, int port, String password){
        RedisURI uri = new RedisURI(host,port,30, TimeUnit.SECONDS);
        if(password != null || password.length()>0)uri.setPassword(password);
        client = RedisClient.create(uri);
        RedisCodec<String, IMessage> codec = new IMessageObjectCodec();
        StatefulRedisPubSubConnection<String, IMessage> connection = client.connectPubSub(codec);
        subscribeConnection = connection.async();
        subscribeConnection.addListener(handler);
        subscribeConnection.psubscribe(REDISKEY + "." + serverID);
        connection = client.connectPubSub(codec);
        publishConnection = connection.sync();

    }

    public static String getServerID() {
        return serverID;
    }

    public void registerCommandHandler(IncomingCommandHandler handler, CommandType type) {
            commandHandlers.put(type, handler);
    }

    private void send(String targetId, IMessage message) {
        publishConnection.publish(String.format("%s.%s>%s", REDISKEY, serverID, targetId), message);
    }

    public ListenableFuture<ResponseMessage> sendCommand(String serverId, IMessage message) {

        // Send it
        WaitFuture future = new WaitFuture(message.getMessageId());
        synchronized (waitingFutures) {
            waitingFutures.put(serverId, future);
        }
        send(serverId, message);
        return future;
    }

    public boolean ping(){
        String result = publishConnection.ping();
        return result!=null;
    }


    private void handleCommand(String serverId, long commandID, IMessage message) {
        IncomingCommandHandler handler = null;
        if(message.getMessageType() == MessageType.Command){
            AbstractCommandMessage commandMessage = (AbstractCommandMessage) message;
             handler = commandHandlers.get(commandMessage.getCommandType());
        }
        ResponseMessage response;
        if (handler == null) {
            response =  new ResponseMessage(serverId,commandID);
            response.setResponse(FAIL);
            response.setError("No handler found for " + message.getMessageType());
        } else {
            response = handler.onCommand(message);
        }
        // Reply
        send(serverId, response);
    }

    private class ChannelHandler implements RedisPubSubListener<String, IMessage> {


        @Override
        public void message(String channel, IMessage message) {

        }

        @Override
        public void message(String pattern, String channel, IMessage message) {
            String sourceId;
            if (pattern.startsWith(REDISKEY)) {
                // Server to server communication
                int start = channel.lastIndexOf('.') + 1;
                int end = channel.indexOf('>');
                sourceId = channel.substring(start, end);
            } else {
                return;
            }
            if (sourceId.equals(serverID)){
                return;
            }
            if(!message.getMessageType().equals("Response")) {
                handleCommand(sourceId, message.getMessageId(), message);
            }else{
                handleReply(sourceId,message.getMessageId(),(ResponseMessage)message);
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

    private void handleReply(String serverId, long queryId, ResponseMessage response) {
        synchronized (waitingFutures) {
            List<WaitFuture> futures = waitingFutures.get(serverId);

            // Find and handle the correct future
            for (WaitFuture future : futures) {
                if (future.getQueryId() == queryId) {
                    // All done
                    if (response.getResponse() == OK) {
                        future.set(response);
                    } else {
                        future.setException(new CommandException(response.getErrorMessage()));
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
    public void timeoutOldQueries() {
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

    private static class WaitFuture extends AbstractFuture<ResponseMessage> {
        private final long commandID;
        private final long initializeTime;

        public WaitFuture(long commandID) {
            this.commandID = commandID;
            initializeTime = System.currentTimeMillis();
        }

        public long getQueryId() {
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
        public boolean set(ResponseMessage value) {
            return super.set(value);
        }

        @Override
        public boolean setException(Throwable throwable) {
            return super.setException(throwable);
        }
    }
}
