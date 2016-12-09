package au.com.addstar.SimpleBot.objects;

import sx.blah.discord.handle.impl.obj.Message;

import java.io.*;
import java.util.Properties;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/12/2016.
 */

public class GuildConfig {

        private String id;
        private String prefix;
        private String welcomeMessage;

        public GuildConfig(String id){
                this.id = id;
                prefix = "!!";
                welcomeMessage = "";
                loadConfig();
        }



        public String getWelcomeMessage() {
                return welcomeMessage;
        }

        public void setWelcomeMessage(String welcomeMessage) {
                this.welcomeMessage = welcomeMessage;
        }

        public String getPrefix() {
                return prefix;
        }

        public void setPrefix(String prefix) {
                this.prefix = prefix;
        }

        public void loadConfig(){
                File parent = new File("/guilds");
                if(!parent.exists()){
                        parent.mkdir();
                }
                File config = new File(parent, id+".properties");
                Properties prop = new Properties();
                prop.setProperty("welcomeMessage",welcomeMessage);
                prop.setProperty("prefix",prefix);
                try {
                        if (config.exists()) {
                                InputStream finput = new FileInputStream(config);
                                prop.clear();
                                prop.load(finput);
                                finput.close();
                        } else {
                                if (config.createNewFile()) {
                                        OutputStream out = new FileOutputStream(config);
                                        prop.store(out, "Default Configurations");
                                        out.close();
                                }
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }
                welcomeMessage = prop.getProperty("welcomeMessage","");
                prefix = prop.getProperty("prefix","!!");
        }

        public void saveConfig(){
                File config = new File("/guilds/"+id+".properties");
                Properties prop = new Properties();
                prop.setProperty("welcomeMessage",welcomeMessage);
                prop.setProperty("prefix",prefix);
                try {
                        if (!config.exists()) {
                               config.createNewFile();
                        }
                        OutputStream out = new FileOutputStream(config);
                        prop.store(out, "Configurations: Server " + id);
                        out.close();
                } catch (FileNotFoundException e){

                        }catch (IOException e){

                }
        }

}
