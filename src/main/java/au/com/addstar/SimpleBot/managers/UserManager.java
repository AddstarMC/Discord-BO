package au.com.addstar.SimpleBot.managers;

import au.com.addstar.SimpleBot.SimpleBot;
import au.com.addstar.SimpleBot.objects.McUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;
import sx.blah.discord.handle.obj.IGuild;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 18/12/2016.
 */
public class UserManager {

    private static Map<String, McUser> userCache;
    private static Gson gsonencoder = new Gson();
    private static Type type = new TypeToken<McUser>(){}.getType();


    public void addGuildtoUser(McUser user, String displayName, IGuild guild) {

    }

    public static void cacheUser(McUser u) {
        String key = u.getDiscordID();
        userCache.put(key, u);
    }

    private static McUser loadfromCache(String id){
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
        for (Map.Entry<String, McUser> e : userCache.entrySet()) {
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
    @Nullable
    public static McUser loadUser(String id){
        McUser user = loadfromCache(id);
        return(user == null)?loadUserFromFile(id):user;
    }
@Nullable
    public static McUser loadUserFromFile(String id){
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

}
