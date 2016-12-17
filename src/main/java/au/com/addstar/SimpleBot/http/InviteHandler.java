package au.com.addstar.SimpleBot.http;

import au.com.addstar.SimpleBot.SimpleBot;
import au.com.addstar.SimpleBot.objects.GuildConfig;
import au.com.addstar.SimpleBot.objects.Invitation;
import au.com.addstar.SimpleBot.ulilities.Utility;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.http.HttpStatus;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IInvite;

import java.io.IOException;
import java.util.*;

import static au.com.addstar.SimpleBot.ulilities.Utility.checkforInvite;
import static au.com.addstar.SimpleBot.ulilities.Utility.createInvite;

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
        int responseCode = HttpStatus.SC_BAD_REQUEST;
        Map<String, String> responsebuilder = new HashMap<>();
        String response = null;
        List<String> contentHeaderList = new ArrayList<>();
        String requestPath = t.getRequestURI().getPath();
        String[] path = requestPath.split("/");

        if (path.length < 6) {
            contentHeaderList.add("application/text");
            Utilities.doResponse(t, responseCode, contentHeaderList, "Must have 5 parts");
            return;
        }
        String guildName = path[2];
        String channelName = path[3];
        UUID uuid = Utility.StringtoUUID(path[4]);
        String user = path[5];
        IGuild guild = Utilities.getGuildbyName(guildName);
        if (guild == null) {
            responseCode = HttpStatus.SC_BAD_REQUEST;
            contentHeaderList.add("application/text");
            response = "No Guilds found matching " + guildName;
            Utilities.doResponse(t, responseCode, contentHeaderList, response);
            return;
        }
        GuildConfig config = SimpleBot.gConfigs.get(guild.getID());
        int expiry = config.getExpiryTime();
        Invitation pendingInvitation = checkInviteforUser(uuid, guild.getID());
        if (pendingInvitation != null && pendingInvitation.hasExpired()) {
            SimpleBot.log.info("Pending Invite Code: " + pendingInvitation.getInviteCode() + "had expired and is being removed. Expiry: " + Utility.getDate(pendingInvitation.getExpiryTime()));
            config.removeInvitation(pendingInvitation.getInviteCode());
            pendingInvitation = null;
        } else if (pendingInvitation != null) {
            if (!pendingInvitation.getUserName().equals(user)) {
                SimpleBot.log.info("Pending Invite Code: " + pendingInvitation.getInviteCode() + " was updated with a new username: " + user + " Old user:" + pendingInvitation.getUserName());

                pendingInvitation.setUserName(user);
            }
            responseCode = HttpStatus.SC_OK;
            responsebuilder.put("url", "https://discord.gg/" + pendingInvitation.getInviteCode());
            responsebuilder.put("cmd", config.getPrefix() + "register " + pendingInvitation.getInviteCode());
            SimpleBot.log.info("Stored Invite will be returned for " + pendingInvitation.getUserName() + " : " + pendingInvitation.getInviteCode());

        }
        List<IChannel> channels = SimpleBot.client.getGuildByID(guild.getID()).getChannelsByName(channelName);
        IChannel channel = Utilities.getChannelbyName(guild, channelName);
        if (channel == null) {
            responseCode = HttpStatus.SC_BAD_REQUEST;
            response = channels.size() + " channels found matching " + channelName;
        } else {
            IInvite invite = null;
            if (pendingInvitation != null) {
                invite = checkforInvite(channel, pendingInvitation);
                if (invite == null) {
                    config.removeInvitation(pendingInvitation.getInviteCode());
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
                responsebuilder.put("url", "https://discord.gg/" + invite.getInviteCode());
                responsebuilder.put("cmd", config.getPrefix() + "register " + invite.getInviteCode());
                Utilities.doJsonResponse(t, responseCode, responsebuilder);
                SimpleBot.log.info("Invite Code Generated for " + user + " : " + invite.getInviteCode());
                storeInvitation(uuid, user, expiryTime, invite.getInviteCode(), config);
                return;
            } else {
                responseCode = HttpStatus.SC_BAD_REQUEST;
                contentHeaderList.add("application/text");
                response = channel.getName() + " invite generation failed";
            }
        }
        Utilities.doResponse(t, responseCode, contentHeaderList, response);
    }

    private void storeInvitation(UUID uuid, String displayName, Long expiryTime, String invitecode,GuildConfig config){
        Invitation inv =  new Invitation(uuid, displayName, expiryTime, invitecode);
        config.storeInvitation(inv);
    }

    private Invitation checkInviteforUser(UUID uuid,String guildID){
        GuildConfig config = SimpleBot.gConfigs.get(guildID);
        return config.checkForUUIDInvite(uuid);
    }


}
