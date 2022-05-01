package ver14.view.Dialog.Dialogs.LoginProcess.Components.Register;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;

import java.util.function.Supplier;

public class ConfirmPasswordPnl extends PasswordPnl {
    private Supplier<String> matchWith;
    private String noMatchErr;

    public ConfirmPasswordPnl(LoginInfo loginInfo, Supplier<String> matchWith, Parent parent) {
        this("Confirm Password", loginInfo, matchWith, parent);
    }

    public ConfirmPasswordPnl(String dialogLabel, LoginInfo loginInfo, Supplier<String> matchWith, Parent parent) {
        super(dialogLabel, false, loginInfo, parent);
        this.matchWith = matchWith;
        this.noMatchErr = "Password and Confirm Password must match";
    }

    public void setMatchWith(Supplier<String> matchWith, String noMatchErr) {
        this.matchWith = matchWith;
        this.noMatchErr = noMatchErr;
    }

    @Override
    protected boolean verifyRegEx() {
        return super.verifyRegEx() && isMatching();
    }

    @Override
    public String errorDetails() {
        if (!isMatching()) {
            return this.noMatchErr;
        }
        return super.errorDetails();
    }

    public boolean isMatching() {
        return matchWith != null && getPassword().equals(matchWith.get());
    }

}
