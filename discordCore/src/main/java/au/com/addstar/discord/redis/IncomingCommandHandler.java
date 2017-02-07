package au.com.addstar.discord.redis;

import au.com.addstar.discord.messages.IMessage;
import au.com.addstar.discord.messages.ResponseMessage;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 2/02/2017.
 */
public interface IncomingCommandHandler {

    ResponseMessage onCommand(IMessage message);

}
