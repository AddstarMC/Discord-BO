package au.com.addstar.discord.http;

import au.com.addstar.discord.SimpleBot;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.ChannelData;
import discord4j.discordjson.json.UserGuildData;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.entity.RestGuild;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.http.HttpHeaders;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

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

    static UserGuildData getGuildByName(String name){
        Flux<UserGuildData> guilds = SimpleBot.client.getGuilds();
        return guilds.filter(userGuildData -> userGuildData.name().equals(name)).blockFirst();
    }

    /**
     * This does not perform an api lookup.
     * @param id Guild id as a long.
     * @return RestGuild
     */
    static Mono<Guild> getGuildByID(Long id){
        return SimpleBot.gatewayDiscordClient.getGuildById(Snowflake.of(id));
    }

    static Mono<GuildChannel> getChannelDataById(Guild guild, String id){
        return guild.getChannelById(Snowflake.of(id));
    }


    static MessageChannel getChannelbyName(UserGuildData guild, String name) {
        return  (MessageChannel) SimpleBot.client.getGuildService()
              .getGuildChannels(Snowflake.of(guild.id()).asLong())
              .filter(channelData -> channelData.name().get().equals(name))
              .map(channelData -> {
                  SimpleBot.
                   return SimpleBot.gatewayDiscordClient
                              .getChannelById(Snowflake.of(channelData.id()))
                              .filter(channel -> channel instanceof MessageChannel).block();
                    }).blockFirst();
        List<RestChannel> channels = SimpleBot.client.getGuildService().getGuildChannels(guild).getChannelsByName(name);
        if (channels.size() == 1) return channels.get(0);
        return null;
    }
}
