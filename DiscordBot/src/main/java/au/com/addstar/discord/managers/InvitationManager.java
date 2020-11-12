package au.com.addstar.discord.managers;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.objects.GuildConfig;
import au.com.addstar.discord.objects.Invitation;
import au.com.addstar.discord.ulilities.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import discord4j.core.object.Invite;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.spec.InviteCreateSpec;
import discord4j.discordjson.json.InviteData;
import discord4j.rest.entity.RestChannel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 18/12/2016.
 */
public class InvitationManager {

    public static void saveInvites(GuildConfig config) {
        SimpleBot.log.info("Saving Invitations to file");
        if (config.getInviteCache().size() == 0) {
            return;
        }
        Gson gsonencoder = new Gson();
        File parent = new File("guilds");
        File sub = new File(parent, config.getId().toString());
        if (!sub.exists()) {
            sub.mkdir();
        }
        File inviteFile = new File(sub, "invites.json");
        try {
            if (!inviteFile.exists()) {
                inviteFile.createNewFile();
            }
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

    public static void loadInvites(GuildConfig config) {

        int size = (config.getInviteCache() != null) ? config.getInviteCache().size() : 0;
        SimpleBot.log.info("Loading Invitations from file (Current:" + size + ")");
        Gson gsondecoder = new Gson();
        File parent = new File("guilds");
        File sub = new File(parent, config.getId().toString());
        Map<String, Invitation> loadedInvites = null;
        if (!sub.exists()) {
            return;
        }
        File inviteFile = new File(sub, "invites.json");
        if (!inviteFile.exists()) {
            return;
        }
        try {
            InputStream in = new FileInputStream(inviteFile);
            InputStreamReader inread = new InputStreamReader(in);
            JsonReader reader = new JsonReader(inread);
            Type type = new TypeToken<Map<String, Invitation>>() {
            }.getType();
            loadedInvites = gsondecoder.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (loadedInvites != null && loadedInvites.size() > 0) {
            if (config.getInviteCache() == null) {
                config.setInviteCache(loadedInvites);
                return;
            }
            for (Map.Entry<String, Invitation> e : loadedInvites.entrySet()) {
                if (!config.getInviteCache().containsKey(e.getKey())) {
                    config.getInviteCache().put(e.getKey(), e.getValue());
                }
            }
        }
        if (config.getInviteCache().size() > size) {
            int added = config.getInviteCache().size() - size;
            SimpleBot.log.info("Loading Completed added " + added);
        }
    }

    public static Invitation storeInvitation(GuildConfig config, Invitation value) {
        Map<String, Invitation> icache = config.getInviteCache();
        synchronized (icache) {
            if (!icache.containsKey(value.getInviteCode())) {
                return icache.put(value.getInviteCode(), value);
            } else {
                SimpleBot.log.error("Key was already cached : " + value.getInviteCode());
            }
        }
        saveInvites(config);
        return icache.get(value.getInviteCode());
    }

    public static void removeInvitation(GuildConfig config, String code) {
        Map<String, Invitation> icache = config.getInviteCache();
        synchronized (icache) {
            if (icache.containsKey(code)) {
                icache.remove(code);
            } else {
                SimpleBot.log.error("Key was not found in invite cache : " + code);
            }
        }
    }

    public static Invitation checkForUUIDInvite(GuildConfig config, UUID uuid) {
        Invitation result = null;
        for (Map.Entry<String, Invitation> e : config.getInviteCache().entrySet()) {
            UUID stored = e.getValue().getUuid();
            if(stored.equals(uuid)){
                result = e.getValue();
                break;
            }
        }
        if (result == null) {
            return null;
        }
        if (result.hasExpired()){
            removeInvitation(config,result.getInviteCode());
            return null;
        }
        return result;
    }

    public static Invitation getInvitation(GuildConfig config, String code) {
        Invitation inv = config.getInviteCache().get(code);
        if (inv == null || inv.hasExpired()) {
            return null;
        }
        return inv;
    }

    public static Invitation getExpiredInvite(GuildConfig config, String code) {
        Invitation inv = config.getInviteCache().get(code);
        return (inv.hasExpired()) ? inv : null;
    }

    public static List<Invitation> getPendingInvites(GuildConfig config) {
        List<Invitation> invites = new ArrayList<>();
        for (Map.Entry<String, Invitation> e : config.getInviteCache().entrySet()) {
            invites.add(e.getValue());
        }
        return invites;
    }

    public static InviteData checkforInvite(RestChannel chan, Invitation botinvite) {
        Flux<InviteData> invites = chan.getInvites();
        InviteData invite = invites.filter(new Predicate<InviteData>() {
            @Override
            public boolean test(InviteData inviteData) {
                return botinvite.getInviteCode().equals(inviteData.code());
            }
        }).onErrorReturn(null).blockFirst();
        if (invite != null) {
            SimpleBot.log.info("Invite Code matched Discord Invite: " + invite.code() + " Expiry: " + Utility.getDate(botinvite.getExpiryTime()));
        }
        return invite;
    }

    public static Mono<InviteData> createInvite(RestChannel chan, int age, int maxUses, boolean temp) {
        return createInvite(chan, age, maxUses, temp, true);
    }

    public static Mono<InviteData> createInvite(RestChannel chan, int age, int maxUses, boolean temp, boolean unique) {
        InviteCreateSpec spec = new InviteCreateSpec();
        spec.setMaxAge(age);
        spec.setMaxUses(maxUses);
        spec.setTemporary(temp);
        spec.setUnique(unique):
        spec.asRequest();
        return chan.createInvite(spec.asRequest(), "Guild Invitation");
    }
}
