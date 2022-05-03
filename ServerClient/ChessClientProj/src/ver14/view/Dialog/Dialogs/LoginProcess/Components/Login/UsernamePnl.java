package ver14.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Utils.RegEx;
import ver14.view.Dialog.Components.Parent;

/**
 * a Username field.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class UsernamePnl extends LoginField {

    /**
     * Instantiates a new Username pnl.
     *
     * @param useDontAddRegex the use dont add regex
     * @param loginInfo       the login info
     * @param parent          the parent
     */
    public UsernamePnl(boolean useDontAddRegex, LoginInfo loginInfo, Parent parent) {
        super("Username", RegEx.Username.get(useDontAddRegex), loginInfo, parent);
    }

    @Override
    protected void onUpdate() {
        loginInfo.setUsername(textField.getText());
        super.onUpdate();
    }
}
