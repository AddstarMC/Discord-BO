package au.com.addstar.discord.ulilities;

import au.com.addstar.discord.SimpleBot;
import discord4j.core.object.entity.*;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 10/12/2016.
 */
public class Utility {

    public  static Mono<Message> sendPrivateMessage(Member target, String m){
        return target.getPrivateChannel().
                flatMap(privateChannel ->
                            privateChannel.createMessage(m));
    }
    public  static Mono<Message> sendPrivateMessage(Mono<Member> target, String m){
        return target.flatMap(Member::getPrivateChannel).
                flatMap(privateChannel ->
                        privateChannel.createMessage(m));
    }
    public static void sendChannelMessage(Long announceID, String m){
        SimpleBot.client.getChannelById(Snowflake.of(announceID)).subscribe(channel -> {
            if(channel instanceof MessageChannel){
                ((MessageChannel) channel).createMessage(m);
            } else {
                SimpleBot.log.info("Channel:" +announceID+ " is not a MessageChannel");
            }
        });
    }
    public static void deleteMessages(Channel chan,Snowflake r) {
        if (chan instanceof MessageChannel) {
            ((MessageChannel) chan).getMessagesAfter(r).subscribe(Message::delete);
        
        }
    }
    
    public static void deleteMessage (Message mess){
                mess.delete();
        }

    public static UUID StringtoUUID(String uuidstring){
        if (uuidstring.length() < 32) {
            throw new IllegalArgumentException("This is not a UUID");
        }
        if (!uuidstring.contains("-")) {
            return UUID.fromString(String.format("%s-%s-%s-%s-%s", uuidstring.substring(0, 8), uuidstring.substring(8, 12), uuidstring.substring(12, 16), uuidstring.substring(16, 20), uuidstring.substring(20)));
        } else {
            return UUID.fromString(uuidstring);
        }
    }

    public static String getDate(Long timelong){
        Date date = new Date(timelong);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
        return dateFormat.format(date);
    }
}
