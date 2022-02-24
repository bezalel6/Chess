package ver12.view.Dialog.Dialogs.LoginProcess.Cards;

import ver12.SharedClasses.LoginInfo;
import ver12.SharedClasses.LoginType;
import ver12.view.Dialog.Cards.CardHeader;
import ver12.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver12.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver12.view.Dialog.Dialogs.LoginProcess.LoginProcess;

public class Login extends LoginCard {

    public Login(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Login"), parentDialog, loginInfo, LoginType.LOGIN);

        addDialogComponent(new UsernamePnl(false, loginInfo, parentDialog));
        addDialogComponent(new PasswordPnl(false, loginInfo, parentDialog));

    }
}
