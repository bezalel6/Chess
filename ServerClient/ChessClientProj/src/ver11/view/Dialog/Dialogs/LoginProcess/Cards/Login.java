package ver11.view.Dialog.Dialogs.LoginProcess.Cards;

import ver11.SharedClasses.LoginInfo;
import ver11.SharedClasses.LoginType;
import ver11.view.Dialog.Cards.CardHeader;
import ver11.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver11.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver11.view.Dialog.Dialogs.LoginProcess.LoginProcess;

public class Login extends LoginCard {

    public Login(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Login"), parentDialog, loginInfo, LoginType.LOGIN);

        addDialogComponent(new UsernamePnl(false, loginInfo, parentDialog));
        addDialogComponent(new PasswordPnl(false, loginInfo, parentDialog));

    }
}
