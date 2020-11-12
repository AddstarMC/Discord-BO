package au.com.addstar.discord.listeners;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.managers.UserManager;
import au.com.addstar.discord.objects.GuildConfig;
import au.com.addstar.discord.objects.McUser;
import au.com.addstar.discord.ulilities.Utility;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.entity.RestChannel;

import java.util.Set;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/12/2016.
 */
public class ManagementListener {

    private final SimpleBot bot = SimpleBot.instance;

    public ManagementListener(final GatewayDiscordClient gatewayClient) {
        gatewayClient.on(ReadyEvent.class)
                .subscribe(this::onReadyEvent);
        gatewayClient.on(MemberJoinEvent.class)
                .subscribe(this::onJoinEvent);
        gatewayClient.on(MemberLeaveEvent.class)
                .subscribe(this::onLeaveEvent);
        gatewayClient.on(PresenceUpdateEvent.class).subscribe(
                this::onUserPresenceChange);
    }


    private void onReadyEvent(ReadyEvent event) {
        Set<ReadyEvent.Guild> guilds = event.getGuilds();
        for (ReadyEvent.Guild g : guilds) {
            long id = g.getId().asLong();
            GuildConfig config = new GuildConfig(id);
            bot.addGuild(id, config);
        }

        UserManager.initialize(SimpleBot.client);
    }

    private void onJoinEvent(MemberJoinEvent event) {
        Member u = event.getMember();
        event.getGuild()
                .subscribe(guild -> {
                    GuildConfig config = bot.getGuildConfig(guild.getId().asLong());
                    McUser user = UserManager.loadUserFromFile(u.getId().asLong());
                    if (user == null) {
                        user = new McUser(u.getId());
                        UserManager.cacheUser(user);
                    }
                    Utility.sendPrivateMessage(u, config.getWelcomeMessage());
                    Utility.sendChannelMessage(config.getAnnounceChannelID(), u.getDisplayName() + " has joined " + guild.getName());
                });
    }

    private void onLeaveEvent(MemberLeaveEvent e) {
        e.getMember().ifPresent(member -> e.getGuild().subscribe(guild -> {
                    GuildConfig config = bot.getGuildConfig(guild.getId().asLong());
                    Utility.sendChannelMessage(config.getAnnounceChannelID(), member.getDisplayName() + " has left  " + guild.getName());
                }
        ));
    }

    private void onUserPresenceChange(PresenceUpdateEvent e) {
        e.getMember().subscribe(member -> member.getGuild().subscribe(guild -> {
            McUser user = UserManager.loadUserFromFile(member.getId().asLong());
            if (user != null) {
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
                    RestChannel channel = SimpleBot.client.getChannelById(Snowflake.of(channelID));
                    if (channel instanceof MessageChannel) {
                        SimpleBot.log.info(guild.getName() + ": " + member.getDisplayName() + message);
                        channel.createMessage(member.getDisplayName() + ' ' + message).subscribe();
                    }
                }
            } else {
                SimpleBot.log.info("Unknown User on Guild Channel: " + guild.getName());
                user = new McUser(member.getId());
                UserManager.checkUserDisplayName(user, guild);
                UserManager.saveUser(user);
            }
        }));
    }
}