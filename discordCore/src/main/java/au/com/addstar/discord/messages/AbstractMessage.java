package au.com.addstar.discord.messages;

import au.com.addstar.discord.messages.identifiers.CommandType;
import au.com.addstar.discord.messages.identifiers.MessageStatus;
import au.com.addstar.discord.messages.identifiers.MessageType;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 17/01/2017.
 */
public abstract class AbstractMessage implements AMessage {

    private static final long serialVersionUID = -3972431229741282241L;
    private MessageType type;
    private MessageStatus status;
    private final String sourceId;
    private final long messageId;
    private final CommandType command;
    private String message;

    AbstractMessage(CommandType command, MessageType type, String sourceId, long messageId){
        this.type = type;
        this.command = command;
        this.sourceId = sourceId;
        this.messageId = messageId;
    }

    @Override
    public MessageType getMessageType() {
        return type;
    }

    @Override
    public void setMessageType(MessageType type) {
        this.type = type ;
    }

    @Override
    public String getSourceId() {
        return sourceId;
    }

    @Override
    public long getMessageId() {
        return messageId;
    }

    @Override
    public MessageStatus getStatus() {
        return status;
    }

    void setStatus(MessageStatus status) {
        this.status = status;
    }

    @Override
    public CommandType getCommandType() {
         return command;
    }
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getErrorMessage(){
        if(this.getStatus() == MessageStatus.FAIL){
            return message;
        }else{
            return null;
        }

    }
    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
