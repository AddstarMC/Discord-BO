package au.com.addstar.SimpleBot.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
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
        private String expiryTime;

        private Map<String, Invitation> inviteCache;

        public GuildConfig(String id){
                this.id = id;
                prefix = "!!";
                welcomeMessage = "";
                announceChannelID = "";
                modChannelID = "";
                reportStatusChange = false;
                loadConfig();
                inviteCache =  new HashMap<>();
        }

        Map<String, Invitation> getInviteCache() {
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
                loadInvites();
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
                saveInvites();
        }

        private void saveInvites(){
                if (inviteCache.size()==0)return;
                Gson gsonencoder = new Gson();
                File parent = new File ("guilds");
                File sub =  new File(parent,id);
                if (!sub.exists()){
                        sub.mkdir();
                }
                File inviteFile = new File(sub,"invites.json");
                try{
                        if(!inviteFile.exists())inviteFile.createNewFile();
                        OutputStream out = new FileOutputStream(inviteFile);
                        Type type = new TypeToken<Map<String,Invitation>>(){}.getType();
                        String encoded = gsonencoder.toJson(inviteCache,type);
                        out.write(encoded.getBytes());
                        out.close();
                }
                catch(IOException e){
                        e.printStackTrace();
                }


        }

        private void loadInvites(){
                Gson gsondecoder = new Gson();
                File parent = new File ("guilds");
                File sub =  new File(parent,id);
                Map<String,Invitation> loadedInvites = null;
                if (!sub.exists()){
                        return;
                }
                File inviteFile = new File(sub,"invites.json");
                if(!inviteFile.exists())return;
                try {
                        InputStream in = new FileInputStream(inviteFile);
                        InputStreamReader inread = new InputStreamReader(in);
                        JsonReader reader = new JsonReader(inread);
                        Type type = new TypeToken<Map<String,Invitation>>(){}.getType();
                        loadedInvites = gsondecoder.fromJson(reader,type);
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                }
                if(loadedInvites != null && loadedInvites.size()>0){
                        if (inviteCache == null){
                                inviteCache = loadedInvites;
                                return;
                        }
                        for(Map.Entry<String,Invitation> e: loadedInvites.entrySet()){
                                if(!inviteCache.containsKey(e.getKey())){
                                        inviteCache.put(e.getKey(),e.getValue());
                                }
                        }
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
                        prop.getProperty("modChannelID").equals(modChannelID)&&
                prop.getProperty("reportStatusChange").equals(reportStatusChange.toString()));
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

        public Invitation storeInvite(Invitation value){
                if(!inviteCache.containsKey(value.getInviteCode())){
                        return inviteCache.put(value.getInviteCode(),value);
                }
                return inviteCache.get(value.getInviteCode());
        }
        public void removeInvite(String code){
                inviteCache.remove(code);
        }

        public Invitation checkForUUIDInvite(UUID uuid) {
                for (Map.Entry<String,Invitation> e : inviteCache.entrySet()){
                        UUID stored = e.getValue().getUuid();
                        if(stored.equals(uuid)){
                                return e.getValue();
                        }
                }
                return null;
        }

        public Invitation getInvitation(String code){
                Invitation inv = inviteCache.get(code);
                if (inv.hasExpired())return null;
                return inviteCache.get(code);
        }

        public Invitation getExpiredInvite(String code){
                Invitation inv = inviteCache.get(code);
                return (inv.hasExpired())?inv:null;
        }

        public List<Invitation> getPendingInvites(){
                List<Invitation> invites = new ArrayList<>();
                for (Map.Entry<String, Invitation> e : inviteCache.entrySet()){
                        invites.add(e.getValue());
                }
                return invites;
        }






}
