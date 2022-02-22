package ver6.view.dialogs.LoginProcess.Panels;

import ver6.SharedClasses.LoginInfo;
import ver6.SharedClasses.LoginType;
import ver6.view.IconManager;
import ver6.view.dialogs.LoginProcess.Components.PasswordPnl;
import ver6.view.dialogs.LoginProcess.Components.UsernamePnl;
import ver6.view.dialogs.LoginProcess.LoginProcess;
import ver6.view.dialogs.Navigation.BackOkInterface;
import ver6.view.dialogs.WinPnl;

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
