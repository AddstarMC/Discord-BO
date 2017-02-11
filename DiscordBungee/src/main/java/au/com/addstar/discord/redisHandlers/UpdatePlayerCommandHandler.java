package au.com.addstar.discord.redisHandlers;

import au.com.addstar.bc.PlayerSettings;
import au.com.addstar.discord.DiscordBungee;
import au.com.addstar.discord.messages.*;
import au.com.addstar.discord.messages.identifiers.CommandType;
import au.com.addstar.discord.messages.identifiers.MessageStatus;
import au.com.addstar.discord.messages.identifiers.MessageType;
import au.com.addstar.discord.objects.McUser;
import au.com.addstar.discord.redis.IncomingCommandHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 4/02/2017.
 */
public class UpdatePlayerCommandHandler implements IncomingCommandHandler {
    private final DiscordBungee plugin;

    public UpdatePlayerCommandHandler(DiscordBungee plugin) {
        this.plugin = plugin;
    }

    @Override
    public AMessage onCommand(AMessage message) {
        AMessage result;
        if (message.getMessageType() == MessageType.Command) {
            switch (message.getCommandType()) {
                case UpdatePlayer:
                    UpdatePlayersMessage updateMessage = (UpdatePlayersMessage) message;
                    result =  UpdatePlayerCommand(updateMessage);
                    break;
                case Unknown:
                    default:
                        result = new ResponseMessage(CommandType.Unknown,message.getSourceId(), message.getMessageId(), MessageStatus.FAIL,
                                "Message Not Command:" + message.getMessageType().toString());
            }
        }else{
            //dont handle this
            result = null;
        }
        return result;
    }

    private AMessage UpdatePlayerCommand(UpdatePlayersMessage message) {
        Map<String, McUser> players = message.getPlayers();
        Map<String, McUser> updated = new HashMap<>();
        for (Map.Entry<String, McUser> entry : players.entrySet()) {
            McUser user = entry.getValue();
            if (plugin.bcplugin != null) {
                if (user.getMinecraftUUID() != null) {
                    PlayerSettings settings = plugin.bcplugin.getManager().getSettings(user.getMinecraftUUID());
                    user.addUpdateDisplayName(message.getSourceId(), settings.nickname);
                    updated.put(user.getDiscordID(), user);
                }
            }
        }
        return new UpdatePlayersMessage(message.getSourceId(),message.getMessageId(),message.getCommandType(),
                MessageType.Response,updated,MessageStatus.OK);

    }
}
