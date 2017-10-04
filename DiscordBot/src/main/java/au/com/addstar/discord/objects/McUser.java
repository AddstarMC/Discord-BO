package au.com.addstar.discord.objects;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 18/12/2016.
 */
public class McUser {

    private final Long discordID;
    private UUID minecraftUUID;
    private Map<Long, String> displayNames;
    private Map<String, String> detail;

    public Long getDiscordID() {
        return discordID;
    }
    public McUser(Long discordID) {
        this.discordID = discordID;
        displayNames = new HashMap<>();
    }

    public UUID getMinecraftUUID() {
        return minecraftUUID;
    }

    public void setDisplayNames(Map<Long, String> displayNames) {
        this.displayNames = displayNames;
    }

    public void setMinecraftUUID(UUID minecraftUUID) {
        this.minecraftUUID = minecraftUUID;
    }

    public void addUpdateDisplayName(Long guildID, String name){
        displayNames.put(guildID,name);
    }

    public String getDisplayName(Long guildID){
        return displayNames.get(guildID);
    }

    public Map<Long, String> getDisplayNames(){
        return displayNames;
    }

    public void storeDetail(String name, String value){
        detail.put(name, value);
    }

    public String getDetail(String name){
        return detail.get(name);
    }


}
