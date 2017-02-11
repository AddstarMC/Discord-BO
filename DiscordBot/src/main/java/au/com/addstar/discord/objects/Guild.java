package au.com.addstar.discord.objects;

import au.com.addstar.discord.BotLog;
import au.com.addstar.discord.ILog;
import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.messages.identifiers.CommandType;
import au.com.addstar.discord.redis.InviteHandler;
import au.com.addstar.discord.redis.RedisManager;

/**
 * Created for the Addstar MC Server Network
 * Created by Narimm on 17/01/2017.
 */
public class Guild {


    public final GuildConfig config;
    private boolean bungeeConnected;
    public final RedisManager redisManager;


    public Guild(GuildConfig config) {
        this.config = config;
        bungeeConnected = false;
        ILog log = new BotLog(SimpleBot.log);
        redisManager = new RedisManager(config.getId(),config.getBungeeId(), log);
        redisManager.initialize(config.getRedisHost(),config.getRedisPort(),config.getRedisPassword());
        if(redisManager.ping())bungeeConnected=true;
        redisManager.registerCommandHandler(new InviteHandler(this), CommandType.INVITE);
    }

    public GuildConfig getConfig() {
        return config;
    }

    public boolean isBungeeConnected() {
        return bungeeConnected;
    }

    public RedisManager getRedis(){
        return redisManager;
    }


}

