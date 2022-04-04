package ver14.view.Dialog.Dialogs.LoginProcess.Cards;

import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.LoginType;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver14.view.Dialog.Dialogs.LoginProcess.LoginProcess;

public class Login extends LoginCard {

    public Login(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Login"), parentDialog, loginInfo, LoginType.LOGIN);

        addDialogComponent(new UsernamePnl(false, loginInfo, parentDialog));
        addDialogComponent(new PasswordPnl(false, loginInfo, parentDialog));

    }
}
