package ver8.view.Dialog.Dialogs.LoginProcess.Components.Register;

import ver8.SharedClasses.LoginInfo;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;

public class ConfirmPasswordPnl extends PasswordPnl {
    private final PasswordPnl matchingWith;

    public ConfirmPasswordPnl(LoginInfo loginInfo, PasswordPnl matchingWith, Dialog parent) {
        this("Confirm Password", loginInfo, matchingWith, parent);
    }

    public ConfirmPasswordPnl(String dialogLabel, LoginInfo loginInfo, PasswordPnl matchingWith, Dialog parent) {
        super(dialogLabel, false, loginInfo, parent);
        this.matchingWith = matchingWith;
    }

    @Override
    public boolean verify() {
        return super.verify() && isMatching();
    }

    public boolean isMatching() {
        return getPassword().equals(matchingWith.getPassword());
    }

    //why verify AND verify regex?
    @Override
    protected boolean verifyRegEx() {
        return super.verifyRegEx() && isMatching();
    }

    @Override
    public String errorDetails() {
        if (!isMatching()) {
            return "Password and Confirm Password must match";
        }
        return super.errorDetails();
    }

}
