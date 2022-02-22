package ver8.view.Wishfull.dialogs.LoginProcess;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.LoginType;
import ver8.SharedClasses.networking.AppSocket;
import ver8.view.Wishfull.dialogs.MyDialog;
import ver8.view.Wishfull.dialogs.LoginProcess.Panels.Login;
import ver8.view.Wishfull.dialogs.LoginProcess.Panels.Register;

public class LoginProcess extends MyDialog {
    private final LoginType startAt;
    private LoginInfo loginInfo;
    private Login loginPnl;
    private Register registerPnl;

    public LoginProcess(String err, LoginType startAt, AppSocket socketToServer) {
        super(socketToServer);
        dialogWideErr(err);
        this.startAt = startAt;

        doneCreating();
    }


    public static void main(String[] args) {
        new LoginProcess("", null, null).start();
//        System.out.println();
    }

    public static LoginProcess create(String err, LoginType loginType, AppSocket socketToServer) {
        return new LoginProcess(err, loginType, socketToServer);
    }

    @Deprecated
    public static LoginInfo loginProcess(String err, LoginType loginType, AppSocket socketToServer) {
        return new LoginProcess(err, loginType, socketToServer).loginInfo;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }


    @Override
    protected void createB4StartingPnl() {
        super.createB4StartingPnl();
        loginInfo = new LoginInfo(LoginType.LOGIN);

        loginPnl = new Login(this);
        registerPnl = new Register(this);
    }

    @Override
    protected void createStartingPnl() {

        addDialogWin(loginPnl);
        addDialogWin(registerPnl);

        if (startAt != null) {
            navigateTo(switch (startAt) {
                case LOGIN -> loginPnl;
                case REGISTER -> registerPnl;
                default -> null;
            });
        }

//        startingPnl = new WinPnl("Choose Login Type") {{
//            add(new MyJButton("Login", font, () -> switchTo(loginPnl)));
//            add(new MyJButton("Register", font, () -> switchTo(registerPnl)));
//            add(new MyJButton("Guest", font, () -> {
//                loginInfo.setLoginType(LoginType.GUEST);
//                done();
//            }));
//            add(new MyJButton("Cancel & Exit", font, () -> {
//                loginInfo.setLoginType(LoginType.CANCEL);
//                done();
//            }));
//
//            doneAdding();
//        }};
    }

}
