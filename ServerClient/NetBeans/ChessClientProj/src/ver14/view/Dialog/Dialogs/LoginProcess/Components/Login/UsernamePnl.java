package ver14.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.RegEx;
import ver14.view.Dialog.Components.Parent;

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
