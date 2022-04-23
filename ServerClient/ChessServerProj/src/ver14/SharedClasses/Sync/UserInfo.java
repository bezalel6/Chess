package ver14.SharedClasses.Sync;

import java.io.Serializable;

/*
 * UserInfo
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * UserInfo -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * UserInfo -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

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
