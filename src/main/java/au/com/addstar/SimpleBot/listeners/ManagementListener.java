package au.com.addstar.SimpleBot.listeners;

import au.com.addstar.SimpleBot.SimpleBot;
import au.com.addstar.SimpleBot.objects.GuildConfig;
import au.com.addstar.SimpleBot.ulilities.Utility;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.*;

import sx.blah.discord.handle.obj.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/12/2016.
 */
public class ManagementListener {

    private final SimpleBot bot;

    public ManagementListener(SimpleBot bot){
        this.bot = bot;
    }


    @EventSubscriber
    public void onReadyEvent(ReadyEvent event){
        IDiscordClient client = event.getClient(); // Gets the client from the event object
        IUser ourUser = client.getOurUser();// Gets the user represented by the client
        for (IGuild guild : client.getGuilds()){
            String id = guild.getID();
            GuildConfig config  =  new GuildConfig(id);
            SimpleBot.gConfigs.put(id, config);
        }
    }

    @EventSubscriber
    public void onJoinEvent(UserJoinEvent event){
        IUser u = event.getUser();
        LocalDateTime time = event.getJoinTime();
        IGuild g = event.getGuild();
        GuildConfig config = SimpleBot.gConfigs.get(g.getID());
        Utility.sendPrivateMessage(u, config.getWelcomeMessage());
    }

    @EventSubscriber
    public void onUserPresenceChange(PresenceUpdateEvent e){
        Presences p = e.getNewPresence();
        IUser u = e.getUser();
        List<IGuild> guilds = e.getClient().getGuilds();
        List<IGuild> userGuilds = new ArrayList<>();
        for(IGuild g : guilds){
            if(g.getUserByID(u.getID())!=null){
                userGuilds.add(g);
            }
        }
        String message = "";
        switch(p){
            case ONLINE:
                message = " has come online.";
                break;
            case IDLE:
                message = " has changed to idle";
                break;
            case DND:
                message = " has asked not to be disturbed";
                break;
            case OFFLINE:
                message = " is now offline,";
                break;
            case STREAMING:
                message = " has started streaming";
        }
        for(IGuild g : userGuilds){

            String announceID = SimpleBot.gConfigs.get(g.getID()).getAnnounceChannelID();
            if(announceID != null && announceID.length()>0) {
                Utility.sendChannelMessage(announceID, u.getDisplayName(g) + message);
            }else{
                SimpleBot.log.info(SimpleBot.client.getGuildByID(g.getID()).getName() + " has no annouce channel configured.");
            }
        }
    }






}
