package ver7.view.dialogs.LoginProcess.Panels;

import ver7.SharedClasses.LoginInfo;
import ver7.SharedClasses.LoginType;
import ver7.view.IconManager;
import ver7.view.dialogs.LoginProcess.Components.ConfirmPasswordPnl;
import ver7.view.dialogs.LoginProcess.Components.PasswordPnl;
import ver7.view.dialogs.LoginProcess.Components.RegisterUsernamePnl;
import ver7.view.dialogs.LoginProcess.LoginProcess;
import ver7.view.dialogs.Navigation.BackOkInterface;
import ver7.view.dialogs.WinPnl;

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
