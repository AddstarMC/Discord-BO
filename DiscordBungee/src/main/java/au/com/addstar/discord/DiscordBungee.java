package au.com.addstar.discord;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 24/12/2016.
 */

        import com.google.common.io.ByteStreams;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import net.md_5.bungee.api.plugin.Plugin;
        import net.md_5.bungee.config.Configuration;
        import net.md_5.bungee.config.ConfigurationProvider;
        import net.md_5.bungee.config.YamlConfiguration;

public class DiscordBungee extends Plugin {
    public static DiscordBungee instance;
    public Configuration config;

    public DiscordBungee() {
    }

    public void onLoad() {
        super.onLoad();
    }

    public void onEnable() {
        super.onEnable();
        instance = this;
        this.loadConfig();
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
                Throwable var3 = null;

                try {
                    FileOutputStream os = new FileOutputStream(configFile);
                    Throwable var5 = null;

                    try {
                        ByteStreams.copy(ex, os);
                        this.getLogger().info("Config file created!");
                    } catch (Throwable var33) {
                        var5 = var33;
                        throw var33;
                    } finally {
                        if(os != null) {
                            if(var5 != null) {
                                try {
                                    os.close();
                                } catch (Throwable var32) {
                                    var5.addSuppressed(var32);
                                }
                            } else {
                                os.close();
                            }
                        }

                    }
                } catch (Throwable var35) {
                    var3 = var35;
                    throw var35;
                } finally {
                    if(ex != null) {
                        if(var3 != null) {
                            try {
                                ex.close();
                            } catch (Throwable var31) {
                                var3.addSuppressed(var31);
                            }
                        } else {
                            ex.close();
                        }
                    }

                }
            } catch (IOException var37) {
                this.getLogger().severe("Unable to create configuration file!");
            }
        }

        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException var30) {
            ;
        }

    }

    private void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, new File(this.getDataFolder(), "config.yml"));
        } catch (IOException var2) {
            this.getLogger().severe("Error while saving files!");
        }

    }
}
