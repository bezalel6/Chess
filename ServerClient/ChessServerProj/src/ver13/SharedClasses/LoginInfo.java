package ver13.SharedClasses;

import java.io.Serializable;

public class LoginInfo implements Serializable {
    private String username, password;
    private LoginType loginType;

    public LoginInfo() {
    }

    public LoginInfo(String username, String password, LoginType loginType) {
        this.username = username;
        this.password = password;
        this.loginType = loginType;
    }

    public LoginInfo(LoginType loginType) {
        this.loginType = loginType;
    }

    public boolean isGuest() {
        return loginType == LoginType.GUEST;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "LoginInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", loginType=" + loginType +
                '}';
    }

    public boolean asUser() {
        return loginType.asUser();
    }
}