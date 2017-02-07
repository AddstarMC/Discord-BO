package au.com.addstar.discord.messages;

import au.com.addstar.discord.objects.McUser;

import java.util.Map;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 2/02/2017.
 */
public class UpdatePlayerResponseMessage extends ResponseMessage {

    private Map<String, McUser> players;

    public UpdatePlayerResponseMessage(String sender, long id, Map<String, McUser> updatedPlayers) {
        super(sender, id);
        players = updatedPlayers;
        setType(CommandType.UpdatePlayer);
    }

    public Map<String, McUser> getPlayers() {
        return players;
    }
}
