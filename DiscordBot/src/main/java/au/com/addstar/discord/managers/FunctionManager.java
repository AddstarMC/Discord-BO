package au.com.addstar.discord.managers;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.messages.AMessage;
import au.com.addstar.discord.messages.UpdatePlayersMessage;
import au.com.addstar.discord.messages.identifiers.CommandType;
import au.com.addstar.discord.objects.Guild;
import au.com.addstar.discord.objects.McUser;
import java.util.Map;

import au.com.addstar.discord.ulilities.Utility;
import com.google.common.util.concurrent.FutureCallback;
import sx.blah.discord.handle.obj.IUser;

import static au.com.addstar.discord.managers.UserManager.saveCache;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 4/02/2017.
 */
public class FunctionManager {

    public static FutureCallback<AMessage> UpdatePlayers(Guild guild, IUser u) {
        return new FutureCallback<AMessage>() {
            @Override
            public void onSuccess(AMessage message) {

                if (message.getCommandType() == CommandType.UpdatePlayer) {
                    UpdatePlayersMessage updateMessage = (UpdatePlayersMessage) message;
                    if (updateMessage.getSourceId().equals(guild.config.getId())) {
                        Map<String, McUser> updatedPlayers = updateMessage.getPlayers();
                        String serverId = updateMessage.getSourceId();
                        Map<String, McUser> currentUsers = UserManager.getAllGuildUsers(serverId);
                        for (Map.Entry<String, McUser> e : updatedPlayers.entrySet()) {
                            McUser user = currentUsers.get(e.getKey());
                            McUser updatedUser = e.getValue();
                            if (user.getDiscordID().equals(updatedUser.getDiscordID())) {
                                user.addUpdateDisplayName(serverId, updatedUser.getDisplayName(serverId));
                                if (user.getMinecraftUUID() != updatedUser.getMinecraftUUID()) {
                                    SimpleBot.log.info(user.getDiscordID() + ":" + user.getDisplayName(serverId) + " UUID updated.");
                                    user.setMinecraftUUID(updatedUser.getMinecraftUUID());
                                    Map<String, String> details = updatedUser.getDetails();
                                    if (details != null && details.size() > 0) {
                                        for (Map.Entry<String, String> detail : details.entrySet()) {
                                            user.storeDetail(detail.getKey(), detail.getValue());
                                        }
                                    }
                                }
                                currentUsers.put(serverId, user);
                            }
                        }
                        saveCache();
                        Utility.sendPrivateMessage(u,"Player Data Updated");
                    }
                    Utility.sendPrivateMessage(u, "Guild ID and sourceID of response did not match..");

                }
                Utility.sendPrivateMessage(u, "Message was incorrect type...");
            }


            @Override
            public void onFailure(Throwable t) {
                Utility.sendPrivateMessage(u, "Exception: " + t.getMessage());

            }

        };
    }
}
