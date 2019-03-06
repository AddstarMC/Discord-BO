package au.com.addstar.discord.listeners;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.commands.CommandContext;
import au.com.addstar.discord.managers.InvitationManager;
import au.com.addstar.discord.managers.UserManager;
import au.com.addstar.discord.objects.GuildConfig;
import au.com.addstar.discord.objects.Invitation;
import au.com.addstar.discord.objects.McUser;
import au.com.addstar.discord.objects.UnhandledCommandException;
import au.com.addstar.discord.ulilities.MessageFormatter;
import au.com.addstar.discord.ulilities.Utility;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static au.com.addstar.discord.ulilities.MessageFormatter.addStyle;
import static au.com.addstar.discord.ulilities.Utility.deleteMessage;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/12/2016.
 */
public class CommandListener {
    Flux<Member> adminMembers;
    Flux<Member> moderators;
    
    public CommandListener(SimpleBot bot) {
        this.adminMembers = SimpleBot.client.getGuilds().
                flatMap(Guild::getMembers).filter(m -> isModerater(m) .block());
        this.moderators = SimpleBot.client.getGuilds().flatMap(Guild::getMembers).filter(m ->isModerater(m).block()).cache();
    
        bot.client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(messageEvent -> {
            commandListener(messageEvent);
        });
    }
    private Mono<Boolean> isAdmin(Mono<Member> member) {
        return member.flatMap(member1 -> member1.
                getRoles().
                any(role -> role.getPermissions().contains(Permission.ADMINISTRATOR)));
    }
    private Mono<Boolean> isAdmin(Member member) {
        return member.getRoles().any(role -> role.getPermissions().contains(Permission.ADMINISTRATOR));
    }
    
    private Mono<Boolean> isModerater(Member member){
        return member.getRoles().any(role -> role.getPermissions().contains(Permission.KICK_MEMBERS));
    }
    public Mono<Void> commandListener(MessageCreateEvent event) {
        Message m = event.getMessage();
        //check we have content.
        if(!m.getContent().isPresent())
            return Mono.empty();
        String content = m.getContent().get();
        if(!event.getGuildId().isPresent())
            return onPrivateMessage();
        
        if (!event.getMember().isPresent()) {
            return Mono.empty();
        }
        GuildConfig guildConfig = SimpleBot.instance.getGuildConfig(event.getGuildId().get().asLong());
        Member member = event.getMember().get();
        return Mono.just(member)
                .filter(member1 -> !member1.isBot())
                .filter(ignored -> content.startsWith(guildConfig.getPrefix()))
                .flatMap(ignored -> this.executeCommand(new CommandContext(event)));
                
                
            Mono<GuildChannel> mono = event.getMessage().getChannel()
                    .ofType(TextChannel.class)
                    .ofType(GuildChannel.class)
                    .filter(messageChannel -> this.filterCommandMessages(messageChannel, m))
                    .flatMap(channel -> {
                        final String[] mSplit = m.getContent().get().split("\\s+");
                        GuildConfig config = SimpleBot.instance.getGuildConfig(channel.getGuildId().asLong());
                        processMemberCommands(member,mSplit,config);
                        if (isAdmin(member)) {
                            processAdminCommands(mSplit, config, member, event.getMessage().getContent().get());
                        } else if (moderators.hasElement(member).block()) {
                            doModeratorCommands(m);
                        } else {
                            doMemberCommands(m);
                        }
                        return channel;
                    }).subscribe();
        }
    
   
                /*if(adminMembers.hasElement(u))
                flatMap(messageChannel -> {
                    String fullMessage = m.getContent().get();
                    final GuildConfig config = SimpleBot.instance.getGuildConfig(((GuildChannel)messageChannel).getGuildId().asLong());
                       if (!fullMessage.startsWith(prefix)) {
            final String message = fullMessage.substring(prefix.length());
            final String[] mSplit = message.split("\\s+");
            u.getRoles().map(role -> {
                if(role.getPermissions().contains(Permission.ADMINISTRATOR)){
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
                        
                                        Channel oldChannel = SimpleBot.client.getChannelById(Snowflake.of(config.getAnnounceChannelID())).block();
                        
                                        if (mSplit.length > 2) {
                                            Long newAnnounceID = Long.parseLong(mSplit[2]);
                                            Channel newChannel = SimpleBot.client.getChannelById(Snowflake.of(newAnnounceID))
                                                    .on
                                                    .subscribe(channel -> {
                                                    
                                                    
                                                    })
                                            );
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
                                            Long newModChannelID = Long.parseLong(mSplit[2]);
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
                            GuildConfig c = SimpleBot.instance.getGuildConfig(m.getGuild().getLongID());
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
                if(role.getPermissions().contains(Permission.KICK_MEMBERS)){
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
                            deleteMessages(m.getChannel(), i);
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
                                McUser user = UserManager.loadUser(u.getLongID());
                                if (user == null){
                                    SimpleBot.log.info("MCUSER was null - should have been created on join??.");
                                    user = new McUser(u.getLongID());
                                }
                                user.addUpdateDisplayName(g.getLongID(),invite.getUserName());
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
            }).subscribe();
            
        };
        

        }*/
    
