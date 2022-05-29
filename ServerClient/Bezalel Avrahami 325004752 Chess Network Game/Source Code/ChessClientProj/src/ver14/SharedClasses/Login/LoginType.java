package ver14.SharedClasses.Login;


/**
 * represents a Login Type.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum LoginType {
    /**
     * Login.
     */
    LOGIN,
    /**
     * Register.
     */
    REGISTER,
    /**
     * Guest.
     */
    GUEST,
    /**
     * Cancel.
     */
    CANCEL,
    /**
     * Not set yet.
     */
    NOT_SET_YET;

    /**
     * is this login type of user. not a guest.
     *
     * @return <code>true</code> if this login type is of a user
     */
    public boolean asUser() {
        return this == LOGIN || this == REGISTER;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return name();
    }
}
