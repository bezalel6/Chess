package ver9.view.Dialog.Dialogs.LoginProcess.Cards;

import ver9.SharedClasses.LoginInfo;
import ver9.SharedClasses.LoginType;
import ver9.view.Dialog.Cards.CardHeader;
import ver9.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver9.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver9.view.Dialog.Dialogs.LoginProcess.LoginProcess;

public class Login extends LoginCard {

    public Login(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Login"), parentDialog, loginInfo, LoginType.LOGIN);

        addDialogComponent(new UsernamePnl(false, loginInfo, parentDialog));
        addDialogComponent(new PasswordPnl(false, loginInfo, parentDialog));

    }
}
