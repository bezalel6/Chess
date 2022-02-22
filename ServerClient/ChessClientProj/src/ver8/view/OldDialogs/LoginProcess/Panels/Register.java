package ver8.view.OldDialogs.LoginProcess.Panels;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.LoginType;
import ver8.view.IconManager;
import ver8.view.OldDialogs.LoginProcess.Components.ConfirmPasswordPnl;
import ver8.view.OldDialogs.LoginProcess.Components.PasswordPnl;
import ver8.view.OldDialogs.LoginProcess.Components.RegisterUsernamePnl;
import ver8.view.OldDialogs.LoginProcess.LoginProcess;
import ver8.view.OldDialogs.Navigation.BackOkInterface;
import ver8.view.OldDialogs.WinPnl;

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
