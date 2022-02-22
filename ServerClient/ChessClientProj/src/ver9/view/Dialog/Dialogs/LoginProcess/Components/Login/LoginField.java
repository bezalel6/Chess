package ver9.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver9.SharedClasses.LoginInfo;
import ver9.SharedClasses.RegEx;
import ver9.view.Dialog.Components.Parent;
import ver9.view.Dialog.DialogFields.DialogTextField;

public abstract class LoginField extends DialogTextField {
    protected final LoginInfo loginInfo;

    protected LoginField(
            String dialogLabel,
            RegEx verifyRegEx,
            boolean useDontAddRegex,
            LoginInfo loginInfo,
            Parent parent) {
        super(dialogLabel, parent, verifyRegEx, useDontAddRegex);

        this.loginInfo = loginInfo;

    }
}
