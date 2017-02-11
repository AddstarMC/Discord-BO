package au.com.addstar.discord;

import au.com.addstar.discord.listeners.CommandListener;
import au.com.addstar.discord.listeners.ManagementListener;
import au.com.addstar.discord.objects.Guild;
import au.com.addstar.discord.objects.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 7/12/2016.
 */
public class SimpleBot {

    private static SimpleBot instance;
    public static IDiscordClient client;
    private static Properties config;
    public static HashMap<String,Guild> guilds;
    public static final Logger log = LoggerFactory.getLogger(SimpleBot.class);


    private SimpleBot(IDiscordClient client) {
        SimpleBot.client = client;
        guilds = new HashMap<>();
    }

    public static void main(String[] args) {
        config = Configuration.loadConfig();
        String token = config.getProperty("discordToken");
        if(token==null){
            SimpleBot.log.info("Server shut down initiated...");
            SimpleBot.log.info("You must edit the config.properties file and add your discord app private token.");
            System.exit(1);
        }
        instance = login(config.getProperty("discordToken"));
        configureListeners();
/*      server = createHttpServer();
        addContexts(server);
        server.setExecutor(null);
        server.start();*/

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                close();
        }, "Shutdown-thread"));
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
        ManagementListener mListen = new ManagementListener();
        CommandListener cListen = new CommandListener();
        client.getDispatcher().registerListener(mListen);
        client.getDispatcher().registerListener(cListen);
        log.info("Listeners are configured.");
    }

    public static void exit(){
        System.exit(0);
    }


    private static void close() {
        SimpleBot.log.info("Server shut down initiated...");
        SimpleBot.log.info("Saving Guild Configs");
        for(Map.Entry<String,Guild> entry : SimpleBot.guilds.entrySet()){
            GuildConfig guildconfig = entry.getValue().getConfig();
            guildconfig.saveConfig();
        }
        SimpleBot.log.info("GuildConfigs saved.");
    }

}
