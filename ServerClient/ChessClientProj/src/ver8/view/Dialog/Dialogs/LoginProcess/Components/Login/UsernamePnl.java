package ver8.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.RegEx;
import ver8.view.Dialog.Dialog;

public class UsernamePnl extends LoginField {

    public UsernamePnl(boolean useDontAddRegex, LoginInfo loginInfo, Dialog parent) {
        super("Username", RegEx.Username, useDontAddRegex, loginInfo, parent);
    }

    @Override
    protected void onInputUpdate() {
        loginInfo.setUsername(textField.getText());
        super.onInputUpdate();
    }
}
