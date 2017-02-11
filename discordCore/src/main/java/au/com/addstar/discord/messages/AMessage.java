package au.com.addstar.discord.messages;

import au.com.addstar.discord.messages.identifiers.CommandType;
import au.com.addstar.discord.messages.identifiers.MessageStatus;
import au.com.addstar.discord.messages.identifiers.MessageType;

import java.io.Serializable;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 17/01/2017.
 */
public interface AMessage extends Serializable {

    MessageType getMessageType();

    void setMessageType(MessageType type);

    String getSourceId();

    long getMessageId();

    MessageStatus getStatus();

    CommandType getCommandType();

    String getMessage();

    void setMessage(String message);

    String getErrorMessage();
}
