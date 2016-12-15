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

        if(path.length <6) {
            contentHeaderList.add("application/text");
            Utilities.doResponse(t,responseCode,contentHeaderList, "Must have 5 parts");
            return;
        }
        String guildName = path[2];
        String channelName = path[3];
        UUID uuid = Utility.StringtoUUID(path[4]);
        String user = path[5];
        List<IGuild> guilds = SimpleBot.client.getGuilds();
        IGuild guild = null;
        int matches = 0;
        for (IGuild g : guilds) {
            if (g.getName().equals(guildName)) {
                matches++;
                guild = g;

            }
        }
        if(matches>1){
            responseCode = HttpStatus.SC_BAD_REQUEST;
            contentHeaderList.add("application/text");
            response = "Multiple Guilds found matching " + guildName;
            Utilities.doResponse(t,responseCode,contentHeaderList,response);
            return;
        }
        if (guild != null) {
            GuildConfig config = SimpleBot.gConfigs.get(guild.getID());
            Invitation pendingInvitation = checkInviteforUser(uuid, guild.getID());
            if (pendingInvitation != null && pendingInvitation.hasExpired()){
                config.removeInvite(pendingInvitation.getInviteCode());
                pendingInvitation = null;
            }else
                if(pendingInvitation != null){
                if(!pendingInvitation.getUserName().equals(user)){
                    pendingInvitation.setUserName(user);
                }
                responseCode = HttpStatus.SC_OK;
                responsebuilder.put("url","https://discord.gg/"+ pendingInvitation.getInviteCode());
                responsebuilder.put("cmd", config.getPrefix() + "register " + pendingInvitation.getInviteCode());
                Utilities.doJsonResponse(t,responseCode,responsebuilder);
                SimpleBot.log.info("Stored Invite returned for " + pendingInvitation.getUserName() + " : " + pendingInvitation.getInviteCode());
            }
            List<IChannel> channels = SimpleBot.client.getGuildByID(guild.getID()).getChannelsByName(channelName);
            IChannel channel;
            IInvite invite = null;
            if (channels.size() == 1) {
                channel = channels.get(0);
                if(pendingInvitation != null){
                    invite = checkforInvite(channel, pendingInvitation);
                    if (invite == null) config.removeInvite(pendingInvitation.getInviteCode());
                }
                if (invite == null) {
                    invite = createInvite(channel, 30 * 60, 1, false);
                    if (invite != null) {
                        Long expiryTime = System.currentTimeMillis() + (30 * 60 * 1000);
                        responseCode = HttpStatus.SC_OK;
                        responsebuilder.put("url","https://discord.gg/"+ invite.getInviteCode());
                        responsebuilder.put("cmd", config.getPrefix() + "register " + invite.getInviteCode());
                        Utilities.doJsonResponse(t,responseCode,responsebuilder);
                        SimpleBot.log.info("Invite Code Generated for " + user + " : " + invite.getInviteCode());
                        storeInvitation(uuid, user, expiryTime, invite.getInviteCode(),config);
                    } else {
                        responseCode = HttpStatus.SC_BAD_REQUEST;
                        contentHeaderList.add("application/text");
                        response = channel.getName() + " invite generation failed";
                    }
                }else{
                    response = invite.getInviteCode();
                }
            }else{
                responseCode = HttpStatus.SC_BAD_REQUEST;
                response = channels.size() + " channels found matching " + channelName;
            }

            } else {
                responseCode = HttpStatus.SC_BAD_REQUEST;
                contentHeaderList.add("application/text");
                response = "No Guilds found matching " + guildName;
            }
            Utilities.doResponse(t,responseCode,contentHeaderList,response);
        }

    private void storeInvitation(UUID uuid, String displayName, Long expiryTime, String invitecode,GuildConfig config){
        Invitation inv =  new Invitation(uuid, displayName, expiryTime, invitecode);
        config.storeInvite(inv);
    }

    private Invitation checkInviteforUser(UUID uuid,String guildID){
        GuildConfig config = SimpleBot.gConfigs.get(guildID);
        return config.checkForUUIDInvite(uuid);
    }


}
