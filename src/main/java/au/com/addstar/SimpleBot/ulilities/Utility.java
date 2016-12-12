package au.com.addstar.SimpleBot.ulilities;

import au.com.addstar.SimpleBot.SimpleBot;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.List;
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

    public static void deleteMessage(IMessage m){
        try {
            m.delete();
        } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
            e.printStackTrace();
        }
    }

    public static void setUserNick(IGuild g, IUser u, String nick){
        try {
            g.setUserNickname(u,nick);
        } catch (MissingPermissionsException e) {
            SimpleBot.log.error(" We dont have permission to set the nick of " + u.getDisplayName(g));
            e.printStackTrace();
        } catch (DiscordException | RateLimitException e) {
            e.printStackTrace();
        }
    }

    public static void setRoleforUser(IGuild g, IUser u, IRole r){
       List<IRole> roles = u.getRolesForGuild(g);
       roles.add(r);
       IRole[] role = (IRole[])roles.toArray();
        try {
            g.editUserRoles(u,role);
        } catch (MissingPermissionsException e) {
            SimpleBot.log.error(" We dont have permission to set the role of " + u.getDisplayName(g) + " to " + roles.toString());
            e.printStackTrace();
        } catch (RateLimitException e) {
            e.printStackTrace();
        } catch (DiscordException e) {
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











}
