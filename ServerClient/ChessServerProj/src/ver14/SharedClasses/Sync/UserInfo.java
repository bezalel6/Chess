package ver14.SharedClasses.Sync;

import java.io.Serializable;


public class UserInfo implements SyncableItem, Serializable {
    public final String id;
    public final String profilePic;

    public UserInfo(String id) {
        this(id, null);
    }

    public UserInfo(String id, String profilePic) {
        this.id = id;
        this.profilePic = profilePic;
    }

    @Override
    public String ID() {
        return id;
    }
}
