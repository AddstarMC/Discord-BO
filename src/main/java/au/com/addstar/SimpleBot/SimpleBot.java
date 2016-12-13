package au.com.addstar.SimpleBot;

import au.com.addstar.SimpleBot.http.InviteHandler;
import au.com.addstar.SimpleBot.listeners.CommandListener;
import au.com.addstar.SimpleBot.listeners.ManagementListener;
import au.com.addstar.SimpleBot.objects.GuildConfig;
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
    public static HttpServer server;
    public static final Logger log = LoggerFactory.getLogger(SimpleBot.class);


    public SimpleBot(IDiscordClient client) {
        SimpleBot.client = client;
        gConfigs = new HashMap<>();
    }

    public static void main(String[] args) {
        config = Configuration.loadConfig();
        instance = login(config.getProperty("discordToken"));
        configureListeners();
        server = createHttpServer();
        addContexts();
        server.setExecutor(null);
        server.start();
        log.info("HttpServer started on " + server.getAddress().getHostString() +":"+ server.getAddress().getPort());
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

    public static HttpServer createHttpServer(){
        HttpServer server =null;
        String host = config.getProperty("hostnameIP","localhost");
        Integer port = Integer.parseInt(config.getProperty("httpPort","22000"));
        try {
            InetAddress ip = InetAddress.getByName(host);
            InetSocketAddress socketAddress = new InetSocketAddress(ip,port);
            server = HttpServer.create(socketAddress, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return server;
    }

    private static void addContexts(){
        server.createContext("/invite/", new InviteHandler());
    }
}
