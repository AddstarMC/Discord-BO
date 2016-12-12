package au.com.addstar.SimpleBot.objects;

import java.util.UUID;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 12/12/2016.
 */
public class Invitation {

    public Invitation(UUID uuid, String displayName, Long expiryTime){
        this.uuid = uuid;
        this.userName = displayName;
        this.expiryTime = expiryTime;
    }
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    private UUID uuid;
    private String userName;
    private long expiryTime;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Invitation) {
            Invitation test = (Invitation) obj;
            return uuid.equals(test.getUuid());
        }
        return false;
    }

}
