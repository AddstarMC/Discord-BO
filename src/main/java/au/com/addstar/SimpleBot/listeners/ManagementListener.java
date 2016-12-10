package au.com.addstar.SimpleBot.listeners;

import au.com.addstar.SimpleBot.SimpleBot;
import au.com.addstar.SimpleBot.objects.GuildConfig;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.InviteReceivedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.impl.obj.Guild;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/12/2016.
 */
public class ManagementListener {

    private SimpleBot bot;

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
            bot.gConfigs.put(id, config);
        }
    }

    public void onJoinEvent(UserJoinEvent event){
        IUser u = event.getUser();
        LocalDateTime time = event.getJoinTime();
        IGuild g = event.getGuild();
        GuildConfig config = bot.gConfigs.get(g.getID());

    }





}
