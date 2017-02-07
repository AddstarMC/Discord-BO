package au.com.addstar.discord.messages;

import java.io.Serializable;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 17/01/2017.
 */
public interface IMessage extends Serializable {


    /**
     * Should return the message type
     *
     * @return String
     */
    MessageType getMessageType();

    void setMessageType(MessageType type);

    String getServerID();

    long getMessageId();
}
