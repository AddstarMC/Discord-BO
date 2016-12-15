package au.com.addstar.SimpleBot.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.apache.http.HttpHeaders;

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
}
