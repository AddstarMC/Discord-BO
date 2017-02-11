package au.com.addstar.discord.redis;

import au.com.addstar.discord.SimpleBot;
import au.com.addstar.discord.managers.InvitationManager;
import au.com.addstar.discord.messages.AMessage;
import au.com.addstar.discord.messages.InviteMessage;
import au.com.addstar.discord.messages.ResponseMessage;
import au.com.addstar.discord.messages.identifiers.CommandType;
import au.com.addstar.discord.messages.identifiers.MessageStatus;
import au.com.addstar.discord.messages.identifiers.MessageType;
import au.com.addstar.discord.objects.Guild;
import au.com.addstar.discord.objects.Invitation;
import au.com.addstar.discord.ulilities.Utility;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IInvite;

import static au.com.addstar.discord.managers.InvitationManager.checkforInvite;
import static au.com.addstar.discord.managers.InvitationManager.createInvite;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 8/02/2017.
 */
public class InviteHandler implements IncomingCommandHandler {

    private final Guild guild;
    public InviteHandler(Guild guild) {
        this.guild = guild;
    }

    @Override
    public AMessage onCommand(AMessage message) {
        if(message.getCommandType() == CommandType.INVITE && message.getMessageType() == MessageType.Command){
            InviteMessage iMessage = (InviteMessage) message;
           String sourceID =  iMessage.getSourceId();
            Long messageId = message.getMessageId();
            IChannel channel = SimpleBot.client.getChannelByID(guild.getConfig().getInviteChannelId());
            if (channel == null) {
                return new ResponseMessage(CommandType.INVITE,sourceID,messageId, MessageStatus.FAIL,"ChannelID is not Valid");
            }
            IInvite invite;
            if (sourceID.equals(guild.getConfig().getBungeeId())) {
                int expiry = guild.getConfig().getExpiryTime();
                Invitation pendingInvitation = InvitationManager.checkForUUIDInvite(guild.getConfig(), iMessage.getUid());
                if (pendingInvitation != null && pendingInvitation.hasExpired()) {
                    guild.redisManager.getLog().info("Pending Invite Code: " + pendingInvitation.getInviteCode() + "had expired and is being removed. Expiry: " + Utility.getDate(pendingInvitation.getExpiryTime()));
                    InvitationManager.removeInvitation(guild.getConfig(), pendingInvitation.getInviteCode());
                    pendingInvitation = null;
                } else if (pendingInvitation != null) {
                    SimpleBot.log.info("Stored Invite will be returned for " + pendingInvitation.getUserName() + " : " + pendingInvitation.getInviteCode());
                    InviteMessage iresponse = new InviteMessage(MessageType.Response, sourceID, messageId, iMessage.getUid());
                    iresponse.setInviteCode(pendingInvitation.getInviteCode());
                    invite = checkforInvite(channel, pendingInvitation);
                    if (invite == null) {
                        InvitationManager.removeInvitation(guild.getConfig(), pendingInvitation.getInviteCode());
                        SimpleBot.log.info("Discord Invite associated with stored Invite had expired.  Detail:" + pendingInvitation.getUserName() + " : " + pendingInvitation.getInviteCode());
                        pendingInvitation = null;
                    } else {
                        return iresponse;
                    }
                }
                if(pendingInvitation == null){
                    SimpleBot.log.info("Generating new invite for uid " + iMessage.getUid());
                    invite = createInvite(channel, expiry, 1, false);
                    if (invite != null) {
                        Long expiryTime = System.currentTimeMillis() + (guild.getConfig().getExpiryTime() * 1000);
                        InviteMessage iresponse =  new InviteMessage(MessageType.Response,iMessage.getSourceId(),iMessage.getMessageId(),iMessage.getUid());
                        iresponse.setDisplayName(iMessage.getMessage());//set displayname
                        iresponse.setInviteCode(invite.getInviteCode());
                        InvitationManager.storeInvitation(guild.getConfig(),new Invitation(iMessage.getUid(), iMessage.getMessage(), expiryTime, invite.getInviteCode()));
                        return iresponse;
                    }else{
                        return new ResponseMessage(CommandType.INVITE,sourceID,messageId,MessageStatus.FAIL,"Invite generation failed");
                    }

                }

           }
            return  new ResponseMessage(CommandType.INVITE,sourceID,messageId,MessageStatus.FAIL,"Unknown error");
        }
        return new ResponseMessage(message.getCommandType(),message.getSourceId(),message.getMessageId(),MessageStatus.FAIL,"Invite handler cannot handle: " +message.getCommandType().toString());
    }
}
