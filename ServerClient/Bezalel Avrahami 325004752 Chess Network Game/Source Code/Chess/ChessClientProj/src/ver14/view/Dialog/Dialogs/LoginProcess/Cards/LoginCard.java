package ver14.view.Dialog.Dialogs.LoginProcess.Cards;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Login.LoginType;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;

/**
 * represents a Login card.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class LoginCard extends DialogCard {
    /**
     * The Login info.
     */
    protected final LoginInfo loginInfo;
    /**
     * The Login type.
     */
    protected final LoginType loginType;

    /**
     * Instantiates a new Login card.
     *
     * @param cardHeader   the card header
     * @param parentDialog the parent dialog
     * @param loginInfo    the login info
     * @param loginType    the login type
     */
    public LoginCard(CardHeader cardHeader, Dialog parentDialog, LoginInfo loginInfo, LoginType loginType) {
        super(cardHeader, parentDialog);
        this.loginInfo = loginInfo;
        this.loginType = loginType;
    }

    @Override
    public void onOk() {
        loginInfo.setLoginType(loginType);
        super.onOk();
    }

}
