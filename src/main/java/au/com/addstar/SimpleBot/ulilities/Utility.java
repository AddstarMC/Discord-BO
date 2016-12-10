package au.com.addstar.SimpleBot.ulilities;

import au.com.addstar.SimpleBot.SimpleBot;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

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





}
