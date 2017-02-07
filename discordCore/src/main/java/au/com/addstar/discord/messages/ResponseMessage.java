package au.com.addstar.discord.messages;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 2/02/2017.
 */
public class ResponseMessage implements IMessage {

    private static final long serialVersionUID = -4700433038445137309L;

    private long messageId;
    private String senderID;
    private CommandType type;
    private String error;

    public ResponseMessage(String sender, long id) {
        senderID = sender;
        messageId = id;
        code = ResponseTypes.OK;
        error = null;
    }
    public ResponseMessage(String sender, long id,ResponseTypes type, String error) {
        senderID = sender;
        messageId = id;
        code = type;
        this.error = error;
    }


    public void setResponse(ResponseTypes rtype){
        code = rtype;
    }

    public ResponseTypes getResponse(){
        return code;
    }

    @Override
    public MessageType getMessageType() {
        return  MessageType.Response;
    }

    @Override
    public void setMessageType(MessageType mtype) {
        throw new UnsupportedOperationException("Cannot set Message type on Response");
    }

    @Override
    public String getServerID() {
        return senderID;
    }

    @Override
    public long getMessageId() {
        return messageId;
    }

    public enum ResponseTypes{
        OK,
        FAIL,
    }

    public String getErrorMessage(){
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    private ResponseTypes code;

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }
}
