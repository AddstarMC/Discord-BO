package au.com.addstar.discord.listeners;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.managers.InvitationManager;
import au.com.addstar.discord.managers.UserManager;
import au.com.addstar.discord.objects.GuildConfig;
import au.com.addstar.discord.objects.Invitation;
import au.com.addstar.discord.objects.McUser;
import au.com.addstar.discord.ulilities.Utility;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.MessageBuilder.Styles;
import sx.blah.discord.util.MessageList;

import java.util.EnumSet;
import java.util.List;

import static au.com.addstar.discord.ulilities.MessageFormatter.addStyle;
import static au.com.addstar.discord.ulilities.Utility.deleteMessage;
import static au.com.addstar.discord.ulilities.Utility.deleteMessages;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/12/2016.
 */
public class CommandListener {

    @EventSubscriber
    public void commandListener(MessageReceivedEvent event) {

        IMessage m = event.getMessage();
        IUser u = m.getAuthor();
        if ((m.getChannel().isPrivate())) {
            return;//no handling PMs here
        }
        IGuild g = m.getGuild();
        GuildConfig config = SimpleBot.gConfigs.get(g.getID());
        String prefix = config.getPrefix();
        String message = m.getContent();
        if (!message.startsWith(prefix)) {
            return;//no prefix dont handle it
        }
        message = message.substring(prefix.length());
        String[] mSplit = message.split("\\s+");
        boolean admin = false;
        boolean moderator = false;
        List<IRole> roles = u.getRolesForGuild(g);
        for (IRole r : roles) {
            EnumSet<Permissions> perms = r.getPermissions();
            if (perms.contains(Permissions.ADMINISTRATOR)) {
                admin = true;
            }
            if (perms.contains(Permissions.KICK)) {
                moderator = true;
            }
        }
        if (admin) { //process admin commands
            switch (mSplit[0].toLowerCase()) {
                case "set":
                    if (mSplit.length > 1) {
                        switch (mSplit[1].toLowerCase()) {
                            case "prefix":
                                String oldPrefix = config.getPrefix();
                                if (mSplit.length > 2) {
                                    String newPrefix = mSplit[2];
                                    config.setPrefix(newPrefix);
                                    config.saveConfig();
                                    Utility.sendPrivateMessage(u, "Prefix updated Old: " + oldPrefix + "New: " + config.getPrefix());
                                } else {
                                    Utility.sendPrivateMessage(u, "Current Prefix is : " + oldPrefix);
                                }
                                return;
                            case "welcomemessage":
                                String oldMessage = config.getWelcomeMessage();
                                if (mSplit.length > 2) {
                                    String newMessage = message.substring(mSplit[0].length() + mSplit[1].length() + 2);
                                    config.setWelcomeMessage(newMessage);
                                    config.saveConfig();
                                    Utility.sendPrivateMessage(u, "Welcome message updated.  Old: " + oldMessage + "New: " + config.getWelcomeMessage());
                                } else {
                                    Utility.sendPrivateMessage(u, "Current Message is : " + oldMessage);
                                }
                                return;
                            case "announcechannelid":

                                IChannel oldChannel = SimpleBot.client.getChannelByID(config.getAnnounceChannelID());

                                if (mSplit.length > 2) {
                                    String newAnnounceID = mSplit[2];
                                    IChannel newChannel = SimpleBot.client.getChannelByID(newAnnounceID);
                                    if (newChannel == null) {
                                        if (oldChannel != null) {
                                            Utility.sendPrivateMessage(u, "Current Annoucement Channel is : " + oldChannel.getName());
                                        }
                                        Utility.sendPrivateMessage(u, newAnnounceID + " could not find a channel with that ID");
                                        return;
                                    }
                                    config.setAnnounceChannelID(newAnnounceID);
                                    config.saveConfig();
                                    if (oldChannel == null) {
                                        Utility.sendPrivateMessage(u, "Channel updated New: " + newChannel.getName());
                                    } else {
                                        Utility.sendPrivateMessage(u, "Channel updated Old: " + oldChannel.getName() + "New: " + newChannel.getName());
                                    }

                                } else {
                                    Utility.sendPrivateMessage(u, "Current Annoucement Channel is : " + oldChannel.getName());
                                }
                                return;
                            case "modchannelid":

                                IChannel oldMChannel = SimpleBot.client.getChannelByID(config.getModChannelID());

                                if (mSplit.length > 2) {
                                    String newModChannelID = mSplit[2];
                                    IChannel newChannel = SimpleBot.client.getChannelByID(newModChannelID);
                                    if (newChannel == null) {
                                        if (oldMChannel != null) {
                                            Utility.sendPrivateMessage(u, "Current Moderation Channel is : " + oldMChannel.getName());
                                        }
                                        Utility.sendPrivateMessage(u, newModChannelID + " could not find a channel with that ID");
                                        return;
                                    }
                                    config.setModChannelID(newModChannelID);
                                    config.saveConfig();
                                    if (oldMChannel == null) {
                                        Utility.sendPrivateMessage(u, "Channel updated New: " + newChannel.getName());
                                    } else {
                                        Utility.sendPrivateMessage(u, "Channel updated Old: " + oldMChannel.getName() + "New: " + newChannel.getName());
                                    }

                                } else {
                                    Utility.sendPrivateMessage(u, "Current Annoucement Channel is : " + oldMChannel.getName());
                                }
                                return;
                            case "expirytime":
                                if (mSplit.length > 2) {
                                    int exp = Integer.parseInt(mSplit[2]);
                                    config.setExpiryTime(exp);
                                }
                                break ;
                            case "reportstatus":
                                Boolean report = config.isReportStatusChange();
                                if (mSplit.length > 2) {
                                    String newReport = mSplit[2];
                                    Boolean nR = Boolean.getBoolean(newReport);
                                    config.setReportStatusChange(nR);
                                    config.saveConfig();
                                    Utility.sendPrivateMessage(u, "ReportingStatus updated Old: " + report + "New: " + nR);
                                } else {
                                    Utility.sendPrivateMessage(u, "Current ReportingStatus is : " + report);
                                }
                                return;
                            case "help":
                            default:
                                sendAdminHelp(g, u, prefix);
                                return;
                        }
                    } else {
                        sendAdminHelp(g, u, prefix);
                        return;
                    }

                case "reloadguildconfig":
                    GuildConfig c = SimpleBot.gConfigs.get(m.getGuild().getID());
                    c.loadConfig();
                    Utility.sendPrivateMessage(u, "Configurations reloaded");
                    return;
                case "discorbotexit":
                    SimpleBot.exit();
                case "help":
                    sendAdminHelp(g, u, prefix);
                    break;
                default:
            }

        }
        if (moderator) { //moderation commands
            switch (mSplit[0].toLowerCase()) {
                case "warn":
                    if (mSplit.length > 1) {
                        String warned = mSplit[1];
                        List<IUser> users = m.getChannel().getGuild().getUsersByName(warned);
                        if (users.size() > 1) {
                            //todo  more than 1 user by that name
                            Utility.sendPrivateMessage(u, "to many users with that name cant warn");
                        }

                    }
                    return;
                case "purge":
                    int i = 1;
                    if (mSplit.length > 1) {
                        String num = mSplit[1];
                        i = Integer.parseInt(num);
                    }
                    if (i < 1) i = 1;
                    MessageList list = m.getChannel().getMessages();
                    deleteMessages(list, i);
                    Utility.sendPrivateMessage(u,i+" messages deleted from " + m.getChannel().getName());
                    SimpleBot.log.info(u.getName() + " deleted " + i + " messages from " + m.getChannel().getName());
                    deleteMessage(m);
                    return;
                case "listpendinginvites":
                    List<Invitation> invites = InvitationManager.getPendingInvites(config);
                    if (invites != null && invites.size() > 0) {
                        for (Invitation inv : invites) {
                            Long time = inv.getExpiryTime();
                            Utility.sendPrivateMessage(u, "Invititation for " + inv.getUserName() + " Code: " + inv.getInviteCode() + "Expiry: " + Utility.getDate(time));
                        }
                    } else {
                        Utility.sendPrivateMessage(u, "No invites found");
                    }
                    break;
                case "help":
                    sendModeraterHelp(g,u,prefix);
                default:

            }

        }
        switch (mSplit[0].toLowerCase()) {
            case "register":
                Utility.sendPrivateMessage(u, "Processing Registration....");
                if (mSplit.length != 2) {
                    Utility.sendPrivateMessage(u, "You must add the invite code. Please copy the command you recieved.");
                    break;
                } else {
                    String code = mSplit[1];
                    Invitation invite = InvitationManager.getInvitation(config, code);
                    if (invite == null) {
                        invite = InvitationManager.getExpiredInvite(config, code);
                        SimpleBot.log.info(u.getName()+ " using expired invite code.");
                    }
                    if(invite == null){
                        Utility.sendPrivateMessage(u, "Invitation Code not found");
                    } else {
                        UserManager.setUserNick(g, u, invite.getUserName());
                        SimpleBot.log.info(u.getName() + " has agreed to the rules. Nicknamed: " + invite.getUserName());
                        Utility.sendChannelMessage(config.getAnnounceChannelID(), u.getDisplayName(g) + " has agreed to the rules.  Welcome to " + g.getName());
                        List<IRole> userroles = g.getRolesByName("member");
                        if (userroles.size() > 1) {
                            SimpleBot.log.warn(" More than 1 role found for \"member\"");
                        }
                        if (userroles.size() == 0) {
                            SimpleBot.log.warn(" No role found for \"member\"");
                        }
                        if(userroles.size() == 1){
                            UserManager.setRoleforUser(g, u, userroles.get(0));
                            SimpleBot.log.info(u.getName() + " applied Role: "+userroles.get(0).getName());
                        }
                        Utility.sendPrivateMessage(u,"Registration complete.");
                        McUser user = UserManager.loadUser(u.getID());
                        if (user == null){
                            SimpleBot.log.info("MCUSER was null - should have been created on join??.");
                            user = new McUser(u.getID());
                        }
                        user.addUpdateDisplayName(g.getID(),invite.getUserName());
                        user.setMinecraftUUID(invite.getUuid());
                        UserManager.saveUser(user);
                        SimpleBot.log.info("Registration complete.");
                        InvitationManager.removeInvitation(config, invite.getInviteCode());
                    }
                }
                deleteMessage(m);
                return;
            case "help":
                sendUserHelp(g, u, prefix);
                break;
            default:

        }

    }

