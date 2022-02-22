package ver8.view.Wishfull.dialogs.LoginProcess.Panels;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.LoginType;
import ver8.view.IconManager;
import ver8.view.Wishfull.List.ComponentsList;
import ver8.view.Wishfull.dialogs.DialogComponent;
import ver8.view.Wishfull.dialogs.LoginProcess.Components.ConfirmPasswordPnl;
import ver8.view.Wishfull.dialogs.LoginProcess.Components.PasswordPnl;
import ver8.view.Wishfull.dialogs.LoginProcess.Components.RegisterUsernamePnl;
import ver8.view.Wishfull.dialogs.LoginProcess.LoginProcess;
import ver8.view.Wishfull.dialogs.Navigation.BackOkInterface;
import ver8.view.Wishfull.dialogs.WinPnl;

public class Register extends WinPnl implements DialogComponent {
    private final LoginProcess loginProcess;

    public Register(LoginProcess loginProcess) {
        super(IconManager.registerIcon);
        this.loginProcess = loginProcess;
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

    @Override
    public String getNavText() {
        return "Register";
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
