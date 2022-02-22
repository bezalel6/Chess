package ver9.view.Dialog.Dialogs.LoginProcess;

import ver9.SharedClasses.LoginInfo;
import ver9.SharedClasses.networking.AppSocket;
import ver9.view.Dialog.Cards.CardHeader;
import ver9.view.Dialog.Cards.DialogCard;
import ver9.view.Dialog.Dialog;
import ver9.view.Dialog.Dialogs.LoginProcess.Cards.CancelAndExit;
import ver9.view.Dialog.Dialogs.LoginProcess.Cards.Guest;
import ver9.view.Dialog.Dialogs.LoginProcess.Cards.Login;
import ver9.view.Dialog.Dialogs.LoginProcess.Cards.Register;

public class LoginProcess extends Dialog {
    private final LoginInfo loginInfo;

    public LoginProcess(AppSocket socketToServer, String err) {
        super(socketToServer, "Login", err);

        loginInfo = new LoginInfo();

        CardHeader header = new CardHeader("Login Process");

        DialogCard[] cards = {
                new Login(this, loginInfo),
                new Register(this, loginInfo),
                new Guest(this, loginInfo),
                new CancelAndExit(this, loginInfo)
        };

        navigationCardSetup(header, cards);
    }

    public static void main(String[] args) {
        System.out.println(showLoginProcess(null));
    }

    public static LoginInfo showLoginProcess(AppSocket socketToServer) {

        return new LoginProcess(socketToServer, null) {{
            start();


        }}.getLoginInfo();
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }
}
