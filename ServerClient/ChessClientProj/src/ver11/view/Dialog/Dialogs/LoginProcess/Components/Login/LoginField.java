package ver11.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver11.SharedClasses.LoginInfo;
import ver11.SharedClasses.RegEx;
import ver11.view.Dialog.Components.Parent;
import ver11.view.Dialog.DialogFields.TextBasedFields.TextField;
import ver11.view.Dialog.Dialogs.Header;

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
