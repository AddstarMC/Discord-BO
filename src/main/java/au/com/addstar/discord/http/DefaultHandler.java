package au.com.addstar.discord.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 14/12/2016.
 */
public class DefaultHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        List<String> contentType = new ArrayList<>();
        contentType.add("application/text");
        Utilities.doResponse(t, HttpStatus.SC_OK,contentType,"Endpoint Not Available");
    }
}
