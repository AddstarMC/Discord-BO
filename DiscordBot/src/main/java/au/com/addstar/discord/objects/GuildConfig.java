package au.com.addstar.discord.objects;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.managers.InvitationManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/12/2016.
 */

public class GuildConfig {


        private final Long id;
        private String prefix;
        private String welcomeMessage;
        private Long announceChannelID;
        private Long modChannelID;
        private Boolean reportStatusChange;
        private Integer expiryTime; //expiry time in seconds
        private Map<String, Invitation> inviteCache;

        public GuildConfig(Long id){
                this.id = id;
                prefix = "!!";
                welcomeMessage = "";
                announceChannelID = 0L;
                modChannelID = 0L;
                reportStatusChange = false;
                inviteCache =  new HashMap<>();
                expiryTime = 7200;
                loadConfig();
        }

        public void setInviteCache(Map<String, Invitation> inviteCache) {
                this.inviteCache = inviteCache;
        }

        public Long getId() {
                return id;
        }

        public Map<String, Invitation> getInviteCache() {
                return inviteCache;
        }

        public boolean isReportStatusChange() {
                return reportStatusChange;
        }

        public void setReportStatusChange(boolean reportStatusChange) {
                this.reportStatusChange = reportStatusChange;
        }

        public Long getModChannelID() {
                return modChannelID;
        }

        public void setModChannelID(Long modChannelID) {
                this.modChannelID = modChannelID;
        }

        public Long getAnnounceChannelID() {
                return announceChannelID;
        }

        public void setAnnounceChannelID(Long announceChannelID) {
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

        public int getExpiryTime() {
                return expiryTime;
        }

        public void setExpiryTime(int expiryTime) {
                this.expiryTime = expiryTime;
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
                                Properties fileprop = new Properties();
                                fileprop.load(finput);
                                finput.close();
                                if (fileprop.size()<prop.size()){//check if we have any new defaults.
                                        Enumeration e = prop.propertyNames();
                                        while (e.hasMoreElements()){
                                                String newprop = (String) e.nextElement();
                                                if (fileprop.get(newprop) == null){
                                                        fileprop.put(newprop,prop.get(newprop));
                                                }
                                        }

                                }
                                prop = fileprop;
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
                String temp = null;
                try {
                    temp = prop.getProperty("announceChannelID", "0");
                        announceChannelID = Long.parseLong(temp);
                }catch (NumberFormatException e){
                    SimpleBot.log.warn("Config -> announceChannelID cannot be parsed to Long..." +temp);
                }
                try {
                         temp = prop.getProperty("modChannelID", "0");
                        modChannelID = Long.parseLong(temp);
                }catch (NumberFormatException e){
                    SimpleBot.log.warn("Config -> modChannelID cannot be parsed to Long..." + temp);

                }
                reportStatusChange = Boolean.getBoolean(prop.getProperty("reportStatusChange", Boolean.toString(false)));
                expiryTime = Integer.parseInt(prop.getProperty("expiryTime", "7200"));
                InvitationManager.loadInvites(this);
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
                                SimpleBot.log.warn("Config failed to update on disk...");
                }
                InvitationManager.saveInvites(this);
        }


        private boolean checkSavedConfig(File config) {
                Properties prop = new Properties();
                try (InputStream finput = new FileInputStream(config)) {
                        prop.clear();
                        prop.load(finput);
                } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                }
                return (prop.getProperty("welcomeMessage").equals(welcomeMessage) &&
                        prop.getProperty("prefix").equals(prefix) &&
                        Long.parseLong(prop.getProperty("announceChannelID")) == announceChannelID &&
                        Long.parseLong(prop.getProperty("modChannelID")) == modChannelID &&
                        prop.getProperty("reportStatusChange").equals(reportStatusChange.toString()) &&
                        prop.getProperty("expiryTime").equals(expiryTime.toString()));
        }

        private Properties createProperties(){
                Properties prop = new Properties();
                prop.setProperty("welcomeMessage",welcomeMessage);
                prop.setProperty("prefix",prefix);
                prop.setProperty("announceChannelID",announceChannelID.toString());
                prop.setProperty("modChannelID",modChannelID.toString());
                prop.setProperty("reportStatusChange",reportStatusChange.toString());
                prop.setProperty("expiryTime",expiryTime.toString());
                return prop;
        }








}
