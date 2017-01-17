package au.com.addstar.discord.socketClient;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.managers.UserManager;
import au.com.addstar.discord.messages.IMessage;
import au.com.addstar.discord.messages.UpdatePlayersMessage;
import au.com.addstar.discord.objects.McUser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.*;

public class MessageHandler extends ChannelInboundHandlerAdapter  {
    /**
     * Creates a client-side handler.
     */

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        IMessage o = (IMessage) msg;
        switch (o.getMessageType()) {
            case "UpdatePlayers"://handle McUser update
                UpdatePlayersMessage updateMsg = (UpdatePlayersMessage) o;
                String serverID = o.getServerID();
                if (serverID == null) {
                    SimpleBot.log.warn("Client Response did not contain valid server id. MSG: {}",updateMsg.toString());
                    return;
                }

                Map<String, McUser> playersToUpdate = updateMsg.getPlayers();
                for (Map.Entry<String, McUser> player : playersToUpdate.entrySet()) {
                    McUser user = UserManager.loadUser(player.getKey());
                    UUID oldUUID = user.getMinecraftUUID();
                    UUID newUUID = player.getValue().getMinecraftUUID();
                    if (oldUUID == null) { //update UUID
                        user.setMinecraftUUID(player.getValue().getMinecraftUUID());
                        UserManager.checkUserDisplayName(user, SimpleBot.client.getGuildByID(serverID)); //update DisplayName
                    }
                    if (oldUUID == newUUID) { //update DisplayName
                        String oldDisplayname = user.getDisplayName(serverID);
                        String newDisplayName = player.getValue().getDisplayName(serverID);
                        if (!oldDisplayname.equals(newDisplayName)) {
                            user.addUpdateDisplayName(serverID, newDisplayName);
                        }
                    } else {
                        SimpleBot.log.warn("UUID mismatch returned from Server:{0} . Require manual checks : OLD: {1} NEW: {2}",
                                serverID, user.toString(), player.getValue().toString());
                    }
                    UserManager.saveUser(user);//save updated User
                }
                break;
            default:
                //no default handler
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
