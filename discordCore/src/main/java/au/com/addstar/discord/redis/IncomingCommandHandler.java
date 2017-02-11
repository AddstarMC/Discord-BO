package au.com.addstar.discord.redis;

import au.com.addstar.discord.messages.AMessage;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 2/02/2017.
 */
public interface IncomingCommandHandler {

    AMessage onCommand(AMessage message);

}
