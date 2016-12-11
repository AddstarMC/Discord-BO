package au.com.addstar.SimpleBot.listeners;

import au.com.addstar.SimpleBot.SimpleBot;
import au.com.addstar.SimpleBot.objects.GuildConfig;
import au.com.addstar.SimpleBot.ulilities.Utility;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageList;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.EnumSet;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/12/2016.
 */
public class CommandListener {

    private final SimpleBot bot;

    public CommandListener(SimpleBot bot) {
        this.bot = bot;
    }

    @EventSubscriber
    public void commandListener(MessageReceivedEvent event) {

        IMessage m = event.getMessage();
        IUser u = m.getAuthor();
        IGuild g = m.getGuild();
        GuildConfig config = SimpleBot.gConfigs.get(g.getID());
        String prefix = config.getPrefix();
        String message =  m.getContent();
        if (message.startsWith(prefix)) {
            message = message.substring(prefix.length());
            if (m.getChannel().isPrivate()) {
                Utility.sendPrivateMessage(u, "Commands must be used in a public server channel");
                return;
            }
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
            if (!admin || !moderator) {
                Utility.sendPrivateMessage(u, "You're not an admin...this has been logged....");
                return;
            }
            if (admin) {
                //this is a command we can respond to
                String[] mSplit = message.split("\\s+");
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
                                    break;
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
                                    break;
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
                                    break;
                                case "modchannelid":

                                    IChannel oldMChannel = SimpleBot.client.getChannelByID(config.getAnnounceChannelID());

                                    if (mSplit.length > 2) {
                                        String newAnnounceID = mSplit[2];
                                        IChannel newChannel = SimpleBot.client.getChannelByID(newAnnounceID);
                                        if (newChannel == null) {
                                            if (oldMChannel != null) {
                                                Utility.sendPrivateMessage(u, "Current Annoucement Channel is : " + oldMChannel.getName());
                                            }
                                            Utility.sendPrivateMessage(u, newAnnounceID + " could not find a channel with that ID");
                                            return;
                                        }
                                        config.setAnnounceChannelID(newAnnounceID);
                                        config.saveConfig();
                                        if (oldMChannel == null) {
                                            Utility.sendPrivateMessage(u, "Channel updated New: " + newChannel.getName());
                                        } else {
                                            Utility.sendPrivateMessage(u, "Channel updated Old: " + oldMChannel.getName() + "New: " + newChannel.getName());
                                        }

                                    } else {
                                        Utility.sendPrivateMessage(u, "Current Annoucement Channel is : " + oldMChannel.getName());
                                    }
                                    break;
                                case "reportStatus":
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
                                    break;
                                default:
                                    //sendSetHelp()
                            }
                        } else {
                            //sendhelp()
                        }

                        break;
                    case "reloadguildconfig":
                        GuildConfig c = SimpleBot.gConfigs.get(m.getGuild().getID());
                            c.loadConfig();
                            Utility.sendPrivateMessage(u,"Configurations reloaded");

                        break;
                    default:
                        //sendHelp()

                }

            }
            if(moderator){
                //this is a command we can respond to
                IChannel channel = m.getChannel();
                String[] mSplit = message.split("\\s+");
                switch (mSplit[0].toLowerCase()){
                    case "warn":
                        if(mSplit.length>1){
                            String warned = mSplit[1];
                            List<IUser> users = m.getChannel().getGuild().getUsersByName(warned);
                            if (users.size()>1){
                                //more than 1 user by that name
                            }
                        }
                        break;
                    case "purge":
                        Integer r = 1;
                        if(mSplit.length>1){
                            String num = mSplit[1];
                            r = Integer.parseInt(num);
                        }
                        if (r < 1) r=1;
                        MessageList list = m.getChannel().getMessages();

                        try{
                            list.deleteFromRange(list.size()-r,list.size());
                        }catch (DiscordException e){
                            SimpleBot.log.error(e.getErrorMessage()); // Print the error message sent by Discord
                        }catch (RateLimitException e){
                            SimpleBot.log.error("Sending messages too quickly!");
                        }catch (MissingPermissionsException e){
                            SimpleBot.log.error("Missing permissions for channel!");
                        }
                    default:
                        //send help

                }

            }
        }//no prefix ignore

    }
}
