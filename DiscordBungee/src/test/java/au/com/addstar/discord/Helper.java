package au.com.addstar.discord;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 21/01/2017.
 */
class Helper {


    static DiscordBungee createPlugin(){
        DiscordBungee plugin =  new DiscordBungee();
        plugin.instance = plugin;
        plugin.log = Logger.getAnonymousLogger();
        return plugin;
    }

    public static DiscordBungee loadConfig(DiscordBungee plugin){
        plugin.loadConfig(new File("test"),"config.yml");
        return plugin;
    }


}
