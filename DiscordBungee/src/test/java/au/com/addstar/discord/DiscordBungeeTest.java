package au.com.addstar.discord;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

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
