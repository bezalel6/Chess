package ver10.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver10.SharedClasses.LoginInfo;
import ver10.SharedClasses.RegEx;
import ver10.view.Dialog.Components.Parent;
import ver10.view.Dialog.DialogFields.TextBasedFields.TextField;
import ver10.view.Dialog.Dialogs.Header;

public abstract class LoginField extends TextField {
    protected final LoginInfo loginInfo;

    protected LoginField(
            String headerStr,
            RegEx verifyRegEx,
            LoginInfo loginInfo,
            Parent parent) {
        this(new Header(headerStr, false), verifyRegEx, loginInfo, parent);
    }

    protected LoginField(
            Header header,
            RegEx verifyRegEx,
            LoginInfo loginInfo,
            Parent parent) {
        super(header, parent, verifyRegEx);
        this.loginInfo = loginInfo;
    }
}
