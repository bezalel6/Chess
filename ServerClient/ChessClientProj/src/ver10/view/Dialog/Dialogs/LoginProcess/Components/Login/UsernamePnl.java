package ver10.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver10.SharedClasses.LoginInfo;
import ver10.SharedClasses.RegEx;
import ver10.view.Dialog.Components.Parent;

public class UsernamePnl extends LoginField {

    public UsernamePnl(boolean useDontAddRegex, LoginInfo loginInfo, Parent parent) {
        super("Username", RegEx.Username.get(useDontAddRegex), loginInfo, parent);
    }

    @Override
    protected void onUpdate() {
        loginInfo.setUsername(textField.getText());
        super.onUpdate();
    }
}
