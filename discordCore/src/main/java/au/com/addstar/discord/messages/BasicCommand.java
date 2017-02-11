package au.com.addstar.discord.messages;

import au.com.addstar.discord.messages.AbstractMessage;
import au.com.addstar.discord.messages.identifiers.CommandType;
import au.com.addstar.discord.messages.identifiers.MessageType;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 8/02/2017.
 */
public class BasicCommand extends AbstractMessage {


    private static final long serialVersionUID = -6489233995380930693L;

    public BasicCommand(CommandType command, String serverID, long messageId) {
        super(command, MessageType.Command, serverID, messageId);
    }

    @Override
    public void setMessageType(MessageType type) {
        throw new UnsupportedOperationException("Cannot set Message type on BasicCommand");
    }
}
