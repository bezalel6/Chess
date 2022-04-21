package ver14.view.Dialog.Dialogs.LoginProcess;

import ver14.SharedClasses.LoginInfo;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.CancelAndExit;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.Guest;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.Login;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.Register;
import ver14.view.Dialog.Properties;

public class LoginProcess extends Dialog {
    private final LoginInfo loginInfo;

    public LoginProcess(Properties properties) {
        super(properties);

        loginInfo = new LoginInfo();

        DialogCard[] cards = {
                new Login(this, loginInfo),
                new Register(this, loginInfo),
                new Guest(this, loginInfo),
                new CancelAndExit(this, loginInfo)
        };

        navigationCardSetup(cards);
    }

//    @Override
//    public BackOkPnl backOkPnl() {
//        return null;
//    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }
}
