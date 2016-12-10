package au.com.addstar.SimpleBot.listeners;

import au.com.addstar.SimpleBot.SimpleBot;
import au.com.addstar.SimpleBot.objects.GuildConfig;
import au.com.addstar.SimpleBot.ulilities.Utility;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;

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
        if (m.getChannel().isPrivate()){
            Utility.sendPrivateMessage(u,"Commands must be used in a public server channel");
            return;
        }
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
            Utility.sendPrivateMessage(u,"You're note an admin...this has been logged....");
            return;
        }

        GuildConfig config = bot.gConfigs.get(g.getID());
        String prefix = config.getPrefix();
        String message =  m.getContent();
        if (message.startsWith(prefix)) {
            message = message.substring(2);
            //this is a command we can respond to
            IChannel channel = m.getChannel();
            //Utility.sendPrivateMessage(u, "This is a discord message");
            String[] mSplit = message.split("\\s+");
            switch (mSplit[0].toLowerCase()){
                case "set":
                    if(mSplit.length > 1) {
                        switch (mSplit[1].toLowerCase()) {
                            case "prefix":
                                String oldPrefix = config.getPrefix();
                                if(mSplit.length > 2){
                                    String newPrefix = mSplit[2];
                                    config.setPrefix(newPrefix);
                                    config.saveConfig();
                                    Utility.sendPrivateMessage(u,"Prefix updated Old: " +oldPrefix +"New: " + config.getPrefix());
                                }else{
                                    Utility.sendPrivateMessage(u,"Current Prefix is : "+oldPrefix);
                                }
                                break;
                            case "welcomemessage":
                                String oldMessage = config.getWelcomeMessage();
                                if(mSplit.length > 2){
                                    StringBuilder out =  new StringBuilder();
                                    for (int i = 2; mSplit.length > i; i++) {
                                        out.append(mSplit[i] + " ");
                                    }
                                    String newMessage = out.toString();
                                    config.setWelcomeMessage(newMessage);
                                    config.saveConfig();
                                    Utility.sendPrivateMessage(u,"Prefix updated Old: " +oldMessage +"New: " + config.getWelcomeMessage());
                                }else{
                                    Utility.sendPrivateMessage(u,"Current Message is : "+oldMessage);
                                }
                                break;
                            default:
                                //sendSetHelp()
                        }
                    }else{
                        //sendhelp()
                    }

                    break;
                default:
                    //sendHelp()

            }

        }

    }
}
