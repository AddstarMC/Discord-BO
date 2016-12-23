package au.com.addstar.discord.objects;

import java.util.UUID;

/**
 * Created for the AddstarMC network
 * Created by Narimm on 12/12/2016.
 */

public class Invitation{

    private final UUID uuid;
    private String userName;
    private final long expiryTime;
    private final String inviteCode;

    public Invitation(UUID uuid, String displayName, Long expiryTime, String inviteCode) {
        this.uuid = uuid;
        this.userName = displayName;
        this.expiryTime = expiryTime;
        this.inviteCode = inviteCode;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public boolean hasExpired(){
        return expiryTime <= System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Invitation) {
            Invitation test = (Invitation) obj;
            return uuid.equals(test.getUuid())
                    &&userName.equals(test.getUserName())
                    &&inviteCode.equals(test.getInviteCode())
                    &&expiryTime == test.getExpiryTime();
        }
        return false;
    }

}
