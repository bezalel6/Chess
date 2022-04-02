package ver14.view.Dialog.Dialogs.DialogProperties;

import ver14.SharedClasses.DBActions.Arg.Config;
import ver14.SharedClasses.Utils.ArrUtils;
import ver14.SharedClasses.networking.AppSocket;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Properties {
    private final AppSocket socketToServer;
    private final JFrame parentWin;
    private final Details details;
    private Container contentPane;
    private Config<?> argConfig;
    public Properties(AppSocket socketToServer, JFrame parentWin,
                      Details details) {
        this.socketToServer = socketToServer;
        this.parentWin = parentWin;
        this.details = details;
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
    public int hashCode() {
        return Objects.hash(socketToServer, parentWin, details);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Properties) obj;
        return Objects.equals(this.socketToServer, that.socketToServer) &&
                Objects.equals(this.parentWin, that.parentWin) &&
                Objects.equals(this.details, that.details);
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
