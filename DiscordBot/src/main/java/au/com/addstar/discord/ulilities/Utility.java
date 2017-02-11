package au.com.addstar.discord.ulilities;

import au.com.addstar.discord.SimpleBot;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 10/12/2016.
 */
public class Utility {

    public  static void sendPrivateMessage(IUser u, String m){
        try {
            MessageBuilder builder = new MessageBuilder(SimpleBot.client).withChannel(u.getOrCreatePMChannel()).withContent(m);
            builder.build();
        } catch (RateLimitException e) { // RateLimitException thrown. The bot is sending messages too quickly!
            SimpleBot.log.error("Sending messages too quickly!");
        } catch (DiscordException e) { // DiscordException thrown. Many possibilities. Use getErrorMessage() to see what went wrong.
            SimpleBot.log.error(e.getErrorMessage()); // Print the error message sent by Discord
        } catch (MissingPermissionsException e) { // MissingPermissionsException thrown. The bot doesn't have permission to send the message!
            SimpleBot.log.error("Missing permissions for channel!");

        }

    }

    public static void sendChannelMessage(String announceID, String m){
        IChannel aChannel = SimpleBot.client.getChannelByID(announceID);
        MessageBuilder builder = new MessageBuilder(SimpleBot.client).withChannel(aChannel).withContent(m);
        try {
            builder.build();
        } catch (RateLimitException e) { // RateLimitException thrown. The bot is sending messages too quickly!
            SimpleBot.log.error("Sending messages too quickly!");
    } catch (DiscordException e) { // DiscordException thrown. Many possibilities. Use getErrorMessage() to see what went wrong.
        SimpleBot.log.error(e.getErrorMessage()); // Print the error message sent by Discord
    } catch (MissingPermissionsException e) { // MissingPermissionsException thrown. The bot doesn't have permission to send the message!
            SimpleBot.log.error("Missing permissions for channel!");
    }
    }
    public static void deleteMessages(MessageList list,int r){
        try {
            int lastindex;
            if(r>list.size()){
                lastindex = list.size();
            }else{
                lastindex = r;
            }
            if (lastindex>100)lastindex=100;
            if (lastindex < 1)lastindex=1;
            int firstindex = 0;
            list.deleteFromRange(firstindex,lastindex);
        } catch (RateLimitException | DiscordException e) {
            e.printStackTrace();
        } catch (MissingPermissionsException e) {
            SimpleBot.log.error("Permission Error");
            e.printStackTrace();
        }

    }


    public static void deleteMessage(IMessage m){
        try {
            m.delete();
        } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
            e.printStackTrace();
        }
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

    public static IChannel getChannelByID(IGuild guild, String channnelId) {
        return  SimpleBot.client.getGuildByID(guild.getID()).getChannelByID(channnelId);
    }
}
