package ver14.SharedClasses.Login;


/**
 * Login Type.
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
     * As user boolean.
     *
     * @return the boolean
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
