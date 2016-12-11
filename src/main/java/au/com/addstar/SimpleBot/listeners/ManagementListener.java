package au.com.addstar.SimpleBot.listeners;

import au.com.addstar.SimpleBot.SimpleBot;
import au.com.addstar.SimpleBot.objects.GuildConfig;
import au.com.addstar.SimpleBot.ulilities.Utility;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.PresenceUpdateEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Presences;

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
        IGuild g = event.getGuild();
        GuildConfig config = SimpleBot.gConfigs.get(g.getID());
        Utility.sendPrivateMessage(u, config.getWelcomeMessage());
        Utility.sendChannelMessage(config.getAnnounceChannelID(), u.getDisplayName(g) + " has joined " + g.getName());
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
            GuildConfig config = SimpleBot.gConfigs.get(g.getID());
            String channelID =config.getModChannelID();
            Boolean report = config.isReportStatusChange();
            if(channelID != null && channelID.length()>0 && report) {
                Utility.sendChannelMessage(channelID, u.getDisplayName(g) + message);
            }else{
                SimpleBot.log.info(g.getName() + ": " + u.getDisplayName(g) + message);
            }
        }
    }






}
