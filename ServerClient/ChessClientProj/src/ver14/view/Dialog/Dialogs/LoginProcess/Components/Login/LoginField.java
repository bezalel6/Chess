package ver14.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Utils.RegEx;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.DialogFields.TextBasedFields.TextField;
import ver14.view.Dialog.Dialogs.Header;

/**
 * a Login field.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class LoginField extends TextField {
    /**
     * The Login info.
     */
    protected final LoginInfo loginInfo;

    /**
     * Instantiates a new Login field.
     *
     * @param headerStr   the header str
     * @param verifyRegEx the verify reg ex
     * @param loginInfo   the login info
     * @param parent      the parent
     */
    protected LoginField(
            String headerStr,
            RegEx verifyRegEx,
            LoginInfo loginInfo,
            Parent parent) {
        this(new Header(headerStr, false), verifyRegEx, loginInfo, parent);
    }

    /**
     * Instantiates a new Login field.
     *
     * @param header      the header
     * @param verifyRegEx the verify reg ex
     * @param loginInfo   the login info
     * @param parent      the parent
     */
    protected LoginField(
            Header header,
            RegEx verifyRegEx,
            LoginInfo loginInfo,
            Parent parent) {
        super(header, parent, verifyRegEx);
        this.loginInfo = loginInfo;
    }
}
