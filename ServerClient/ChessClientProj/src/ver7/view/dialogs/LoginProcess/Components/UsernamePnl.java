package ver7.view.dialogs.LoginProcess.Components;

import ver7.SharedClasses.LoginInfo;
import ver7.SharedClasses.RegEx;
import ver7.view.dialogs.CombinedKeyAdapter;
import ver7.view.dialogs.DialogField;
import ver7.view.dialogs.MyDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                usernameTxtField.requestFocusInWindow();
            }
        });

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
