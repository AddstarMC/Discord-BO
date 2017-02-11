package au.com.addstar.discord.managers;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.objects.GuildConfig;
import au.com.addstar.discord.objects.Invitation;
import au.com.addstar.discord.ulilities.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IInvite;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 18/12/2016.
 */
public class InvitationManager {

    public static void saveInvites(GuildConfig config) {
        SimpleBot.log.info("Saving Invitations to file");
        if (config.getInviteCache().size() == 0) return;
        Gson gsonencoder = new Gson();
        File parent = new File("guilds");
        File sub = new File(parent, config.getId());
        if (!sub.exists()) {
            sub.mkdir();
        }
        File inviteFile = new File(sub, "invites.json");
        try {
            if (!inviteFile.exists()) inviteFile.createNewFile();
            OutputStream out = new FileOutputStream(inviteFile);
            Type type = new TypeToken<Map<String, Invitation>>() {
            }.getType();
            String encoded = gsonencoder.toJson(config.getInviteCache(), type);
            out.write(encoded.getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SimpleBot.log.info("Save Completed...");
    }
    public static void loadInvites(GuildConfig config){
        int size = (config.getInviteCache()!= null)?config.getInviteCache().size():0;
        SimpleBot.log.info("Loading Invitations from file (Current:" +size +")");
        Gson gsondecoder = new Gson();
        File parent = new File ("guilds");
        File sub =  new File(parent,config.getId());
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
            if (config.getInviteCache() == null){
                config.setInviteCache(loadedInvites);
                return;
            }
            for(Map.Entry<String,Invitation> e: loadedInvites.entrySet()){
                if(!config.getInviteCache().containsKey(e.getKey())){
                    config.getInviteCache().put(e.getKey(),e.getValue());
                }
            }
        }
        if (config.getInviteCache().size() > size) {
            int added = config.getInviteCache().size() - size;
            SimpleBot.log.info("Loading Completed added " + added);
        }
    }

    public static Invitation storeInvitation(GuildConfig config, Invitation value){
        synchronized (config.getInviteCache()) {
            if (!config.getInviteCache().containsKey(value.getInviteCode())) {
                return config.getInviteCache().put(value.getInviteCode(), value);
            } else {
                SimpleBot.log.error("Key was already cached : " + value.getInviteCode());
            }
        }
        saveInvites(config);
        return config.getInviteCache().get(value.getInviteCode());
    }

    public static void removeInvitation(GuildConfig config, String code){
        synchronized (config.getInviteCache()) {
            if (config.getInviteCache().containsKey(code)) {
                config.getInviteCache().remove(code);
            } else {
                SimpleBot.log.error("Key was not found in invite cache : " + code);
            }
        }
    }
    public static Invitation checkForUUIDInvite(GuildConfig config, UUID uuid) {
        for (Map.Entry<String,Invitation> e : config.getInviteCache().entrySet()){
            UUID stored = e.getValue().getUuid();
            if(stored.equals(uuid)){
                return e.getValue();
            }
        }
        return null;
    }

    public static Invitation getInvitation(GuildConfig config, String code){
        Invitation inv = config.getInviteCache().get(code);
        if (inv.hasExpired())return null;
        return inv;
    }
    public static Invitation getExpiredInvite(GuildConfig config, String code){
        Invitation inv = config.getInviteCache().get(code);
        return (inv.hasExpired())?inv:null;
    }

    public static List<Invitation> getPendingInvites(GuildConfig config){
        List<Invitation> invites = new ArrayList<>();
        for (Map.Entry<String, Invitation> e : config.getInviteCache().entrySet()){
            invites.add(e.getValue());
        }
        return invites;
    }
    public static IInvite checkforInvite(IChannel chan, Invitation botinvite){
        try {
            List<IInvite> invites = chan.getInvites();
            for(IInvite invite : invites){
                if(invite.getInviteCode().equals(botinvite.getInviteCode()))return invite;
                SimpleBot.log.info("Invite Code matched Discord Invite: " + invite.getInviteCode() +" Expiry: "  + Utility.getDate(botinvite.getExpiryTime()));
            }
        } catch (DiscordException | RateLimitException e) {
            e.printStackTrace();
        } catch (MissingPermissionsException e) {
            SimpleBot.log.error(" We dont have permission to get channel invites");
            e.printStackTrace();
        }
        return null;
    }
    public static IInvite createInvite(IChannel chan, int age, int maxUses, Boolean temp){

        return createInvite(chan, age, maxUses, true,true);
    }
    private static IInvite createInvite(IChannel chan, int age, int maxUses, Boolean temp, boolean unique){
        IInvite invite = null;
        try {
            invite = chan.createInvite(age, maxUses, temp, unique);
        } catch (MissingPermissionsException | DiscordException | RateLimitException e) {
            e.printStackTrace();
        }
        return invite;
    }
}
