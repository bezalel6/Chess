package ver8.view.Dialog.Dialogs.LoginProcess;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.networking.AppSocket;
import ver8.view.Dialog.Cards.CardHeader;
import ver8.view.Dialog.Cards.DialogCard;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Dialogs.LoginProcess.Cards.CancelAndExit;
import ver8.view.Dialog.Dialogs.LoginProcess.Cards.Guest;
import ver8.view.Dialog.Dialogs.LoginProcess.Cards.Login;
import ver8.view.Dialog.Dialogs.LoginProcess.Cards.Register;

public class LoginProcess extends Dialog {
    private final LoginInfo loginInfo;

    private LoginProcess(AppSocket socketToServer) {
        super(socketToServer, "Login");

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
        return new LoginProcess(socketToServer).loginInfo;
    }
}
