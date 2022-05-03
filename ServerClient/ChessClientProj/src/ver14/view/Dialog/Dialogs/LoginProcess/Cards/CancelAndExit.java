package ver14.view.Dialog.Dialogs.LoginProcess.Cards;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Login.LoginType;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialog;

/**
 * Cancel and exit.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class CancelAndExit extends LoginCard {
    /**
     * Instantiates a new Cancel and exit.
     *
     * @param parentDialog the parent dialog
     * @param loginInfo    the login info
     */
    public CancelAndExit(Dialog parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Cancel & Exit"), parentDialog, loginInfo, LoginType.CANCEL);
    }

    @Override
    public void navToMe() {
        onOk();
    }
}
