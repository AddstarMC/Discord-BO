package au.com.addstar.discord;

import au.com.addstar.discord.http.AnnouncerHandler;
import au.com.addstar.discord.http.DefaultHandler;
import au.com.addstar.discord.http.InviteHandler;
import au.com.addstar.discord.listeners.CommandListener;
import au.com.addstar.discord.listeners.ManagementListener;
import au.com.addstar.discord.objects.Guild;
import au.com.addstar.discord.objects.GuildConfig;
import au.com.addstar.discord.socketClient.BungeeComClient;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 7/12/2016.
 */
public class SimpleBot {

    public static SimpleBot instance;
    public static IDiscordClient client;
    public static Properties config;
    public static HashMap<String,Guild> guilds;
    static HttpServer server;
    public static final Logger log = LoggerFactory.getLogger(SimpleBot.class);


    public SimpleBot(IDiscordClient client) {
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
        server = createHttpServer();
        addContexts(server);
        server.setExecutor(null);
        server.start();
        log.info("HttpServer started on " + server.getAddress().getHostString() +":"+ server.getAddress().getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    close();
                } catch (DiscordException e) {
                    e.printStackTrace();
                }
            }
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

    static HttpServer createHttpServer(){
        HttpServer server =null;
        String host = config.getProperty("hostnameIP","localhost");
        Integer port = Integer.parseInt(config.getProperty("httpPort","22000"));
        try {
            InetAddress ip = InetAddress.getByName(host);
            InetSocketAddress socketAddress = new InetSocketAddress(ip,port);
            server = HttpServer.create(socketAddress, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return server;
    }

    private static void addContexts(HttpServer server){
        server.createContext("/", new DefaultHandler());
        server.createContext("/invite/", new InviteHandler());
        server.createContext("/announcer/", new AnnouncerHandler());
    }
    public static void exit(){
        System.exit(0);
    }


    private static void close() throws DiscordException {
        SimpleBot.log.info("Server shut down initiated...");
        SimpleBot.log.info("Saving Guild Configs");
        for(Map.Entry<String,Guild> entry : SimpleBot.guilds.entrySet()){
            GuildConfig guildconfig = entry.getValue().getConfig();
            guildconfig.saveConfig();
        }
        SimpleBot.log.info("GuildConfigs saved.");
    }



}
