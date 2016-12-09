package au.com.addstar.SimpleBot.objects;

import sx.blah.discord.handle.impl.obj.Guild;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 10/12/2016.
 */
public class BotGuild {

    private String id;
    private GuildConfig config;

    public BotGuild(Guild guild,GuildConfig config){
        this.id = guild.getID();
        this.config = config;
    }

    public GuildConfig getConfig() {
        return config;
    }
}
