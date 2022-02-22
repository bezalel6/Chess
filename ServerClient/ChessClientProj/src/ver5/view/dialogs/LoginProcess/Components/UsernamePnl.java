package ver5.view.dialogs.LoginProcess.Components;

import ver5.SharedClasses.LoginInfo;
import ver5.SharedClasses.RegEx;
import ver5.view.dialogs.CombinedKeyAdapter;
import ver5.view.dialogs.DialogField;
import ver5.view.dialogs.MyDialog;

import javax.swing.*;
import java.awt.*;

public class UsernamePnl extends DialogField {
    protected final JTextField usernameTxtField;
    private final LoginInfo loginInfo;

    public UsernamePnl(LoginInfo loginInfo, boolean useDontAddRegex, MyDialog parent) {
        super("Username", RegEx.Username, useDontAddRegex, parent);
        this.loginInfo = loginInfo;
        usernameTxtField = new JTextField() {{
            setForeground(Color.BLUE);
            setPreferredSize(defaultTextFieldSize);
            requestFocus();
        }};
        usernameTxtField.addKeyListener(CombinedKeyAdapter.combinedKeyAdapter(() -> {
            loginInfo.setUsername(usernameTxtField.getText());
        }));

        addMainComp(usernameTxtField);

        doneCreating();
    }

    @Override
    public String getText() {
        return usernameTxtField.getText();
    }

    @Override
    public int getMainCompWidth() {
        return usernameTxtField.getWidth();
    }
}
