package au.com.addstar.discord.socketServer;

import au.com.addstar.discord.DiscordBungee;
import au.com.addstar.discord.messages.IMessage;
import au.com.addstar.discord.messages.UpdatePlayersMessage;
import au.com.addstar.discord.objects.McUser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.cubespace.geSuit.geSuit;
import net.cubespace.geSuit.managers.APIManager;

import java.util.*;

public class MessageHandler extends ChannelInboundHandlerAdapter  {



    /**
     * Creates a client-side handler.
     */

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        IMessage o = (IMessage) msg;
        if(o.getMessageType().equals("UpdatePlayers")){
            UpdatePlayersMessage updateMsg = (UpdatePlayersMessage) o;
            Map<String, McUser> playersToUpdate = updateMsg.getPlayers();
            List<String> uuidsToResolve = new ArrayList<>();
            for (Map.Entry<String,McUser> e : playersToUpdate.entrySet()){
                UUID uuid = null;
                String name = e.getKey();
                if(e.getValue() !=null)uuid = e.getValue().getMinecraftUUID();
                if(uuid==null){
                    uuidsToResolve.add(name);
                }
            }
            Map<String,UUID> updatedNames = APIManager.resolveNames(uuidsToResolve);
            for (Map.Entry<String, UUID> e : updatedNames.entrySet()){
                boolean updated = false;
                for(Map.Entry<String, McUser> u: playersToUpdate.entrySet()){
                    if(u.getValue().getMinecraftUUID().equals(e.getValue())){
                        u.getValue().addUpdateDisplayName(updateMsg.getServerID(),e.getKey());
                        updated = true;
                    }
                    if(updated)break;
                }

            }
            ctx.write(updateMsg);
            ctx.close();
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
