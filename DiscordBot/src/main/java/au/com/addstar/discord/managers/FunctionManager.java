package au.com.addstar.discord.managers;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.managers.UserManager;
import au.com.addstar.discord.messages.CommandType;
import au.com.addstar.discord.messages.ResponseMessage;
import au.com.addstar.discord.messages.UpdatePlayerResponseMessage;
import au.com.addstar.discord.objects.Guild;
import au.com.addstar.discord.objects.McUser;
import java.util.Map;
import com.google.common.base.Function;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 4/02/2017.
 */
public class FunctionManager {

    public static Function<ResponseMessage, Object> UpdatePlayerFunction(Guild guild){
        return new Function<ResponseMessage, Object>() {
            public Object apply(ResponseMessage message) {
                if(message.getType() == CommandType.UpdatePlayer){
                    UpdatePlayerResponseMessage updateMessage = (UpdatePlayerResponseMessage) message;
                    if(updateMessage.getServerID().equals(guild.config.getId())) {
                        Map<String, McUser> updatedPlayers = updateMessage.getPlayers();
                        String serverId = updateMessage.getServerID();
                        Map<String, McUser> currentUsers = UserManager.getAllGuildUsers(serverId);
                        for(Map.Entry<String, McUser> e: updatedPlayers.entrySet()){
                            McUser user = currentUsers.get(e.getKey());
                            McUser updatedUser = e.getValue();
                            if(user.getDiscordID().equals(updatedUser.getDiscordID())) {
                                user.addUpdateDisplayName(serverId,updatedUser.getDisplayName(serverId));
                                if(user.getMinecraftUUID() != updatedUser.getMinecraftUUID()){
                                    SimpleBot.log.info(user.getDiscordID() + ":" + user.getDisplayName(serverId) + " UUID updated.");
                                    user.setMinecraftUUID(updatedUser.getMinecraftUUID());
                                    if(updatedUser.getDetail() != null && updatedUser.getDetail().size() >0 ){
                                        for(Map.Entry<String, String> detail: updatedUser.getDetail().entrySet()){
                                            if(user.getDetail().containsKey(detail.getKey())){
                                                user.storeDetail(detail.getKey(),detail.getValue());
                                            }
                                        }
                                    }
                                }
                                currentUsers.put(serverId,user);
                            }
                        }
                        UserManager.saveCache();
                    }

                }
                return null;
            }

        };
    }


}
