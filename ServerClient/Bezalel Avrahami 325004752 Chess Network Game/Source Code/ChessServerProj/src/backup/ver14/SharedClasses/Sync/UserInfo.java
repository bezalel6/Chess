package ver14.SharedClasses.Sync;

import java.io.Serializable;


/**
 * represents syncable User information.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class UserInfo implements SyncableItem, Serializable {
    /**
     * The Id.
     */
    public final String id;
    /**
     * The Profile pic.
     */
    public final String profilePic;

    /**
     * Instantiates a new User info.
     *
     * @param id the id
     */
    public UserInfo(String id) {
        this(id, null);
    }

    /**
     * Instantiates a new User info.
     *
     * @param id         the id
     * @param profilePic the profile pic
     */
    public UserInfo(String id, String profilePic) {
        this.id = id;
        this.profilePic = profilePic;
    }

    /**
     * Id string.
     *
     * @return the string
     */
    @Override
    public String ID() {
        return id;
    }
}
