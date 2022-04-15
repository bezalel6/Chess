package ver14.view.Dialog.Dialogs.LoginProcess.Cards;

import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.LoginType;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Register.ConfirmPasswordPnl;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Register.RegisterUsernamePnl;
import ver14.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver14.view.IconManager.Size;

public class Register extends LoginCard {

    public Register(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Register"), parentDialog, loginInfo, LoginType.REGISTER);

        addDialogComponent(new RegisterUsernamePnl(loginInfo, this));

        PasswordPnl passwordPnl = new PasswordPnl(true, loginInfo, this);
        addDialogComponent(passwordPnl);
        addDialogComponent(new ConfirmPasswordPnl(loginInfo, passwordPnl::getPassword, this));

    }

    @Override
    public Size getPreferredSize() {
        return new Size(400, 700);
    }
}
