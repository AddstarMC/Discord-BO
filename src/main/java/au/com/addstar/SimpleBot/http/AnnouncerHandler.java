package au.com.addstar.SimpleBot.http;

import au.com.addstar.SimpleBot.SimpleBot;
import au.com.addstar.SimpleBot.ulilities.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is designed to handle and translate strings into Discord Announcements.
 * It takes a GuildName and Channelname as the path ie
 * URL / announcer / GuildName / ChannelName
 * the response body should contain a single JSON object in the form
 * {message=>themessageyou want to send}
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 14/12/2016.
 */
public class AnnouncerHandler implements HttpHandler {

    private static Gson GsonDecoder = new Gson();

    @Override
    public void handle(HttpExchange t) throws IOException {
        int responseCode = HttpStatus.SC_OK;
        String response = null;
        List<String> contentHeaderList = new ArrayList<>();
        String requestPath = t.getRequestURI().getPath();
        String[] path = requestPath.split("/");
        String message = null;
        if(path.length != 4){
            responseCode = HttpStatus.SC_METHOD_NOT_ALLOWED;
            contentHeaderList.add("application/text");
            response = "Path format incorrect Error: "+ requestPath;
            SimpleBot.log.info("Path format incorrect Error: "+ requestPath + "Size = "+path.length);

        }else {
            InputStream in = t.getRequestBody();
            String myString = IOUtils.toString(in, "UTF-8");
            Type type = new TypeToken<Map<String,String>>(){}.getType();
            Map<String, String> request = null;
            try{
              request = GsonDecoder.fromJson(myString, type);
            }catch (JsonSyntaxException e){
                SimpleBot.log.error("Could not parse the json :" + myString);
                responseCode = HttpStatus.SC_BAD_REQUEST;
                response = "Could not parse the json :" + myString;
            }
            if (request != null) {
                message = request.get("message");
            }
            IGuild guild = Utilities.getGuildbyName(path[2]);
            IChannel channel = Utilities.getChannelbyName(guild, path[3]);
            Utility.sendChannelMessage(channel.getID(),message);
            SimpleBot.log.info("Message recieved on Announcer: Guild: " + path[2] + " Channel:" + path[3] + "Message: " + message);
        }
        Utilities.doResponse(t,responseCode,contentHeaderList, response);
    }
}
