package ver10.view.Dialog.Dialogs.LoginProcess.Cards;

import ver10.view.Dialog.Cards.CardHeader;
import ver10.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver10.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver10.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver10.SharedClasses.LoginInfo;
import ver10.SharedClasses.LoginType;

public class Login extends LoginCard {

    public Login(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Login"), parentDialog, loginInfo, LoginType.LOGIN);

        addDialogComponent(new UsernamePnl(false, loginInfo, parentDialog));
        addDialogComponent(new PasswordPnl(false, loginInfo, parentDialog));

    }
}
