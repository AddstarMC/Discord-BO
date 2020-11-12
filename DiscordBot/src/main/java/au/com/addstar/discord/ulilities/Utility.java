package au.com.addstar.discord.ulilities;

import au.com.addstar.discord.SimpleBot;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.entity.RestChannel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 10/12/2016.
 */
public class Utility {

    public  static void sendPrivateMessage(User u, String m){
        u.getPrivateChannel().subscribe(privateChannel -> {
            privateChannel.createMessage(m).subscribe();
        });
    }

    public static void sendChannelMessage(Long announceID, String m){
        SimpleBot.client.getChannelById(Snowflake.of(announceID))
                .createMessage(m);
    }

    public static void deleteMessages(RestChannel chan, Snowflake r) {
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
