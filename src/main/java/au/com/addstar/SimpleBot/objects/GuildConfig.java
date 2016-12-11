package au.com.addstar.SimpleBot.objects;

import java.io.*;
import java.util.Properties;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/12/2016.
 */

public class GuildConfig {

        private final String id;
        private String prefix;
        private String welcomeMessage;
        private String announceChannelID;
        private String modChannelID;
        private Boolean reportStatusChange;

        public GuildConfig(String id){
                this.id = id;
                prefix = "!!";
                welcomeMessage = "";
                announceChannelID = "";
                modChannelID = "";
                reportStatusChange = false;
                loadConfig();
        }


        public boolean isReportStatusChange() {
                return reportStatusChange;
        }

        public void setReportStatusChange(boolean reportStatusChange) {
                this.reportStatusChange = reportStatusChange;
        }

        public String getModChannelID() {
                return modChannelID;
        }

        public void setModChannelID(String modChannelID) {
                this.modChannelID = modChannelID;
        }

        public String getAnnounceChannelID() {
                return announceChannelID;
        }

        public void setAnnounceChannelID(String announceChannelID) {
                this.announceChannelID = announceChannelID;
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
                File parent = new File("guilds");
                if(!parent.exists()){
                        parent.mkdir();
                }
                File config = new File(parent, id+".properties");
                Properties prop = createProperties();
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
                announceChannelID = prop.getProperty("announceChannelID","");
                modChannelID = prop.getProperty("modChannelID","");
                reportStatusChange = Boolean.getBoolean(prop.getProperty("reportStatusChange", Boolean.toString(false)));
        }

        public void saveConfig(){
                File parent = new File("guilds");
                File config = new File(parent, id+".properties");
                Properties prop = createProperties();
                try {
                        if (!config.exists()) {
                               config.createNewFile();
                        }
                        OutputStream out = new FileOutputStream(config);
                        prop.store(out, "Configurations: Server " + id);
                        out.close();
                } catch (IOException e){
                        e.printStackTrace();
                }
                if(!checkSavedConfig(config)){
                                System.err.print("Config failed to update on disk...");
                }
        }

        private boolean checkSavedConfig(File config){
                Properties prop = new Properties();
                try (InputStream finput = new FileInputStream(config)) {
                        prop.clear();
                        prop.load(finput);
                        finput.close();

                } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                }
                return (prop.getProperty("welcomeMessage").equals(welcomeMessage)&&
                        prop.getProperty("prefix").equals(prefix)&&
                        prop.getProperty("announceChannelID").equals(announceChannelID)&&
                        prop.getProperty("modChannelID").equals(modChannelID));
        }

        private Properties createProperties(){
                Properties prop = new Properties();
                prop.setProperty("welcomeMessage",welcomeMessage);
                prop.setProperty("prefix",prefix);
                prop.setProperty("announceChannelID",announceChannelID);
                prop.setProperty("modChannelID",modChannelID);
                prop.setProperty("reportStatusChange",reportStatusChange.toString());
                return prop;
        }


}