    private void sendAdminHelp(IGuild g, IUser u, String prefix){
        String builder = addStyle(Styles.BOLD,
                SimpleBot.client.getOurUser().getDisplayName(g) +
                        " Admin Help Text") +
                "\n" +
                prefix + "set prefix <prefix>  - Sets the prefix for commands" + "\n" +
                prefix + "set welcomemessage <message>  - Set the Welcome Message" + "\n" +
                prefix + "set announcechannelid <channelID>  - Set the Announcment Channel" + "\n" +
                prefix + "set modchannelid <channelID>  - Set the Mod reporting Channel" + "\n" +
                prefix + "set reportstatus <true/false>  - Report Status Changes?" + "\n" +
                prefix + "set expirytime <seconds>  - Invite Expiry Time?" + "\n" +

                prefix + "reloadguildconfig  - Reloads the config from file. Does not save current." + "\n";
        Utility.sendPrivateMessage(u, builder);
    }

    private void sendModeraterHelp(IGuild g,IUser u, String prefix){
        String builder = addStyle(Styles.BOLD,
                SimpleBot.client.getOurUser().getDisplayName(g) +
                        " Moderator Help Text") + " \n" +
                prefix + "purge <Number - Purge this number of messages" + "\n" +
                prefix + "warn <@Name> -- not yet implemented" + "\n" +
                prefix + "listpendinginvites  -- list all pending invites";
        Utility.sendPrivateMessage(u, builder);
    }
    private void sendUserHelp(IGuild g, IUser u, String prefix) {
        String builder = addStyle(Styles.BOLD,
                SimpleBot.client.getOurUser().getDisplayName(g) +
                        " User Help Text") +
                "\n" +
                prefix + "register <token>   - register on the server" + "\n";
        Utility.sendPrivateMessage(u, builder);
    }
    }

