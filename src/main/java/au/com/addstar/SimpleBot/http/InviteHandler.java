package au.com.addstar.SimpleBot.http;

import au.com.addstar.SimpleBot.SimpleBot;
import au.com.addstar.SimpleBot.objects.GuildConfig;
import au.com.addstar.SimpleBot.objects.Invitation;
import au.com.addstar.SimpleBot.ulilities.Utility;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IInvite;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 11/12/2016.
 */
public class InviteHandler implements HttpHandler {

    public InviteHandler(){

    }
    @Override
    public void handle(HttpExchange t) throws IOException {
        SimpleBot.log.debug("InviteRequest received from "  + t.getRemoteAddress());
        int responseCode = 200;
        String response = null;
        String requestPath = t.getRequestURI().getPath();
        String[] path = requestPath.split("/");
        String guildName = path[2];
        String channelName = path[3];
        UUID uuid = Utility.StringtoUUID(path[4]);
        String user = path[5];
        List<IGuild> guilds = SimpleBot.client.getGuilds();
        String guildID = null;
        for (IGuild guild : guilds){
            if (guild.getName().equals(guildName)){
                guildID = guild.getID();
            }
        }
        if(guildID != null) {
            String pendingInvitation = checkInviteforUser(uuid,guildID);
            List<IChannel> channels = SimpleBot.client.getGuildByID(guildID).getChannelsByName(channelName);
            IChannel channel;
            IInvite invite;
            if(channels.size() == 1){
                channel = channels.get(0);
                invite = Utility.checkforInvite(channel,pendingInvitation);
                if(invite == null) {
                    removeInvitation(pendingInvitation, guildID);
                    invite = Utility.createInvite(channel, 30 * 60, 1, false);
                    if (invite != null) {
                        Long expiryTime = System.currentTimeMillis() + (30 * 60 * 1000);
                        response = invite.getInviteCode();
                        SimpleBot.log.info("Invite Code Generated for " + user + " : " + response);
                        storeInvitation(uuid, user, expiryTime, invite, guildID);
                    } else {
                        responseCode = 449;
                        response = channel.getName() + " invite generation failed";
                    }
                }else{
                    response = invite.getInviteCode();
                }
            }else{
                responseCode = 449;
                response = channels.size() + " channels found matching " + channelName;
            }

        }else{
            responseCode = 449;
            response = "No Guilds found matching " + guildName;
        }
        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }



    private Invitation storeInvitation(UUID uuid, String displayName, Long expiryTime, IInvite inv, String guildID){
        Invitation i =  new Invitation(uuid, displayName, expiryTime);
        GuildConfig config = SimpleBot.gConfigs.get(guildID);
        return config.storeInvite(i,inv);
    }

    private void removeInvitation(String code, String guildID){
        GuildConfig config = SimpleBot.gConfigs.get(guildID);
        config.removeInvite(code);
    }

    private String checkInviteforUser(UUID uuid,String guildID){
        GuildConfig config = SimpleBot.gConfigs.get(guildID);
        return config.checkForUUIDInvite(uuid);
    }


}
