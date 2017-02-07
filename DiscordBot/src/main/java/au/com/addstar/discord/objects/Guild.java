package au.com.addstar.discord.objects;

import au.com.addstar.discord.redis.RedisManager;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 17/01/2017.
 */
public class Guild {


    public GuildConfig config;
    private boolean bungeeConnected;
    public RedisManager redisManager;


    public Guild(GuildConfig config) {
        this.config = config;
        bungeeConnected = false;
        redisManager = new RedisManager(config.getId());
        redisManager.initialize(config.getRedisHost(),config.getRedisPort(),config.getRedisPassword());
        if(redisManager.ping())bungeeConnected=true;
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

