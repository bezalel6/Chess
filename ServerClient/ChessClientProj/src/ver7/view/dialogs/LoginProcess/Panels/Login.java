package ver7.view.dialogs.LoginProcess.Panels;

import ver7.SharedClasses.LoginInfo;
import ver7.SharedClasses.LoginType;
import ver7.view.IconManager;
import ver7.view.dialogs.LoginProcess.Components.PasswordPnl;
import ver7.view.dialogs.LoginProcess.Components.UsernamePnl;
import ver7.view.dialogs.LoginProcess.LoginProcess;
import ver7.view.dialogs.Navigation.BackOkInterface;
import ver7.view.dialogs.WinPnl;

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
