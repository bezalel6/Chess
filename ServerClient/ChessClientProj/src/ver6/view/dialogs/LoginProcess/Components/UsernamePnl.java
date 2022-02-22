package ver6.view.dialogs.LoginProcess.Components;

import ver6.SharedClasses.LoginInfo;
import ver6.SharedClasses.RegEx;
import ver6.view.dialogs.CombinedKeyAdapter;
import ver6.view.dialogs.DialogField;
import ver6.view.dialogs.MyDialog;

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
