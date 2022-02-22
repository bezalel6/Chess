package ver11.view.Dialog.Dialogs.LoginProcess.Cards;

import ver11.SharedClasses.LoginInfo;
import ver11.SharedClasses.LoginType;
import ver11.view.Dialog.Cards.CardHeader;
import ver11.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver11.view.Dialog.Dialogs.LoginProcess.Components.Register.ConfirmPasswordPnl;
import ver11.view.Dialog.Dialogs.LoginProcess.Components.Register.RegisterUsernamePnl;
import ver11.view.Dialog.Dialogs.LoginProcess.LoginProcess;

public class Register extends LoginCard {

    public Register(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Register"), parentDialog, loginInfo, LoginType.REGISTER);

        addDialogComponent(new RegisterUsernamePnl(loginInfo, this));

        PasswordPnl passwordPnl = new PasswordPnl(true, loginInfo, this);
        addDialogComponent(passwordPnl);
        addDialogComponent(new ConfirmPasswordPnl(loginInfo, passwordPnl::getPassword, this));

    }

}
