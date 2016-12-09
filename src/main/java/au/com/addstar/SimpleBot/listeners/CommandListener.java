package au.com.addstar.SimpleBot.listeners;

import au.com.addstar.SimpleBot.SimpleBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.EnumSet;
import java.util.List;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/12/2016.
 */
public class CommandListener {

    private SimpleBot bot;

    public CommandListener(SimpleBot bot) {
        this.bot = bot;
    }

    @EventSubscriber
    public void commandListener(MessageReceivedEvent event) {
        IMessage m = event.getMessage();
        IUser u = m.getAuthor();
        IGuild g = m.getGuild();
        boolean admin = false;
        List<IRole> roles = u.getRolesForGuild(g);
        for (IRole r : roles) {
            EnumSet<Permissions> perms = r.getPermissions();
            if (perms.contains(Permissions.ADMINISTRATOR)) {
                admin = true;
            }
        }
        if (!admin) {
            return;
        }
        String prefix = bot.gConfigs.get(m.getGuild().getID()).getPrefix();
        if (m.getContent().startsWith(prefix)) {
            //this is a command we can respond to
            IChannel channel = m.getChannel();
            try {
                MessageBuilder builder = new MessageBuilder(SimpleBot.client).withChannel(u.getOrCreatePMChannel()).withContent("This is a discord command");
                builder.build();
            } catch (RateLimitException e) { // RateLimitException thrown. The bot is sending messages too quickly!
                System.err.print("Sending messages too quickly!");
                e.printStackTrace();
            } catch (DiscordException e) { // DiscordException thrown. Many possibilities. Use getErrorMessage() to see what went wrong.
                System.err.print(e.getErrorMessage()); // Print the error message sent by Discord
                e.printStackTrace();
            } catch (MissingPermissionsException e) { // MissingPermissionsException thrown. The bot doesn't have permission to send the message!
                System.err.print("Missing permissions for channel!");
                e.printStackTrace();
            }


        }

    }
}
