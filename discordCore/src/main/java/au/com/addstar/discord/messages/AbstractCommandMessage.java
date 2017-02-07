package au.com.addstar.discord.messages;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 17/01/2017.
 */
public abstract class AbstractCommandMessage implements IMessage{

    private static final long serialVersionUID = -3972431229741282241L;
    private MessageType type;
    private String serverID;
    private long messageId;
    private CommandType command;

    AbstractCommandMessage(CommandType type, String serverID, long messageId){
        this.type = MessageType.Command;
        this.serverID = serverID;
        this.messageId = messageId;
        command = type;
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
    public String getServerID() {
        return serverID;
    }

    @Override
    public long getMessageId() {
        return messageId;
    }

    public CommandType getCommandType() {
         return command;
    }
}
