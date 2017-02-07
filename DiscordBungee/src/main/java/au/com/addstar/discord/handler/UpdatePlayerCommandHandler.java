package au.com.addstar.discord.handler;

import au.com.addstar.bc.PlayerSettings;
import au.com.addstar.discord.DiscordBungee;
import au.com.addstar.discord.messages.*;
import au.com.addstar.discord.objects.McUser;
import au.com.addstar.discord.redis.IncomingCommandHandler;
import net.cubespace.geSuit.managers.DatabaseManager;
import net.cubespace.geSuit.objects.GSPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 4/02/2017.
 */
public class UpdatePlayerCommandHandler implements IncomingCommandHandler {
    DiscordBungee plugin;

    public UpdatePlayerCommandHandler(DiscordBungee plugin) {
        this.plugin = plugin;
    }

    @Override
    public ResponseMessage onCommand(IMessage message) {
        if (message.getMessageType() == MessageType.Command) {
            AbstractCommandMessage cmessage = (AbstractCommandMessage) message;
            switch (cmessage.getCommandType()) {
                case UpdatePlayer:
                    UpdatePlayersMessage updateMessage = (UpdatePlayersMessage) cmessage;
                    return UpdatePlayerCommand(updateMessage);
            }
        }
        return new ResponseMessage(message.getServerID(), message.getMessageId(), ResponseMessage.ResponseTypes.FAIL, "Message Not Command:" + message.getMessageType().toString());
    }

    private ResponseMessage UpdatePlayerCommand(UpdatePlayersMessage message) {
        Map<String, McUser> players = message.getPlayers();
        Map<String, McUser> updated = new HashMap<>();
        for (Map.Entry<String, McUser> entry : players.entrySet()) {
            McUser user = entry.getValue();
            if (plugin.gHooked) {
                if (user.getMinecraftUUID() != null) {
                    PlayerSettings settings = plugin.bcplugin.getManager().getSettings(user.getMinecraftUUID());
                    user.addUpdateDisplayName(message.getServerID(), settings.nickname);
                    updated.put(user.getDiscordID(), user);
                }
            }
        }
        return new UpdatePlayerResponseMessage(message.getServerID(), message.getMessageId(), updated);

    }
}
