package ver8.view.OldDialogs.LoginProcess.Panels;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.LoginType;
import ver8.view.IconManager;
import ver8.view.OldDialogs.LoginProcess.Components.PasswordPnl;
import ver8.view.OldDialogs.LoginProcess.Components.UsernamePnl;
import ver8.view.OldDialogs.LoginProcess.LoginProcess;
import ver8.view.OldDialogs.Navigation.BackOkInterface;
import ver8.view.OldDialogs.WinPnl;

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
