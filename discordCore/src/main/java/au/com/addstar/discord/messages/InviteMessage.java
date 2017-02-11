package au.com.addstar.discord.messages;

import au.com.addstar.discord.messages.identifiers.CommandType;
import au.com.addstar.discord.messages.identifiers.MessageType;

import java.util.UUID;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 8/02/2017.
 */
public class InviteMessage extends AbstractMessage {

    private static final long serialVersionUID = -8096292571095786972L;
    private UUID uid;

    private String inviteCode;

    public InviteMessage(MessageType type, String serverID, long messageId, UUID uid) {
        super(CommandType.INVITE, type, serverID, messageId);
    }
    public String getInviteCode() {
        return inviteCode;
    }
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public UUID getUid() {
        return uid;
    }

    public void setDisplayName(String name){
        super.setMessage(name);
    }

    public String getDisplayName(){
        return super.getMessage();
    }


}
