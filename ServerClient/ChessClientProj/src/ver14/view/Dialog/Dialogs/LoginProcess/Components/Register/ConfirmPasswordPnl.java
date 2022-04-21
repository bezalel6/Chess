package ver14.view.Dialog.Dialogs.LoginProcess.Components.Register;

import ver14.SharedClasses.Callbacks.ObjCallback;
import ver14.SharedClasses.Login.LoginInfo;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;

public class ConfirmPasswordPnl extends PasswordPnl {
    private ObjCallback<String> matchWith;
    private String noMatchErr;

    public ConfirmPasswordPnl(LoginInfo loginInfo, ObjCallback<String> matchWith, Parent parent) {
        this("Confirm Password", loginInfo, matchWith, parent);
    }

    public ConfirmPasswordPnl(String dialogLabel, LoginInfo loginInfo, ObjCallback<String> matchWith, Parent parent) {
        super(dialogLabel, false, loginInfo, parent);
        this.matchWith = matchWith;
        this.noMatchErr = "Password and Confirm Password must match";
    }

    public void setMatchWith(ObjCallback<String> matchWith, String noMatchErr) {
        this.matchWith = matchWith;
        this.noMatchErr = noMatchErr;
    }

    @Override
    public boolean verify() {
        return super.verify() && isMatching();
    }

    public boolean isMatching() {
        return matchWith != null && getPassword().equals(matchWith.get());
    }

    //why verify AND verify regex?
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

}
