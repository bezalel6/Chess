package ver5.view.dialogs.LoginProcess;

import ver5.SharedClasses.FontManager;
import ver5.SharedClasses.LoginInfo;
import ver5.SharedClasses.LoginType;
import ver5.SharedClasses.networking.AppSocket;
import ver5.SharedClasses.networking.DummySocket;
import ver5.SharedClasses.ui.MyJButton;
import ver5.view.dialogs.LoginProcess.Panels.Login;
import ver5.view.dialogs.LoginProcess.Panels.Register;
import ver5.view.dialogs.MyDialog;
import ver5.view.dialogs.WinPnl;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginProcess extends MyDialog {
    private final LoginInfo loginInfo;
    private final Login loginPnl;
    private final Register registerPnl;

    public LoginProcess(String err, LoginType startAt, AppSocket socketToServer) {
        super(socketToServer);
        dialogWideErr(err);
        loginInfo = new LoginInfo(LoginType.LOGIN);

        loginPnl = new Login(this);
        registerPnl = new Register(this);

        doneCreating();
        if (startAt != null) {
            switchTo(switch (startAt) {
                case LOGIN -> loginPnl;
                case REGISTER -> registerPnl;
                default -> null;
            });
        }
    }


    public static void main(String[] args) {
        System.out.println(new LoginProcess("", null, new DummySocket()).loginInfo);
    }

    public static LoginInfo loginProcess(String err, LoginType loginType, AppSocket socketToServer) {
        return new LoginProcess(err, loginType, socketToServer).loginInfo;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    @Override
    protected void createStartingPnl() {
        Font font = FontManager.loginProcess;
        startingPnl = new WinPnl("Choose Login Type") {{
            add(new MyJButton("Login", font, () -> switchTo(loginPnl)));
            add(new MyJButton("Register", font, () -> switchTo(registerPnl)));
            add(new MyJButton("Guest", font, () -> {
                loginInfo.setLoginType(LoginType.GUEST);
                done();
            }));
            add(new MyJButton("Cancel & Exit", font, () -> {
                loginInfo.setLoginType(LoginType.CANCEL);
                done();
            }));

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    super.keyTyped(e);
                    switch (e.getKeyChar()) {
                        case 'g': {
                            loginInfo.setLoginType(LoginType.GUEST);
                        }
                        case 'l': {
                            loginInfo.setLoginType(LoginType.LOGIN);
                            loginInfo.setUsername("bezalel6");
                            loginInfo.setPassword("123456");
                        }
                    }
                }
            });

            doneAdding();
        }};
    }
}
