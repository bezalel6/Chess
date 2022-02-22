package ver10.view.Dialog.Dialogs.LoginProcess;

import ver10.SharedClasses.LoginInfo;
import ver10.SharedClasses.networking.AppSocket;
import ver10.view.Dialog.Cards.DialogCard;
import ver10.view.Dialog.Dialog;
import ver10.view.Dialog.DialogProperties;
import ver10.view.Dialog.Dialogs.LoginProcess.Cards.CancelAndExit;
import ver10.view.Dialog.Dialogs.LoginProcess.Cards.Guest;
import ver10.view.Dialog.Dialogs.LoginProcess.Cards.Login;
import ver10.view.Dialog.Dialogs.LoginProcess.Cards.Register;

import java.awt.*;

public class LoginProcess extends Dialog {
    private final LoginInfo loginInfo;

    public LoginProcess(AppSocket socketToServer, DialogProperties properties, Component parentWin) {
        super(socketToServer, properties, parentWin);

        loginInfo = new LoginInfo();

        DialogCard[] cards = {
                new Login(this, loginInfo),
                new Register(this, loginInfo),
                new Guest(this, loginInfo),
                new CancelAndExit(this, loginInfo)
        };

        navigationCardSetup(cards);
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }
}
