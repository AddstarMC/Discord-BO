package au.com.addstar.SimpleBot;

import au.com.addstar.SimpleBot.listeners.CommandListener;
import au.com.addstar.SimpleBot.listeners.ManagementListener;
import au.com.addstar.SimpleBot.objects.GuildConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

import java.util.HashMap;
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
    public static final Logger log = LoggerFactory.getLogger(Discord4J.class);


    public SimpleBot(IDiscordClient client) {
        SimpleBot.client = client;
        gConfigs = new HashMap<>();
    }

    public static void main(String[] args) {
        config = Configuration.loadConfig();
        instance = login(config.getProperty("discordToken"));
        configureListeners();
    }

    private static SimpleBot login(String token) {
        ClientBuilder builder = new ClientBuilder(); // Creates a new client builder instance
        builder.withToken(token);// Sets the bot token for the client
        IDiscordClient c;
        try {
            c = builder.login(); // Builds the IDiscordClient instance and logs it in
            // Creating the bot instance
            return new SimpleBot(c);
        } catch (DiscordException e) { // Error occurred logging in
            e.printStackTrace();

        }
        return null;
    }

    private static void configureListeners() {
        ManagementListener mListen = new ManagementListener(instance);
        CommandListener cListen = new CommandListener(instance);
        client.getDispatcher().registerListener(mListen);
        client.getDispatcher().registerListener(cListen);
        log.info("Listeners are configured.");
    }

    public Logger getLog(){
        return log;
    }

}
