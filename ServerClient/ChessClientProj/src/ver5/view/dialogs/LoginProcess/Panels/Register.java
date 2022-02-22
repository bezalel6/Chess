package ver5.view.dialogs.LoginProcess.Panels;

import ver5.SharedClasses.LoginInfo;
import ver5.SharedClasses.LoginType;
import ver5.view.IconManager;
import ver5.view.dialogs.LoginProcess.Components.ConfirmPasswordPnl;
import ver5.view.dialogs.LoginProcess.Components.PasswordPnl;
import ver5.view.dialogs.LoginProcess.Components.RegisterUsernamePnl;
import ver5.view.dialogs.LoginProcess.LoginProcess;
import ver5.view.dialogs.Navigation.BackOkInterface;
import ver5.view.dialogs.WinPnl;

public class Register extends WinPnl {
    public Register(LoginProcess loginProcess) {
        super(IconManager.registerIcon);
        LoginInfo loginInfo = loginProcess.getLoginInfo();
        initBackOk(new BackOkInterface() {
            @Override
            public void onBack() {
                loginProcess.switchToStart();
            }

            @Override
            public void onOk() {
                loginInfo.setLoginType(LoginType.REGISTER);
                loginProcess.done();
            }
        });
        add(new RegisterUsernamePnl(loginInfo, loginProcess));
        PasswordPnl passwordPnl = new PasswordPnl(loginInfo, true, loginProcess);
        add(passwordPnl);
        add(new ConfirmPasswordPnl(loginInfo, passwordPnl, loginProcess));

        doneAdding();
    }
}
