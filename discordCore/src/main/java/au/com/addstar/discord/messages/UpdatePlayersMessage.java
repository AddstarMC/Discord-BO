package au.com.addstar.discord.messages;

import au.com.addstar.discord.objects.McUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdatePlayersMessage extends AbstractCommandMessage {

    private static final long serialVersionUID = -3202570780981339073L;
    private Map<String, McUser> players;


    public UpdatePlayersMessage(String serverID, long id){
        super(CommandType.UpdatePlayer,serverID, id);
        players = new HashMap<>();
    }

    public void addPlayer(McUser user){
        players.put(user.getDiscordID(),user);
    }

    public void removePlayer(String discordID){
        players.remove(discordID);
    }

    public void removePlayer(UUID uuid){
        String keytoRemove=null;
        for (Map.Entry<String, McUser> e: players.entrySet()){
            if(e.getValue().getMinecraftUUID().equals(uuid)){
                keytoRemove = e.getKey();
                break;
            }

        }
        if(keytoRemove != null)removePlayer(keytoRemove);
    }

    public Map<String, McUser> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, McUser> players) {
        this.players = players;
    }

}