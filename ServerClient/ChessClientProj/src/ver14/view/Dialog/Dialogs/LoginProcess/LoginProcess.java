package ver14.view.Dialog.Dialogs.LoginProcess;

import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.networking.AppSocket;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.DialogProperties;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.CancelAndExit;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.Guest;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.Login;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.Register;

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
