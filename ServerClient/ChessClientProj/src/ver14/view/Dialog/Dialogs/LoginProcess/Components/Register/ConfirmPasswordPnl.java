package ver14.view.Dialog.Dialogs.LoginProcess.Components.Register;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;

import java.util.function.Supplier;

/**
 * Confirm password field. a field for repeating a password for confirmation.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ConfirmPasswordPnl extends PasswordPnl {
    /**
     * The Match with.
     */
    private Supplier<String> matchWith;
    /**
     * The No match err.
     */
    private String noMatchErr;

    /**
     * Instantiates a new Confirm password pnl.
     *
     * @param loginInfo the login info
     * @param matchWith the match with
     * @param parent    the parent
     */
    public ConfirmPasswordPnl(LoginInfo loginInfo, Supplier<String> matchWith, Parent parent) {
        this("Confirm Password", loginInfo, matchWith, parent);
    }

    /**
     * Instantiates a new Confirm password pnl.
     *
     * @param dialogLabel the dialog label
     * @param loginInfo   the login info
     * @param matchWith   the match with
     * @param parent      the parent
     */
    public ConfirmPasswordPnl(String dialogLabel, LoginInfo loginInfo, Supplier<String> matchWith, Parent parent) {
        super(dialogLabel, false, loginInfo, parent);
        this.matchWith = matchWith;
        this.noMatchErr = "Password and Confirm Password must match";
    }

    /**
     * Sets match with.
     *
     * @param matchWith  the match with
     * @param noMatchErr the no match err
     */
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

    /**
     * Is matching with the actual password field.
     *
     * @return the boolean
     */
    public boolean isMatching() {
        return matchWith != null && getPassword().equals(matchWith.get());
    }

}
