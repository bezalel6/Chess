package ver8.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.RegEx;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.DialogFields.DialogTextField;

public abstract class LoginField extends DialogTextField {
    protected final LoginInfo loginInfo;

    protected LoginField(
            String dialogLabel,
            RegEx verifyRegEx,
            boolean useDontAddRegex,
            LoginInfo loginInfo,
            Dialog parent) {
        super(dialogLabel, parent, verifyRegEx, useDontAddRegex);

        this.loginInfo = loginInfo;

    }
}
