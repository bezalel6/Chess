package ver5.view.dialogs.LoginProcess.Panels;

import ver5.SharedClasses.LoginInfo;
import ver5.SharedClasses.LoginType;
import ver5.view.IconManager;
import ver5.view.dialogs.LoginProcess.Components.PasswordPnl;
import ver5.view.dialogs.LoginProcess.Components.UsernamePnl;
import ver5.view.dialogs.LoginProcess.LoginProcess;
import ver5.view.dialogs.Navigation.BackOkInterface;
import ver5.view.dialogs.WinPnl;

public class Login extends WinPnl {
    public Login(LoginProcess loginProcess) {
        super(IconManager.loginIcon);
        LoginInfo loginInfo = loginProcess.getLoginInfo();

        initBackOk(new BackOkInterface() {
            @Override
            public void onBack() {
                loginProcess.switchToStart();
            }

            @Override
            public void onOk() {
                loginInfo.setLoginType(LoginType.LOGIN);
                loginProcess.done();
            }
        });
        add(new UsernamePnl(loginInfo, false, loginProcess));
        add(new PasswordPnl(loginInfo, false, loginProcess));
        doneAdding();
    }
}
