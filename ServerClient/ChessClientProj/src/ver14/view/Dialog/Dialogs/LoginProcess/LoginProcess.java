package ver14.view.Dialog.Dialogs.LoginProcess;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.DialogProperties;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.CancelAndExit;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.Guest;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.Login;
import ver14.view.Dialog.Dialogs.LoginProcess.Cards.Register;
import ver14.view.IconManager.Size;

/**
 * represents the dialog for the Login process.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class LoginProcess extends Dialog {
    /**
     * The Login info.
     */
    private final LoginInfo loginInfo;

    /**
     * Instantiates a new Login process.
     *
     * @param dialogProperties the properties
     */
    public LoginProcess(DialogProperties dialogProperties) {
        super(dialogProperties);

        loginInfo = new LoginInfo();

        DialogCard[] cards = {
                new Login(this, loginInfo),
                new Register(this, loginInfo),
                new Guest(this, loginInfo),
                new CancelAndExit(this, loginInfo)
        };

        navigationCardSetup(new Size(300, 350), cards);
    }

    //    @Override
//    public BackOkPnl backOkPnl() {
//        return null;
//    }

    /**
     * Gets login info.
     *
     * @return the login info
     */
    public LoginInfo getLoginInfo() {
        return loginInfo;
    }
}
