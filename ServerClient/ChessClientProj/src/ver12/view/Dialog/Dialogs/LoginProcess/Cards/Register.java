package ver12.view.Dialog.Dialogs.LoginProcess.Cards;

import ver12.SharedClasses.LoginInfo;
import ver12.SharedClasses.LoginType;
import ver12.view.Dialog.Cards.CardHeader;
import ver12.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver12.view.Dialog.Dialogs.LoginProcess.Components.Register.ConfirmPasswordPnl;
import ver12.view.Dialog.Dialogs.LoginProcess.Components.Register.RegisterUsernamePnl;
import ver12.view.Dialog.Dialogs.LoginProcess.LoginProcess;

public class Register extends LoginCard {

    public Register(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Register"), parentDialog, loginInfo, LoginType.REGISTER);

        addDialogComponent(new RegisterUsernamePnl(loginInfo, this));

        PasswordPnl passwordPnl = new PasswordPnl(true, loginInfo, this);
        addDialogComponent(passwordPnl);
        addDialogComponent(new ConfirmPasswordPnl(loginInfo, passwordPnl::getPassword, this));

    }

}
