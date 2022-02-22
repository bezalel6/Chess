package ver10.view.Dialog.Dialogs.LoginProcess.Cards;

import ver10.view.Dialog.Cards.CardHeader;
import ver10.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver10.view.Dialog.Dialogs.LoginProcess.Components.Register.ConfirmPasswordPnl;
import ver10.view.Dialog.Dialogs.LoginProcess.Components.Register.RegisterUsernamePnl;
import ver10.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver10.SharedClasses.LoginInfo;
import ver10.SharedClasses.LoginType;

public class Register extends LoginCard {

    public Register(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Register"), parentDialog, loginInfo, LoginType.REGISTER);

        addDialogComponent(new RegisterUsernamePnl(loginInfo, this));

        PasswordPnl passwordPnl = new PasswordPnl(true, loginInfo, this);
        addDialogComponent(passwordPnl);
        addDialogComponent(new ConfirmPasswordPnl(loginInfo, passwordPnl, this));

    }

}
