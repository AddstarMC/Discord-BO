package au.com.addstar.SimpleBot;

import java.io.*;
import java.util.Properties;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 7/12/2016.
 */
class Configuration {
    private static Properties defaultProps;
    private static final File config = new File("config.properties");

    public static Properties loadConfig(){
        InputStream input = Configuration.class.getResourceAsStream("/config.properties");
        defaultProps = new Properties();
        try{
            defaultProps.load(input);
            input.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        Properties prop = new Properties();
        try {
            if (config.exists()) {
                InputStream finput = new FileInputStream(config);
                prop.clear();
                prop.load(finput);
                finput.close();
            } else {
                createConfig();
                prop = defaultProps;
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return prop;
    }
    private static void createConfig(){
        try {
            if (config.createNewFile()) {
                OutputStream out = new FileOutputStream(config);
                defaultProps.store(out, "Default Configurations");
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties reloadConfig(){
        Properties prop = new Properties();
        try {
            File configFile = new File("config.properties");
            if (configFile.exists()) {
                InputStream input = new FileInputStream(configFile);
                prop.clear();
                prop.load(input);
                input.close();
            } else {
                OutputStream out = new FileOutputStream(configFile);
                defaultProps.store(out, "Default Config");
                prop = defaultProps;
                out.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return prop;
    }

}
