package au.com.addstar.discord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 20/01/2017.
 */
public class DiscordBungeeTest {

    private DiscordBungee plugin;
    private File testPath;

    @Before
    public void Setup(){
        plugin =  Helper.createPlugin();
        testPath = new File("test");
        if(testPath.exists())testPath.delete();

    }
    @After
    public void tearDown(){
        File testConfig = new File(testPath,"config.yml");
        if(testConfig.exists())testConfig.delete();
        testPath.deleteOnExit();
        new File(testPath,"config.yml").delete();
    }


    @Test
    public void loadConfigTest(){
        assertNull(plugin.config);
        plugin.loadConfig(testPath, "config.yml");
        assertEquals(plugin.config.get("discordComBindPort"),0);
        plugin.config.set("discordComBindPort",10);
        assertEquals(plugin.config.get("discordComBindPort"),10);
        plugin.saveConfig(testPath, "config.yml");
        plugin.config = null;
        plugin.loadConfig(testPath,"config.yml");
        assertEquals(plugin.config.get("discordComBindPort"),10);
    }

}
