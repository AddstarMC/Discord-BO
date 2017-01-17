package au.com.addstar.discord.socketClient;

import au.com.addstar.discord.SimpleBot;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import au.com.addstar.discord.messages.IMessage;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 28/12/2016.
 */
public class BungeeComClient {

    private NioEventLoopGroup workerGroup = new NioEventLoopGroup(5);

    public String getDiscordHost() {
        return discordHost;
    }

    private final String discordHost;

    public int getPort() {
        return port;
    }

    final int port;
    ChannelFuture future;


    public BungeeComClient(String ghost, int gport) throws Exception {
        Bootstrap b = new Bootstrap();
        b.group(workerGroup)
                .channel(LocalServerChannel.class)
                .option(ChannelOption.SO_BACKLOG, 5)
                .option(ChannelOption.SO_KEEPALIVE, true);
        port = gport;
        discordHost = ghost;
        b.localAddress(port);
        SimpleBot.log.info("Started BungeeComClient on port {}", port);
        try {
            openConnection(b);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
    public void send(IMessage msg) {
            future.channel().write(msg);
    }
    public void closeConnection() throws Exception{
        future.channel().closeFuture().sync();
    }
    public void openConnection(Bootstrap b) throws Exception{
            future = b.connect(discordHost, port).sync();
    }

}