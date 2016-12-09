package au.com.addstar.SimpleBot;

import au.com.addstar.SimpleBot.listeners.CommandListener;
import au.com.addstar.SimpleBot.listeners.ManagementListener;
import au.com.addstar.SimpleBot.objects.GuildConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 7/12/2016.
 */
public class SimpleBot {

    public static SimpleBot instance;
    public static IDiscordClient client;
    private static Properties config;
    public static HashMap<String,GuildConfig> gConfigs;

    private static final Logger logger = LogManager.getLogger("SimpleBot");

    public SimpleBot(IDiscordClient client) {
        configureListeners();
        gConfigs = new HashMap<>();
    }

    public static void main(String[] args) {
        config = Configuration.loadConfig();
        instance = login(config.getProperty("discordToken"));
    }

    public static SimpleBot login(String token) {
        ClientBuilder builder = new ClientBuilder(); // Creates a new client builder instance
        builder.withToken(token);// Sets the bot token for the client
        try {
            client = builder.login(); // Builds the IDiscordClient instance and logs it in
            // Creating the bot instance
        } catch (DiscordException e) { // Error occurred logging in
            e.printStackTrace();
        }
        SimpleBot bot = new SimpleBot(client);
        return bot;
    }

    private static void configureListeners() {
        ManagementListener mListen = new ManagementListener(instance);
        CommandListener cListen = new CommandListener(instance);
        client.getDispatcher().registerListener(mListen);
        client.getDispatcher().registerListener(cListen);
    }

    public static Logger getLogger() {
        return logger;
    }
}
