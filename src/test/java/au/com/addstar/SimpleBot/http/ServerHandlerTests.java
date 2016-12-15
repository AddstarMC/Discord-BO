package au.com.addstar.SimpleBot.http;

import au.com.addstar.SimpleBot.SimpleBot;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 14/12/2016.
 */
public class ServerHandlerTests {

    private static HttpClient testclient;

    @BeforeClass
    public static void setUp() throws Exception {
        SimpleBot.main(null);
        testclient = HttpClientBuilder.create().build();
    }

    @Test
    public void testInviteHandlerUnknownGuild() throws Exception {
        UUID id  = UUID.randomUUID();
        HttpGet uriRequest = new HttpGet("http://localhost:22000/invite/UnknownGuild/UnknownChannel/"+id.toString()+"/UnknownUser");
        testclient.execute(uriRequest, (ResponseHandler<HttpResponse>) response -> {
            Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST);
            Assert.assertEquals("No Guilds found matching UnknownGuild", EntityUtils.toString(response.getEntity()));
            return null;
        });

    }
}