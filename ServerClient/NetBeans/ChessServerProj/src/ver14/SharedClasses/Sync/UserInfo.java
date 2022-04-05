package ver14.SharedClasses.Sync;

import java.io.Serializable;

public class UserInfo implements SyncableItem, Serializable {
    public final String id;

    public UserInfo(String id) {
        this.id = id;
    }

    @Override
    public String ID() {
        return id;
    }
}
