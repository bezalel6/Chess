package ver8.view.Dialog.Dialogs.LoginProcess.Cards;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.LoginType;
import ver8.view.Dialog.Cards.CardHeader;
import ver8.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver8.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver8.view.Dialog.Dialogs.LoginProcess.LoginProcess;

public class Login extends LoginCard {

    public Login(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Login"), parentDialog, loginInfo, LoginType.LOGIN);

        addDialogComponent(new UsernamePnl(false, loginInfo, parentDialog));
        addDialogComponent(new PasswordPnl(false, loginInfo, parentDialog));

    }
}
