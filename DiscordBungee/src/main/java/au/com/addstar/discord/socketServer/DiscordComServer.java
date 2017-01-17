package au.com.addstar.discord.socketServer;

import au.com.addstar.discord.DiscordBungee;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 16/01/2017.
 */
public class DiscordComServer {


    /**
     * Created for use for the Add5tar MC Minecraft server
     * Created by benjamincharlton on 28/12/2016.
     */

    private DiscordBungee plugin;
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup(5);
    ChannelFuture future;
    public DiscordComServer(DiscordBungee p) throws Exception{
        plugin = p;
        String discordBotHost = plugin.config.getString("discordServerAddress", "localhost");
        int port = plugin.config.getInt("discordComPort", 11000);
        ServerBootstrap client = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .handler(new DiscordChannelInitializer())
                .option(ChannelOption.SO_BACKLOG, 5)
                .option(ChannelOption.SO_KEEPALIVE, true);
        try{
            future  = client.bind(discordBotHost,port).sync();
            p.log.info("Discord Connection available on :" + discordBotHost + " : " + port);

        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }
    public void closeConnection(){
        try {
            future.channel().closeFuture().sync();
            plugin.log.info("Discord Connection closed.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    public void finalize(){
        if(future.channel().isOpen())closeConnection();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        try{
            super.finalize();
        }catch (Throwable t){
           t.printStackTrace();
        }
    }


}
