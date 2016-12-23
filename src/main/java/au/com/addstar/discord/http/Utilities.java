package au.com.addstar.discord.http;

import au.com.addstar.discord.SimpleBot;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.apache.http.HttpHeaders;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 14/12/2016.
 */
class Utilities {

    private static final Gson gson = new Gson();


    static void doJsonResponse(HttpExchange t, int responseCode, Map<String, String> map) throws IOException {
        List<String> contentType = new ArrayList<>();
        String response = gson.toJson(map);
        contentType.add("application/json");
        doResponse(t,responseCode,contentType,response);
    }

    static void doResponse(HttpExchange t, int responseCode, List<String> contentType, String response) throws IOException {
        if (contentType.size() > 0){
            t.getResponseHeaders().put(HttpHeaders.CONTENT_TYPE, contentType);
        }
        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    static IGuild getGuildbyName(String name){
        List<IGuild> guilds = SimpleBot.client.getGuilds();
        IGuild guild = null;
        int matches = 0;
        for (IGuild g : guilds) {
            if (g.getName().equals(name)) {
                matches++;
                guild = g;

            }
        }
        if(matches>1){
            return null;
        }
        return guild;
    }
    static IChannel getChannelbyName(IGuild guild, String name) {
        List<IChannel> channels = SimpleBot.client.getGuildByID(guild.getID()).getChannelsByName(name);
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
