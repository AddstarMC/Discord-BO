package au.com.addstar.discord;



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
    public Configuration config;
    public Logger log;
    public boolean gHooked;
    public geSuit gPlugin;

    public DiscordBungee() {
    }

    public void onLoad() {
        super.onLoad();
    }

    public void onEnable() {
        super.onEnable();
        instance = this;
        this.loadConfig();
        log = this.getProxy().getLogger();
        Plugin p = this.getProxy().getPluginManager().getPlugin("geSuit");
        if (p != null){
            gPlugin = (geSuit) p;
            gHooked = true;
        }
    }

    public void onDisable() {
        super.onDisable();
    }

    private void loadConfig() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
                InputStream ex = this.getResourceAsStream("config.yml");
                Throwable se2 = null;

                try {
                    FileOutputStream os = new FileOutputStream(configFile);
                    Throwable se1 = null;

                    try {
                        ByteStreams.copy(ex, os);
                        this.getLogger().info("Config file created!");
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
                this.getLogger().severe("Unable to create configuration file!");
            }
        }

        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, new File(this.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            this.getLogger().severe("Error while saving files!");
            e.printStackTrace();
        }

    }
}
