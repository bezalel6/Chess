package ver12.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver12.SharedClasses.LoginInfo;
import ver12.SharedClasses.RegEx;
import ver12.view.Dialog.Components.Parent;

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
