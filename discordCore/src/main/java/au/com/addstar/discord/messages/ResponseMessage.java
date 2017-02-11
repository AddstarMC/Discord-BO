package au.com.addstar.discord.messages;

import au.com.addstar.discord.messages.identifiers.CommandType;
import au.com.addstar.discord.messages.identifiers.MessageStatus;
import au.com.addstar.discord.messages.identifiers.MessageType;

/**
 *
 * Created for the AddstarMC Project.
 * Created by Narimm on 2/02/2017.
 */
public class ResponseMessage extends AbstractMessage {

    private static final long serialVersionUID = -4700433038445137309L;

    /**
     * Generic Response that can be used to signal a failure or simple response.
     *
     * @param sender the server ID
     * @param id the message ID
     */
    public ResponseMessage(String sender, long id) {
        this(null, sender, id, MessageStatus.FAIL, "Unknown Error");
    }

    /**
     * A detailed response that could be used for many endpoints.
     *
     * @param command the CommandType
     * @param senderID the server ID
     * @param id the message ID (usually set to the incoming Message ID
     * @param status the status ie OK or FAIL
     * @param message a simple message to return to the commander.
     */
    public ResponseMessage(CommandType command, String senderID, long id, MessageStatus status, String message) {
        super(command, MessageType.Response, senderID, id);
        setStatus(status);
        setMessage(message);
    }

    @Override
    public void setMessageType(MessageType mtype) {
        throw new UnsupportedOperationException("Cannot set Message type on Response");
    }

}

