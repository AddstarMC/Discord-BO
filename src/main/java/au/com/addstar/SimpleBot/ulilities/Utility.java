package au.com.addstar.SimpleBot.ulilities;

import au.com.addstar.SimpleBot.SimpleBot;
import au.com.addstar.SimpleBot.objects.Invitation;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.*;

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

    public static void setUserNick(IGuild g, IUser u, String nick){
        try {
            g.setUserNickname(u,nick);
        } catch (MissingPermissionsException e) {
            SimpleBot.log.error(" We dont have permission to set the nick of " + u.getDisplayName(g));
        } catch (DiscordException | RateLimitException e) {
            e.printStackTrace();
        }
    }

    public static void setRoleforUser(IGuild g, IUser u, IRole r){
        List<IRole> currroles = g.getRolesForUser(u);
        currroles.add(r);
        IRole[] newroles = currroles.toArray(new IRole[0]);
        try {
           g.editUserRoles(u,newroles);
        } catch (MissingPermissionsException e) {
            SimpleBot.log.error(" We dont have permission to set the role of " + u.getDisplayName(g) + " to " + newroles.toString());
            e.printStackTrace();
        } catch (RateLimitException | DiscordException e) {
            e.printStackTrace();
        }
    }

    public static IInvite checkforInvite(IChannel chan, Invitation botinvite){
        try {
            List<IInvite> invites = chan.getInvites();
            for(IInvite invite : invites){
                if(invite.getInviteCode().equals(botinvite.getInviteCode()))return invite;
            }
        } catch (DiscordException | RateLimitException e) {
            e.printStackTrace();
        } catch (MissingPermissionsException e) {
            SimpleBot.log.error(" We dont have permission to get channel invites");
            e.printStackTrace();
        }
        return null;
    }
    public static IInvite createInvite(IChannel chan, int age, int maxUses, Boolean temp){

        return createInvite(chan, age, maxUses, true,true);
    }

    public static IInvite createInvite(IChannel chan, int age, int maxUses, Boolean temp, boolean unique){
        IInvite invite = null;
        try {
            invite = chan.createInvite(age, maxUses, temp, unique);
        } catch (MissingPermissionsException | DiscordException | RateLimitException e) {
            e.printStackTrace();
        }
        return invite;
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
