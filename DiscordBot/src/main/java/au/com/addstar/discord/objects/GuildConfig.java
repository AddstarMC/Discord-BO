package au.com.addstar.discord.objects;

import au.com.addstar.discord.managers.InvitationManager;

import java.io.*;
import java.util.*;

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
        private Integer expiryTime; //expiry time in seconds
        private String guildMcHost;
        private Integer guildPort;
        private Map<String, Invitation> inviteCache;

        public GuildConfig(String id){
                this.id = id;
                prefix = "!!";
                welcomeMessage = "";
                announceChannelID = "";
                modChannelID = "";
                reportStatusChange = false;
                inviteCache =  new HashMap<>();
                expiryTime = 7200;
                guildMcHost = null;
                guildPort = null;
                loadConfig();
        }

        public void setInviteCache(Map<String, Invitation> inviteCache) {
                this.inviteCache = inviteCache;
        }

        public String getId() {
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

        public int getExpiryTime() {
                return expiryTime;
        }

        public void setExpiryTime(int expiryTime) {
                this.expiryTime = expiryTime;
        }


        public String getGuildMcHost() {
                return guildMcHost;
        }

        public void setGuildMcHost(String guildMcHost) {
                this.guildMcHost = guildMcHost;
        }

        public Integer getGuildPort() {
                return guildPort;
        }

        public void setGuildPort(int guildPort) {
                this.guildPort = guildPort;
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
                announceChannelID = prop.getProperty("announceChannelID","");
                modChannelID = prop.getProperty("modChannelID","");
                reportStatusChange = Boolean.getBoolean(prop.getProperty("reportStatusChange", Boolean.toString(false)));
                expiryTime = Integer.parseInt(prop.getProperty("expiryTime", "7200"));
                guildMcHost = prop.getProperty("guildMcHost",null);
                try{
                        guildPort = Integer.parseInt(prop.getProperty("guildPort",null));
                }catch (NumberFormatException e){
                        guildPort = null;
                }
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
                                System.err.print("Config failed to update on disk...");
                }
                InvitationManager.saveInvites(this);
        }


        private boolean checkSavedConfig(File config) {
                Properties prop = new Properties();
                try (InputStream finput = new FileInputStream(config)) {
                        prop.clear();
                        prop.load(finput);
                        finput.close();

                } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                }
                return (prop.getProperty("welcomeMessage").equals(welcomeMessage) &&
                        prop.getProperty("prefix").equals(prefix) &&
                        prop.getProperty("announceChannelID").equals(announceChannelID) &&
                        prop.getProperty("modChannelID").equals(modChannelID) &&
                        prop.getProperty("reportStatusChange").equals(reportStatusChange.toString()) &&
                        prop.getProperty("expiryTime").equals(expiryTime.toString()) &&
                        Objects.equals(prop.getProperty("guildMcHost"),guildMcHost)  &&
                        ((prop.getProperty("guildPort") == null && guildPort == null) ||
                                Integer.parseInt(prop.getProperty("guildPort"))==guildPort)
                );
        }

        private Properties createProperties(){
                Properties prop = new Properties();
                prop.setProperty("welcomeMessage",welcomeMessage);
                prop.setProperty("prefix",prefix);
                prop.setProperty("announceChannelID",announceChannelID);
                prop.setProperty("modChannelID",modChannelID);
                prop.setProperty("reportStatusChange",reportStatusChange.toString());
                prop.setProperty("expiryTime",expiryTime.toString());
                if(guildMcHost != null)prop.setProperty("guildMcHost",guildMcHost);
                if(guildPort != null)prop.setProperty("guildPort",guildPort.toString());
                return prop;
        }








}
