package ver14.view.Dialog;

import ver14.SharedClasses.DBActions.Arg.Config;
import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Networking.AppSocket;
import ver14.SharedClasses.UI.MyJframe.MyJFrame;

import java.awt.*;

/**
 * stores all kinds of important Dialog properties. like the current client's login info (if any), the socket to server so the dialogs can communicate directly with the server
 * dialog details, and more.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class DialogProperties {
    /**
     * The Login info.
     */
    private final LoginInfo loginInfo;
    /**
     * The Socket to server.
     */
    private final AppSocket socketToServer;
    /**
     * The Parent win.
     */
    private final MyJFrame parentWin;
    /**
     * The Details.
     */
    private final DialogDetails dialogDetails;
    /**
     * The Content pane.
     */
    private Container contentPane;
    /**
     * The Arg config.
     */
    private Config<?> argConfig;

    /**
     * Instantiates a new Properties.
     *
     * @param dialogDetails the details
     */
    public DialogProperties(DialogDetails dialogDetails) {
        this(null, null, null, dialogDetails);
    }

    /**
     * Instantiates a new Properties.
     *
     * @param loginInfo      the login info
     * @param socketToServer the socket to server
     * @param parentWin      the parent win
     * @param dialogDetails  the details
     */
    public DialogProperties(LoginInfo loginInfo, AppSocket socketToServer, MyJFrame parentWin,
                            DialogDetails dialogDetails) {
        this.loginInfo = loginInfo;
        this.socketToServer = socketToServer;
        this.parentWin = parentWin;
        this.dialogDetails = dialogDetails;
    }

    /**
     * Gets login info.
     *
     * @return the login info
     */
    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    /**
     * Gets content pane.
     *
     * @return the content pane
     */
    public Container getContentPane() {
        return contentPane;
    }

    /**
     * Sets content pane.
     *
     * @param contentPane the content pane
     */
    public void setContentPane(Container contentPane) {
        this.contentPane = contentPane;
    }

    /**
     * Arg config config.
     *
     * @return the config
     */
    public Config<?> argConfig() {
        return argConfig;
    }

    /**
     * Sets arg config.
     *
     * @param argConfig the arg config
     */
    public void setArgConfig(Config<?> argConfig) {
        this.argConfig = argConfig;
    }

    /**
     * Socket to server app socket.
     *
     * @return the app socket
     */
    public AppSocket socketToServer() {
        return socketToServer;
    }

    /**
     * Parent win my j frame.
     *
     * @return the my j frame
     */
    public MyJFrame parentWin() {
        return parentWin;
    }

    /**
     * Details details.
     *
     * @return the details
     */
    public DialogDetails details() {
        return dialogDetails;
    }

    @Override
    public String toString() {
        return "Properties[" +
                "socketToServer=" + socketToServer + ", " +
                "parentWin=" + parentWin + ", " +
                "details=" + dialogDetails + ']';
    }


}
