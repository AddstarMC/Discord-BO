package au.com.addstar.discord.listeners;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.managers.UserManager;
import au.com.addstar.discord.objects.GuildConfig;
import au.com.addstar.discord.objects.McUser;
import au.com.addstar.discord.ulilities.Utility;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;

import java.util.Set;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/12/2016.
 */
public class ManagementListener {

    SimpleBot bot =  SimpleBot.instance;
    
    public ManagementListener(final SimpleBot bot) {
        DiscordClient client = bot.client;
        client.getEventDispatcher().
                on(ReadyEvent.class)
                .subscribe(readyEvent -> {onReadyEvent(readyEvent);
                });
        client.getEventDispatcher().on(MemberJoinEvent.class)
                .subscribe(memberJoinEvent -> {
                    onJoinEvent(memberJoinEvent);
                });
        client.getEventDispatcher().on(MemberLeaveEvent.class)
                .subscribe(memberLeaveEvent -> {
                    onLeaveEvent(memberLeaveEvent);
                });
        client.getEventDispatcher().on(PresenceUpdateEvent.class).subscribe(
                presenceUpdateEvent -> {
                    onUserPresenceChange(presenceUpdateEvent);
                });
    }
    
    
    public void onReadyEvent(ReadyEvent event){
        Set<ReadyEvent.Guild> guilds = event.getGuilds();
        for(ReadyEvent.Guild g:guilds) {
                Long id = g.getId().asLong();
                GuildConfig config  =  new GuildConfig(id);
                bot.addGuild(id, config);
        }
        
        UserManager.initialize(SimpleBot.client);
    }

    public void onJoinEvent(MemberJoinEvent event){
        Member u = event.getMember();
        Guild g = event.getGuild().block();
        GuildConfig config = bot.getGuildConfig(g.getId().asLong());
        McUser user = UserManager.loadUserFromFile(u.getId().asLong());
        if(user == null){
            user = new McUser(u.getId());
            UserManager.cacheUser(user);
        }
        Utility.sendPrivateMessage(u, config.getWelcomeMessage());
        Utility.sendChannelMessage(config.getAnnounceChannelID(), u.getDisplayName() + " has joined " + g.getName());
    }

    public void onLeaveEvent(MemberLeaveEvent e){
        Member u = e.getMember().get();
        Guild g = e.getGuild().block();
        GuildConfig config = bot.getGuildConfig(g.getId().asLong());
        Utility.sendChannelMessage(config.getAnnounceChannelID(), u.getDisplayName() + " has left  " + g.getName());
    }
    
    public void onUserPresenceChange(PresenceUpdateEvent e){
        e.getMember().subscribe(member -> {
                member.getGuild().subscribe( guild -> {
                    McUser user = UserManager.loadUserFromFile(member.getId().asLong());
                    if (user != null) {
                        String displayName = user.getDisplayName(guild.getId().asLong());
                        if(displayName == null) {
                        
                        }
                        GuildConfig config = bot.getGuildConfig(guild.getId().asLong());
                        if (config.isReportStatusChange()) {
                            String message;
                            switch (e.getCurrent().getStatus()) {
                                case ONLINE:
                                    message = " has come online.";
                                    break;
                                case IDLE:
                                    message = " has changed to idle";
                                    break;
                                case DO_NOT_DISTURB:
                                    message = " has asked not to be disturbed";
                                    break;
                                case OFFLINE:
                                    message = " is now offline,";
                                    break;
                                default:
                                    message = null;
                            }
                            Long channelID = config.getModChannelID();
                            bot.client.getChannelById(Snowflake.of(channelID)).subscribe(channel -> {
                                if (channel instanceof MessageChannel) {
                                    SimpleBot.log.info(guild.getName() + ": " + member.getDisplayName() + message);
                                    ((MessageChannel) channel).createMessage(member.getDisplayName() + ' ' + message).subscribe();
                                }
                            });
                        }
                    } else {
                        SimpleBot.log.info("Unknown User on Guild Channel: "+guild.getName());
                        user = new McUser(member.getId());
                        UserManager.checkUserDisplayName(user, guild);
                        UserManager.saveUser(user);
                    }
                });

            });
        }
    }