package au.com.addstar.discord;

        import au.com.addstar.discord.messages.identifiers.CommandType;
        import au.com.addstar.discord.redis.RedisManager;
        import au.com.addstar.bc.BungeeChat;

        import au.com.addstar.discord.redisHandlers.UpdatePlayerCommandHandler;
        import com.google.common.io.ByteStreams;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.util.logging.Logger;

        import net.cubespace.geSuit.geSuit;
        import net.md_5.bungee.api.plugin.Plugin;
        import net.md_5.bungee.config.Configuration;
        import net.md_5.bungee.config.ConfigurationProvider;
        import net.md_5.bungee.config.YamlConfiguration;
/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 24/12/2016.
 **/

public class DiscordBungee extends Plugin {
    public static DiscordBungee instance;
    public Configuration config ;
    public Logger log;
    public boolean gHooked;
    private boolean redisEnabled;
    private geSuit gPlugin;
    public BungeeChat bcplugin;
//    DiscordComServer server;

    RedisManager redisManager;

    public DiscordBungee() {
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        this.loadConfig(this.getDataFolder(),"config.yml");
        log = this.getProxy().getLogger();
        Plugin p = this.getProxy().getPluginManager().getPlugin("geSuit");
        if (p != null){
            gPlugin = (geSuit) p;
            gHooked = true;
        }
        p = this.getProxy().getPluginManager().getPlugin("BungeeChat");
        if (p != null){
            bcplugin = (BungeeChat) p;
        }
        ILog iLog  = new BungeeLog(log);
        redisManager = new RedisManager(config.get("bungeeId",null),config.getString("discordId",null),iLog);
        redisEnabled = redisManager.initialize(config.getString("redisHost", "localhost"),config.getInt("redisPort",6379),config.getString("redisPassword"));
        if(redisEnabled){
            redisManager.registerCommandHandler(new UpdatePlayerCommandHandler(this), CommandType.UpdatePlayer);
        }else {
            log.info("Redis could not be configured please check you logs and configuration options");
        }


    }
    @Override
    public void onDisable() {
        super.onDisable();
        if (redisEnabled)redisManager.terminate();
    }

    void loadConfig(File path, String fileName) {
        File configFile = new File(path,fileName);
        if(!configFile.exists()) {
            try {
                path.mkdirs();
                configFile.createNewFile();
                InputStream ex = this.getResourceAsStream(fileName);
                Throwable se2 = null;

                try {
                    FileOutputStream os = new FileOutputStream(configFile);
                    Throwable se1 = null;

                    try {
                        ByteStreams.copy(ex, os);
                        log.info("Config file created!");
                    } catch (Throwable e1) {
                        se1 = e1;
                        throw e1;
                    } finally {
                        if(os != null) {
                            if(se1 != null) {
                                try {
                                    os.close();
                                } catch (Throwable e2) {
                                    se1.addSuppressed(e2);
                                }
                            } else {
                                os.close();
                            }
                        }

                    }
                } catch (Throwable e) {
                    se2 = e;
                    throw e;
                } finally {
                    if(ex != null) {
                        if(se2 != null) {
                            try {
                                ex.close();
                            } catch (Throwable e2) {
                                se2.addSuppressed(e2);
                            }
                        } else {
                            ex.close();
                        }
                    }

                }
            } catch (IOException e) {
                log.severe("Unable to create configuration file!");
                log.severe(path.getAbsolutePath());
                log.severe(configFile.getAbsolutePath());
                e.printStackTrace();

            }
        }

        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void saveConfig(File path, String fileName) {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, new File(path,fileName ));
        } catch (IOException e) {
            this.getLogger().severe("Error while saving files!");
            e.printStackTrace();
        }

    }
}
