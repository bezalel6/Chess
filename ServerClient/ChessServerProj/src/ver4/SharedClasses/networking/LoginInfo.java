package ver4.SharedClasses.networking;

import java.io.Serializable;

public class LoginInfo implements Serializable {
    private String username, password;
    private boolean guest, ai, cancel;

    public LoginInfo(String username, String password, boolean ai) {
        this.username = username;
        this.password = password;
        this.ai = ai;
        this.cancel = false;
    }

    public LoginInfo(boolean ai) {
        this.ai = ai;
        this.guest = true;
        this.cancel = false;
    }

    public LoginInfo() {
        this.cancel = true;
    }

    public boolean isCancel() {
        return cancel;
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

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    public boolean isAi() {
        return ai;
    }

    public void setAi(boolean ai) {
        this.ai = ai;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", guest=" + guest +
                ", ai=" + ai +
                ", cancel=" + cancel +
                '}';
    }
}