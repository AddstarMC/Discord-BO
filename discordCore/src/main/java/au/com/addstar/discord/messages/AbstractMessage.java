package au.com.addstar.discord.messages;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 17/01/2017.
 */
public abstract class AbstractMessage implements IMessage{

    private String type;
    private String serverID;

    AbstractMessage(String type, String serverID){
        this.type = type;
        this.serverID = serverID;
    }

    @Override
    public String getMessageType() {
        return type;
    }

    @Override
    public void setMessageType(String type) {
        this.type = type ;
    }

    @Override
    public String getServerID() {
        return serverID;
    }
}
