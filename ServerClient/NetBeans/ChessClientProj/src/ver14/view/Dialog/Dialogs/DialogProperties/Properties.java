package ver14.view.Dialog.Dialogs.DialogProperties;

import ver14.SharedClasses.DBActions.Arg.Config;
import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.Utils.ArrUtils;
import ver14.SharedClasses.networking.AppSocket;

import javax.swing.*;
import java.awt.*;

public class Properties {
    private final LoginInfo loginInfo;
    private final AppSocket socketToServer;
    private final JFrame parentWin;
    private final Details details;
    private Container contentPane;
    private Config<?> argConfig;

    public Properties(Details details) {
        this(null, null, null, details);
    }

    public Properties(LoginInfo loginInfo, AppSocket socketToServer, JFrame parentWin,
                      Details details) {
        this.loginInfo = loginInfo;
        this.socketToServer = socketToServer;
        this.parentWin = parentWin;
        this.details = details;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public Container getContentPane() {
        return contentPane;
    }

    public void setContentPane(Container contentPane) {
        this.contentPane = contentPane;
    }

    public Config<?> argConfig() {
        return argConfig;
    }

    public void setArgConfig(Config<?> argConfig) {
        this.argConfig = argConfig;
    }

    public AppSocket socketToServer() {
        return socketToServer;
    }

    public JFrame parentWin() {
        return parentWin;
    }

    public Details details() {
        return details;
    }

    @Override
    public String toString() {
        return "Properties[" +
                "socketToServer=" + socketToServer + ", " +
                "parentWin=" + parentWin + ", " +
                "details=" + details + ']';
    }


    public record Details(String header, String title, String error) {

        /**
         * @param details []/[header]/[header,title]/[header,title,error]
         */
        public Details(String... details) {
            this(ArrUtils.exists(details, 0),
                    ArrUtils.exists(details, 1),
                    ArrUtils.exists(details, 2));
        }

    }


}
