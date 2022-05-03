package ver14.view.Dialog.Dialogs.LoginProcess.Cards;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Login.LoginType;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver14.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver14.view.IconManager.Size;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Set;

/**
 * represents the normal Login card. username and password.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Login extends LoginCard {


    /**
     * The Username pnl.
     */
    private final UsernamePnl usernamePnl;
    /**
     * The Password pnl.
     */
    private final PasswordPnl passwordPnl;
    /**
     * The Remove adapters.
     */
    private final ArrayList<Set<Integer>> removeAdapters = new ArrayList<>();

    /**
     * Instantiates a new Login.
     *
     * @param parentDialog the parent dialog
     * @param loginInfo    the login info
     */
    public Login(LoginProcess parentDialog, LoginInfo loginInfo) {
        super(new CardHeader("Login"), parentDialog, loginInfo, LoginType.LOGIN);

        this.usernamePnl = new UsernamePnl(false, loginInfo, parentDialog);
        this.passwordPnl = new PasswordPnl(false, loginInfo, parentDialog);
        addDialogComponent(usernamePnl);
        addDialogComponent(passwordPnl);

        setPreferredSize(new Size(300, 400));
    }


    @Override
    public void displayed() {
        super.displayed();
        removeAdapters.add(parentDialog.keyAdapter().addAction(() -> {
            ((LoginProcess) parentDialog).getLoginInfo().initDebugLoginValues();
            refreshValues((LoginProcess) parentDialog);
//            onOk();
        }, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_F));
    }

    /**
     * Refresh values.
     *
     * @param loginProcess the login process
     */
    protected void refreshValues(LoginProcess loginProcess) {
        usernamePnl.setValue(loginProcess.getLoginInfo().getUsername());
        passwordPnl.setValue(loginProcess.getLoginInfo().getPassword());
    }

    @Override
    public void onBack() {
        removeAdapters.forEach(parentDialog.keyAdapter()::removeAction);
        super.onBack();
    }
}
