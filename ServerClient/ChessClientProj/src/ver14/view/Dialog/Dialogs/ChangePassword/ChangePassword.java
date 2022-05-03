package ver14.view.Dialog.Dialogs.ChangePassword;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.view.Dialog.BackOk.BackOkInterface;
import ver14.view.Dialog.CanError;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Cards.SimpleDialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.DialogProperties;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Register.ConfirmPasswordPnl;
import ver14.view.IconManager.Size;

/**
 * Change password dialog.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ChangePassword extends Dialog implements BackOkInterface {
    /**
     * The New info.
     */
    private final LoginInfo newInfo;
    /**
     * The Password.
     */
    private String password;

    /**
     * Instantiates a new Change password.
     *
     * @param dialogProperties the properties
     * @param currentPassword  the current password
     */
    public ChangePassword(DialogProperties dialogProperties, String currentPassword) {
        super(dialogProperties);

        ConfirmPasswordPnl confirmCurrentPassword = new ConfirmPasswordPnl("enter current password", new LoginInfo(), null, this);
        confirmCurrentPassword.setMatchWith(() -> currentPassword, "wrong password");

        newInfo = new LoginInfo();
        PasswordPnl newPwPnl = new PasswordPnl("enter new password", true, newInfo, this);
        newPwPnl.notEqualsTo(new CanError<>() {
            @Override
            public String obj() {
                return currentPassword;
            }

            @Override
            public String errorDetails() {
                return "password cant be same as old password";
            }
        });

        ConfirmPasswordPnl confirmNewPassword = new ConfirmPasswordPnl("Confirm new Password", new LoginInfo(), newPwPnl::getPassword, this);

        DialogCard card = SimpleDialogCard.create(new CardHeader("change password"), this, this, confirmCurrentPassword, newPwPnl, confirmNewPassword);

        card.setPreferredSize(new Size(350, 500));
        cardsSetup(null, card);

    }

//    /**
//     * The entry point of application.
//     *
//     * @param args the input arguments
//     */
//    public static void main(String[] args) {
//        new ChangePassword(new Properties(new Properties.Details()), "123456").start();
//    }


    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    @Override
    public String getBackText() {
        return "cancel";
    }

    @Override
    public void onBack() {
        password = null;
        done();
    }

    @Override
    public void onOk() {
        password = newInfo.getPassword();
        done();
    }
}
