package ver8.view.Wishfull.dialogs.LoginProcess.Panels;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.LoginType;
import ver8.view.IconManager;
import ver8.view.Wishfull.List.ComponentsList;
import ver8.view.Wishfull.dialogs.DialogComponent;
import ver8.view.Wishfull.dialogs.LoginProcess.Components.PasswordPnl;
import ver8.view.Wishfull.dialogs.LoginProcess.Components.UsernamePnl;
import ver8.view.Wishfull.dialogs.LoginProcess.LoginProcess;
import ver8.view.Wishfull.dialogs.Navigation.BackOkInterface;
import ver8.view.Wishfull.dialogs.WinPnl;

public class Login extends WinPnl implements DialogComponent {
    private final LoginProcess loginProcess;

    public Login(LoginProcess loginProcess) {
        super(IconManager.loginIcon);
        this.loginProcess = loginProcess;
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

    @Override
    public String getNavText() {
        return "Login";
    }

    @Override
    public WinPnl navTo() {
        return this;
    }

    @Override
    public ComponentsList.Parent parent() {
        return loginProcess;
    }

    @Override
    public boolean verify() {
        return false;
    }
}
