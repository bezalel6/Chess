package ver10.view.Dialog.Dialogs.ChangePassword;

import ver10.SharedClasses.LoginInfo;
import ver10.SharedClasses.networking.AppSocket;
import ver10.view.Dialog.CanError;
import ver10.view.Dialog.Cards.CardHeader;
import ver10.view.Dialog.Cards.DialogCard;
import ver10.view.Dialog.Cards.SimpleDialogCard;
import ver10.view.Dialog.Dialog;
import ver10.view.Dialog.DialogProperties;
import ver10.view.Dialog.Dialogs.BackOkInterface;
import ver10.view.Dialog.Dialogs.LoginProcess.Components.Login.PasswordPnl;
import ver10.view.Dialog.Dialogs.LoginProcess.Components.Register.ConfirmPasswordPnl;

import java.awt.*;

public class ChangePassword extends Dialog implements BackOkInterface {
    private final LoginInfo newInfo;
    private String password;

    public ChangePassword(AppSocket socketToServer, DialogProperties properties, Component parentWin, String currentPassword) {
        super(socketToServer, properties, parentWin);

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
    }

    public static void main(String[] args) {
        ChangePassword changePassword = new ChangePassword(null, DialogProperties.example, null, "123456");
        changePassword.start();
        System.out.println("new pass = " + changePassword.getPassword());
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
