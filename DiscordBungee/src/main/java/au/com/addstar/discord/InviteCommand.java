package au.com.addstar.discord;

import au.com.addstar.discord.messages.AMessage;
import au.com.addstar.discord.messages.InviteMessage;
import au.com.addstar.discord.messages.identifiers.MessageType;
import au.com.addstar.discord.objects.Invitation;
import au.com.addstar.discord.redisHandlers.ResponseHandlers;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.cubespace.geSuit.managers.PlayerManager;
import net.cubespace.geSuit.objects.GSPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 8/02/2017.
 */
class InviteCommand extends Command {

    private final DiscordBungee plugin;

    public InviteCommand(DiscordBungee plugin) {
        super("discordinvite", "discordinvite.request", "dinvite");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0) {

            if (!commandSender.hasPermission("discordinvite.request.self")) return;
            if (commandSender instanceof ProxiedPlayer) {
                runInviteCommand(commandSender,(ProxiedPlayer) commandSender);
            }
        }else{
            if (!commandSender.hasPermission("discordinvite.request.other")) return;
            String playerName = args[1];
            if(!plugin.gHooked){
                commandSender.sendMessage(TextComponent.fromLegacyText("No Gesuit available to find a player"));
            }else{
                GSPlayer player = PlayerManager.matchOnlinePlayer(playerName);
                if(player == null){
                    commandSender.sendMessage(TextComponent.fromLegacyText("No Player available by that name"));
                }else{
                    runInviteCommand(commandSender, player.getProxiedPlayer());
                }

            }


        }
    }

    private void runInviteCommand(CommandSender sender, ProxiedPlayer target){
        InviteMessage message = new InviteMessage(MessageType.Command, plugin.getProxy().getName(), plugin.redisManager.getNextCommandId(), target.getUniqueId());
        message.setDisplayName(target.getDisplayName());
        ListenableFuture<AMessage> future = plugin.redisManager.sendCommand(plugin.getProxy().getName(), message);
        ListenableFuture<Invitation> inviteFuture = Futures.transform(future, ResponseHandlers.InviteResponseHandler());
        Futures.addCallback(inviteFuture, new FutureCallback<Invitation>() {
            @Override
            public void onSuccess(Invitation result) {
                target.sendMessage(TextComponent.fromLegacyText("You will require a discord account. You cannot use the invite before creating an account"));
                target.sendMessage(TextComponent.fromLegacyText("https://discordapp.com/register"));
                target.sendMessage(TextComponent.fromLegacyText("A invititation to discord has been generated"));
                target.sendMessage(TextComponent.fromLegacyText("http:/discord.gg/" + result.getInviteCode()));
                if(sender != target)sender.sendMessage(TextComponent.fromLegacyText("Invite Code: " + result.getInviteCode()));
                target.sendMessage(TextComponent.fromLegacyText("Once you have joined type !!register "+result.getInviteCode()));
                target.sendMessage(TextComponent.fromLegacyText("This will give you the ability to voice chat with other members."));
            }
            @Override
            public void onFailure(@NotNull Throwable t) {
                sender.sendMessage(TextComponent.fromLegacyText("Invitation generation failed.  Please ask place an admin ticket asking for help."));
                plugin.getLogger().log(Level.SEVERE,t.getMessage()+" Sender:"+ sender.getName() + " Target: " + target.getName());

            }
        });

    }
}
