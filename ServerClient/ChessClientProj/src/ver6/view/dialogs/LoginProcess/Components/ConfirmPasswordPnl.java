package ver6.view.dialogs.LoginProcess.Components;

import ver6.SharedClasses.LoginInfo;
import ver6.view.dialogs.MyDialog;

public class ConfirmPasswordPnl extends PasswordPnl {
    private final PasswordPnl passwordPnl;

    public ConfirmPasswordPnl(LoginInfo loginInfo, PasswordPnl passwordPnl, MyDialog parent) {
        this("Confirm Password", loginInfo, passwordPnl, parent);
    }

    public ConfirmPasswordPnl(String label, LoginInfo loginInfo, PasswordPnl passwordPnl, MyDialog parent) {
        super(label, loginInfo, true, parent);
        this.passwordPnl = passwordPnl;
    }

    @Override
    public boolean verify() {
        return super.verify() && isMatching();
    }

    @Override
    protected boolean verifyRegEx() {
        return super.verifyRegEx() && isMatching();
    }

    @Override
    public String getError() {
        if (!isMatching()) {
            return "Password and Confirm Password must match";
        }
        return super.getError();
    }

    public boolean isMatching() {
        return getPassword().equals(passwordPnl.getPassword());
    }
}
