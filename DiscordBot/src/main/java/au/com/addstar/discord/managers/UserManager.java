package au.com.addstar.discord.managers;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.objects.McUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import reactor.core.publisher.Mono;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 18/12/2016.
 */
public class UserManager {

    private static Map<Long, McUser> userCache = new HashMap<>();
    private static Gson gsonencoder = new Gson();
    private static Type type = new TypeToken<McUser>(){}.getType();

    public static void initialize(final DiscordClient client){
        client.getGuilds().map(guild -> {
            guild.getMembers().map(member -> {
                McUser user = loadUserFromFile(member.getId().asLong());
                if (user == null){
                    SimpleBot.log.info("No save exists for User:" + user.getDisplayName(guild.getId().asLong()));
                    user = new McUser(member.getId());
                    user.addUpdateDisplayName(guild.getId().asLong(),user.getDisplayName(guild.getId().asLong()));
                    saveUserToFile(user);
                }
                cacheUser(user);
                return user;
            });
            return guild;
        });
    }



    public static void addGuildtoUser(McUser user, String displayName, Guild guild) {
        user.addUpdateDisplayName(guild.getId().asLong(),displayName);
    }

    public static void cacheUser(McUser u) {
        Long key = u.getDiscordID();
        userCache.put(key, u);
    }

    private static McUser loadfromCache(Long id){
        return userCache.get(id);
    }

    public static void removeUser(McUser u) {
        userCache.remove(u.getDiscordID());

    }
    public static void saveUser(McUser u){
        cacheUser(u);
        saveUserToFile(u);
    }

    public static void saveCache() {
        SimpleBot.log.info("Transferring User Cache to disk");
        File parent = new File("users");
        if (!parent.exists()) {
            parent.mkdir();
        }
        for (Map.Entry<Long, McUser> e : userCache.entrySet()) {
            String fileName = e.getKey() + ".json";
            File outFile = new File(parent, fileName);
            boolean exists = outFile.exists();
            if (!exists) {
                try {
                    outFile.createNewFile();
                } catch (IOException ex) {
                    SimpleBot.log.error("Could not create file :" + outFile.getName());
                    continue;
                }
            }
            try {
                OutputStream out = new FileOutputStream(outFile);
                String encoded = gsonencoder.toJson(e.getValue(), type);
                out.write(encoded.getBytes());
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        SimpleBot.log.info("User Cache saved to disk");


    }
    public static void saveUserToFile(McUser u) {
        SimpleBot.log.info("Transferring User to disk: " + u.getDiscordID());
        File parent = new File("users");
        if (!parent.exists()) {
            parent.mkdir();
        }
        String fileName = u.getDiscordID() + ".json";
        File outFile = new File(parent, fileName);
        boolean exists = outFile.exists();
        if (!exists) {
            try {
                outFile.createNewFile();
            } catch (IOException ex) {
                SimpleBot.log.error("Could not create file :" + outFile.getName());
                return;
            }
        }
        try {
            OutputStream out = new FileOutputStream(outFile);
            Type type = new TypeToken<McUser>() {
            }.getType();
            String encoded = gsonencoder.toJson(u, type);
            out.write(encoded.getBytes());
            out.close();
        } catch (IOException ex) {
            SimpleBot.log.error("Unable to write user file to disk");
            ex.printStackTrace();
            return;
        }

        SimpleBot.log.info("User saved to disk");

    }
    public static McUser loadUser(Long id){
        McUser user = loadfromCache(id);
        return(user == null)?loadUserFromFile(id):user;
    }
    public static McUser loadUserFromFile(Long id){
        File parent = new File("users");
        if(!parent.exists()){
            return null;
        }
        String fileName = id + ".json";
        File file = new File(parent,fileName);
        if(!file.exists())return null;
        try {
            InputStream in = new FileInputStream(file);
            InputStreamReader inread = new InputStreamReader(in);
            McUser user = gsonencoder.fromJson(inread,type);
            userCache.put(user.getDiscordID(),user);
            return user;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Mono<Void> setUserNick(Member u, String nick) {
        return u.edit(guildMemberEditSpec -> {
            guildMemberEditSpec.setNickname(nick);
        });
    }


    public static Mono<Member> setRoleforUser(Guild g, Member u, Role r){
        return u.asMember(g.getId()).map(member -> {
            member.addRole(r.getId());
            return member;
        });
    }

    public static void checkUserDisplayName(McUser user, Guild guild){

        String savedName = user.getDisplayName(guild.getId().asLong());
        if(savedName == null){
            guild.getMemberById(user.getId()).
                    subscribe(member -> {
                        user.addUpdateDisplayName(guild.getId().asLong(),member.getDisplayName());
                        saveUser(user);
                    });
        } else {
            guild.getMemberById(user.getId()).
                    filter(member -> !member.getDisplayName().equals(savedName)).
                    map(member -> {
                        SimpleBot.log.info("Discord User: " + user.getDiscordID() + " has updated thier displayName. Resetting");
                        setUserNick(member, savedName);
                        return member;
                    }).
                    subscribe();
        }
    }



}
