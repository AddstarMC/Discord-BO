package au.com.addstar.discord.redisHandlers;

import au.com.addstar.discord.messages.AMessage;
import au.com.addstar.discord.messages.InviteMessage;
import au.com.addstar.discord.messages.identifiers.CommandType;
import au.com.addstar.discord.objects.Invitation;
import com.google.common.base.Function;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 8/02/2017.
 */
public class ResponseHandlers {


    public static Function<AMessage, Invitation> InviteResponseHandler(){
        return message -> {
            if(message.getCommandType() != CommandType.INVITE)return null;
            InviteMessage imessage = (InviteMessage) message;
                return new Invitation(imessage.getUid(),null,0L,imessage.getInviteCode());
        };
    }
}
