package au.com.addstar.discord.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 18/12/2016.
 */
public class McUser implements Serializable {

    private static final long serialVersionUID = -3606891793023513622L;
    private final String discordID;
    private UUID minecraftUUID;
    private Map<String, String> displayNames;
    private Map<String, String> detail;

    public String getDiscordID() {
        return discordID;
    }

    public McUser(String discordID) {
        this.discordID = discordID;
        displayNames = new HashMap<>();
    }

    public UUID getMinecraftUUID() {
        return minecraftUUID;
    }

    public void setDisplayNames(Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    public void setMinecraftUUID(UUID minecraftUUID) {
        this.minecraftUUID = minecraftUUID;
    }

    public void addUpdateDisplayName(String guildID, String name) {
        displayNames.put(guildID, name);
    }

    public String getDisplayName(String guildId) {
        return displayNames.get(guildId);
    }

    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    public void storeDetail(String name, String value) {
        detail.put(name, value);
    }

    public String getDetail(String name) {
        return detail.get(name);
    }

    public Map<String, String> getDetails() {
        return detail;
    }
}