    private Mono<Message> processMemberCommands (Member member,  String[] mSplit, GuildConfig config) throws UnhandledCommandException {
        List<String> messages = new ArrayList<>();
            switch (mSplit[0].toLowerCase()) {
                case "register":
                    messages.add("Processing Registration....");
                    if(mSplit.length == 2){
                        String code = mSplit[1];
                        Invitation i = InvitationManager.getInvitation(config, code);
                        if (i == null) {
                            i = InvitationManager.getExpiredInvite(config, code);
                        }
                        if(i ==null) {
                            throw new UnhandledCommandException();
                            return sendModeraterHelp(member, config.getPrefix());
                        }
                        final Invitation invite = i;
                        return UserManager.setUserNick(member, invite.getUserName()).;
                            SimpleBot.log.info(u.getDisplayName() + " has agreed to the rules. Nicknamed: " + invite.getUserName());
                            
                            Utility.sendChannelMessage(config.getAnnounceChannelID(), u.getDisplayName() + " has agreed to the rules.  Welcome to " +.getName());
                            List<Role> userroles = u.getGuild()
                                    .map(Guild::getRoles)
                                    .filter (
                                            role -> {
                                                role.filter(role1 -> {
                                                    role1.getName().equals("moderator");
                                                });
                                    )
                                                if (userroles.size() > 1) {
                                                    SimpleBot.log.warn(" More than 1 role found for \"member\"");
                                                }
                                                if (userroles.size() == 0) {
                                                    SimpleBot.log.warn(" No role found for \"member\"");
                                                }
                                                if(userroles.size() == 1){
                                                    UserManager.setRoleforUser(u.getGuild().block(), u, userroles.get(0));
                                                    SimpleBot.log.info(u.getDisplayName() + " applied Role: "+userroles.get(0).getName());
                                                }
                                                Utility.sendPrivateMessage(u,"Registration complete.");
                                                McUser user = UserManager.loadUser(u.getId().asLong());
                                                if (user == null){
                                                    SimpleBot.log.info("MCUSER was null - should have been created on join??.");
                                                    user = new McUser(u.getId());
                                                }
                                                user.addUpdateDisplayName(u.getGuildId().asLong(),invite.getUserName());
                                                user.setMinecraftUUID(invite.getUuid());
                                                UserManager.saveUser(user);
                                                SimpleBot.log.info("Registration complete.");
                                                InvitationManager.removeInvitation(config, invite.getInviteCode());
                                            }
                        
                        deleteMessage(m);
                        }
                        }
                        if (mSplit.length != 2) {
                        messages.add("You must add the invite code. Please copy the command you recieved.");
                        break;
                    } else {
                        String code = mSplit[1];
                        Invitation invite = InvitationManager.getInvitation(config, code);
                        if (invite == null) {
                            invite = InvitationManager.getExpiredInvite(config, code);
                            SimpleBot.log.info(u.getDisplayName()+ " using expired invite code.");
                        }
                        if(invite == null) {
                            messages.add("Invitation Code not found");
                            return u;}}
                            deleteMessage(m);
                        } else {

                        return;
                        case "help":
                            sendUserHelp(g, u, prefix);
                            break;
                        default:
                         }
    }
    
    
    private Mono<Message> sendAdminHelp(Mono<Member> mono, String prefix){
        return mono.flatMap(User::getPrivateChannel).flatMap(channel -> channel.createMessage(
                " Admin Help Text" +
                "\n" +
                prefix + "set prefix <prefix>  - Sets the prefix for commands" + "\n" +
                prefix + "set welcomemessage <message>  - Set the Welcome Message" + "\n" +
                prefix + "set announcechannelid <channelID>  - Set the Announcment Channel" + "\n" +
                prefix + "set modchannelid <channelID>  - Set the Mod reporting Channel" + "\n" +
                prefix + "set reportstatus <true/false>  - Report Status Changes?" + "\n" +
                prefix + "set expirytime <seconds>  - Invite Expiry Time?" + "\n" +
    
                prefix + "reloadguildconfig  - Reloads the config from file. Does not save current." + "\n"));
    }
    
    private void processAdminCommands(String[] mSplit, GuildConfig config, Member u, String message){
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
                        
                            Channel oldChannel = SimpleBot.client.getChannelById(Snowflake.of(config.getAnnounceChannelID())).block();
                        
                            if (mSplit.length > 2) {
                                Long newAnnounceID = Long.parseLong(mSplit[2]);
                                Mono<Channel> newChannel = SimpleBot.client.getChannelById(Snowflake.of(newAnnounceID));
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
                                Long newModChannelID = Long.parseLong(mSplit[2]);
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
                GuildConfig c = SimpleBot.instance.getGuildConfig(m.getGuild().getLongID());
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
    
    private Mono<Void> executeCommand(CommandContext context){
    
    }
    private static Mono<Message> sendModeraterHelp(Member u, String prefix){
        String builder = addStyle(MessageFormatter.Styles.BOLD,
                        " Moderator Help Text") + " \n" +
                prefix + "purge <Number - Purge this number of messages" + "\n" +
                prefix + "warn <@Name> -- not yet implemented" + "\n" +
                prefix + "listpendinginvites  -- list all pending invites";
        return Utility.sendPrivateMessage(u, builder);
    }
    
    private static Mono<Message> sendUserHelp(Mono<Member> sender,String prefix) {
        String builder = addStyle(MessageFormatter.Styles.BOLD,
                        " User Help Text") +
                "\n" +
                prefix + "register <token>   - register on the server" + "\n";
        return Utility.sendPrivateMessage(sender, builder);
    }
    
    private boolean filterCommandMessages(GuildChannel channel, Message message){
            
            final GuildConfig config = SimpleBot.instance.getGuildConfig(channel.getGuildId().asLong());
            String prefix = config.getPrefix();
            if (!message.getContent().get().startsWith(prefix)) {
                return false;
            } else {
                return true;
            }
    }
    
    private Mono<Void> onPrivateMessage(){
    
    }

