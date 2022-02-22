package ver8.view.Dialog.Dialogs.LoginProcess.Cards;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.LoginType;
import ver8.view.Dialog.Cards.CardHeader;
import ver8.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver8.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver8.view.Dialog.Dialogs.LoginProcess.Components.Register.ConfirmPasswordPnl;
import ver8.view.Dialog.Dialogs.LoginProcess.LoginProcess;

public class Register extends LoginCard {

    public Register(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Register"), parentDialog, loginInfo, LoginType.REGISTER);

        addDialogComponent(new UsernamePnl(false, loginInfo, parentDialog));

        PasswordPnl passwordPnl = new PasswordPnl(false, loginInfo, parentDialog);
        addDialogComponent(passwordPnl);
        addDialogComponent(new ConfirmPasswordPnl(loginInfo, passwordPnl, parentDialog));

    }

}
