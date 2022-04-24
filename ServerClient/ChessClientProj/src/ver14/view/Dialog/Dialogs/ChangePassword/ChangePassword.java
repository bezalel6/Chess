package ver14.view.Dialog.Dialogs.ChangePassword;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.view.Dialog.BackOk.BackOkInterface;
import ver14.view.Dialog.CanError;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Cards.SimpleDialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Register.ConfirmPasswordPnl;
import ver14.view.Dialog.Properties;
import ver14.view.IconManager.Size;

public class ChangePassword extends Dialog implements BackOkInterface {
    private final LoginInfo newInfo;
    private String password;

    public ChangePassword(Properties properties, String currentPassword) {
        super(properties);

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
        cardsSetup(null, card);

        setPreferredSize(new Size(350, 400));
    }


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
