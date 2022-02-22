package ver9.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver9.SharedClasses.LoginInfo;
import ver9.SharedClasses.RegEx;
import ver9.view.Dialog.Components.Parent;

public class UsernamePnl extends LoginField {

    public UsernamePnl(boolean useDontAddRegex, LoginInfo loginInfo, Parent parent) {
        super("Username", RegEx.Username, useDontAddRegex, loginInfo, parent);
    }

    @Override
    protected void onUpdate() {
        loginInfo.setUsername(textField.getText());
        super.onUpdate();
    }
}
