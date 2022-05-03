package ver14.SharedClasses.Login;

import java.io.Serializable;


/**
 * Login info.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class LoginInfo implements Serializable {
    /**
     * The Username.
     */
    private String username, /**
     * The Password.
     */
    password;
    /**
     * The Login type.
     */
    private LoginType loginType;
    /**
     * The Profile pic.
     */
    private String profilePic;

    /**
     * Instantiates a new Login info.
     */
    public LoginInfo() {
        this.loginType = LoginType.NOT_SET_YET;
    }

    /**
     * Instantiates a new Login info.
     *
     * @param username  the username
     * @param password  the password
     * @param loginType the login type
     */
    public LoginInfo(String username, String password, LoginType loginType) {
        this.username = username;
        this.password = password;
        this.loginType = loginType;
    }

    /**
     * Instantiates a new Login info.
     *
     * @param loginType the login type
     */
    public LoginInfo(LoginType loginType) {
        this.loginType = loginType;
    }

    /**
     * Gets profile pic.
     *
     * @return the profile pic
     */
    public String getProfilePic() {
        return profilePic;
    }

    /**
     * Sets profile pic.
     *
     * @param profilePic the profile pic
     */
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    /**
     * Is guest boolean.
     *
     * @return the boolean
     */
    public boolean isGuest() {
        return loginType == LoginType.GUEST;
    }

    /**
     * Gets login type.
     *
     * @return the login type
     */
    public LoginType getLoginType() {
        return loginType;
    }

    /**
     * Sets login type.
     *
     * @param loginType the login type
     */
    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "LoginInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", loginType=" + loginType +
                '}';
    }

    /**
     * As user boolean.
     *
     * @return the boolean
     */
    public boolean asUser() {
        return loginType.asUser();
    }

    /**
     * Init debug login values.
     */
    public void initDebugLoginValues() {
        setLoginType(LoginType.LOGIN);
        setUsername("testing");
        setPassword("123456");
    }
}