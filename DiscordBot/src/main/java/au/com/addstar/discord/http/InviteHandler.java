package au.com.addstar.discord.http;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.managers.InvitationManager;
import au.com.addstar.discord.objects.GuildConfig;
import au.com.addstar.discord.objects.Invitation;
import au.com.addstar.discord.ulilities.Utility;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.discordjson.json.ChannelData;
import discord4j.discordjson.json.InviteData;
import discord4j.discordjson.json.UserGuildData;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.entity.RestGuild;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.http.HttpStatus;
import reactor.core.publisher.Mono;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static au.com.addstar.discord.managers.InvitationManager.checkforInvite;
import static au.com.addstar.discord.managers.InvitationManager.createInvite;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 11/12/2016.
 */
public class InviteHandler implements HttpHandler {

    public InviteHandler(){

    }
    @Override
    public void handle(HttpExchange t) throws IOException {
        SimpleBot.log.debug("InviteRequest received from " + t.getRemoteAddress());
        final AtomicInteger responseCode = new AtomicInteger(HttpStatus.SC_BAD_REQUEST);
        Map<String, String> responsebuilder = new HashMap<>();
        final AtomicReference<String> response = new AtomicReference<>();
        List<String> contentHeaderList = new ArrayList<>();
        String requestPath = t.getRequestURI().getPath();
        String[] path = requestPath.split("/");

        if (path.length < 6) {
            contentHeaderList.add("application/text");
            Utilities.doResponse(t, responseCode.get(), contentHeaderList, "Must have 5 parts");
            return;
        }
        Long guildName = Long.parseLong(path[2]);
        String channelName = path[3];
        UUID uuid = Utility.StringtoUUID(path[4]);
        String user = path[5];
        Mono<Guild> guild;
        try {
            guild = Utilities.getGuildByID(guildName).single();
        }catch (NoSuchElementException e) {
            responseCode.set(HttpStatus.SC_BAD_REQUEST);
            contentHeaderList.add("application/text");
            response.set("No Guilds found matching " + guildName);
            Utilities.doResponse(t, responseCode.get(), contentHeaderList, response.get());
            return;
        }
        guild.subscribe(new Consumer<Guild>() {
            @Override
            public void accept(Guild guild) {
                GuildConfig config = SimpleBot.instance.getGuildConfig(guild.getId().asLong());
                final Invitation pendingInvitation = InvitationManager.checkForUUIDInvite(config, uuid);
                if (pendingInvitation != null) {
                    if (!pendingInvitation.getUserName().equals(user)) {
                        SimpleBot.log.info("Pending Invite Code: " + pendingInvitation.getInviteCode() + " was updated with a new username: " + user + " Old user:" + pendingInvitation.getUserName());
                        pendingInvitation.setUserName(user);
                    }
                    responseCode.set(HttpStatus.SC_OK);
                    responsebuilder.put("url", "https://discord.gg/" + pendingInvitation.getInviteCode());
                    responsebuilder.put("cmd", config.getPrefix() + "register " + pendingInvitation.getInviteCode());
                    SimpleBot.log.info("Stored Invite will be returned for " + pendingInvitation.getUserName() + " : " + pendingInvitation.getInviteCode());
                } else { //generate a new invite.
                    guild.getChannelById(Snowflake.of(channelName)).subscribe(new Consumer<GuildChannel>() {
                        @Override
                        public void accept(GuildChannel guildChannel) {
                            g
                        }
                    })
                }
            }
        })
        int expiry = config.getExpiryTime();
        final Invitation pendingInvitation = InvitationManager.checkForUUIDInvite(config, uuid);
        if (pendingInvitation != null) {
            if (!pendingInvitation.getUserName().equals(user)) {
                SimpleBot.log.info("Pending Invite Code: " + pendingInvitation.getInviteCode() + " was updated with a new username: " + user + " Old user:" + pendingInvitation.getUserName());
                pendingInvitation.setUserName(user);
            }
            responseCode.set(HttpStatus.SC_OK);
            responsebuilder.put("url", "https://discord.gg/" + pendingInvitation.getInviteCode());
            responsebuilder.put("cmd", config.getPrefix() + "register " + pendingInvitation.getInviteCode());
            SimpleBot.log.info("Stored Invite will be returned for " + pendingInvitation.getUserName() + " : " + pendingInvitation.getInviteCode());

        } else {

        }
        SimpleBot.client.getGuildById(Snowflake.of(guild.id())).getChannels().filter(
              new Predicate<ChannelData>() {
                  @Override
                  public boolean test(ChannelData channelData) {
                      return channelData.name().get().equals(channelName);
                  }
              }).subscribe(new Consumer<ChannelData>() {
            @Override
            public void accept(ChannelData channelData) {
                if (channelData == null) {
                    responseCode.set(HttpStatus.SC_BAD_REQUEST);
                    response.set("0 channels found matching " + channelName);
                } else {
                    RestChannel channel = SimpleBot.gatewayDiscordClient.getRestClient().getChannelById(Snowflake.of(channelData.id()))
                    InviteData data = checkforInvite(channel,pendingInvitation);
                }
            }
        })
        IChannel channel = Utilities.getChannelbyName(guild, channelName);
        if (channel == null) {
            responseCode = HttpStatus.SC_BAD_REQUEST;
            response = channels.size() + " channels found matching " + channelName;
        } else {
            IInvite invite ;
            if (pendingInvitation != null) {
                invite = checkforInvite(channel, pendingInvitation);
                if (invite == null) {
                    InvitationManager.removeInvitation(config, pendingInvitation.getInviteCode());
                    SimpleBot.log.info("Discord Invite associated with stored Invite had expired.  Detail:" + pendingInvitation.getUserName() + " : " + pendingInvitation.getInviteCode());
                } else {
                    Utilities.doJsonResponse(t, responseCode, responsebuilder);
                    return;
                }
            }

            SimpleBot.log.info("Generating new invite for " + user);
            invite = createInvite(channel, expiry, 1, false);
            if (invite != null) {
                Long expiryTime = System.currentTimeMillis() + (120 * 60 * 1000);
                responseCode = HttpStatus.SC_OK;
                responsebuilder.put("url", "https://discord.gg/" + invite.getCode());
                responsebuilder.put("cmd", config.getPrefix() + "register " + invite.getCode());
                Utilities.doJsonResponse(t, responseCode, responsebuilder);
                SimpleBot.log.info("Invite Code Generated for " + user + " : " + invite.getCode());
                InvitationManager.storeInvitation(config,new Invitation(uuid, user, expiryTime, invite.getCode()));
                return;
            } else {
                responseCode = HttpStatus.SC_BAD_REQUEST;
                contentHeaderList.add("application/text");
                response = channel.getName() + " invite generation failed";
            }
        }
        Utilities.doResponse(t, responseCode, contentHeaderList, response);
    }



}
