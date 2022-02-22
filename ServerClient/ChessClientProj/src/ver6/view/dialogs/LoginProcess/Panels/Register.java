package ver6.view.dialogs.LoginProcess.Panels;

import ver6.SharedClasses.LoginInfo;
import ver6.SharedClasses.LoginType;
import ver6.view.IconManager;
import ver6.view.dialogs.LoginProcess.Components.ConfirmPasswordPnl;
import ver6.view.dialogs.LoginProcess.Components.PasswordPnl;
import ver6.view.dialogs.LoginProcess.Components.RegisterUsernamePnl;
import ver6.view.dialogs.LoginProcess.LoginProcess;
import ver6.view.dialogs.Navigation.BackOkInterface;
import ver6.view.dialogs.WinPnl;

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
