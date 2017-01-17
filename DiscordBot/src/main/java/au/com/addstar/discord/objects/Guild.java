package au.com.addstar.discord.objects;

import au.com.addstar.discord.socketClient.BungeeComClient;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 17/01/2017.
 */
public class Guild {


    public GuildConfig config;
    private boolean bungeeConnected;
    public BungeeComClient client;


    public Guild(GuildConfig config) {
        this.config = config;
        bungeeConnected = false;
        if (config.getGuildMcHost() != null && config.getGuildPort() != null){
            try {
                client = new BungeeComClient(config.getGuildMcHost(), config.getGuildPort());
                bungeeConnected = true;
            }catch (Exception e){
                e.printStackTrace();
                bungeeConnected = false;
            }
        }
    }

    public GuildConfig getConfig() {
        return config;
    }

    public boolean isBungeeConnected() {
        return bungeeConnected;
    }

    public BungeeComClient getClient() {
        return client;
    }
}

