package ver9.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver9.SharedClasses.LoginInfo;
import ver9.SharedClasses.RegEx;
import ver9.SharedClasses.ui.MyJButton;
import ver9.view.Dialog.Components.Parent;
import ver9.view.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PasswordPnl extends LoginField {
    private static final double iconRatio = 1.5;
    private final MyJButton showPasswordBtn;

    public PasswordPnl(boolean useDontAddRegex, LoginInfo loginInfo, Parent parent) {
        this("Password", useDontAddRegex, loginInfo, parent);
    }

    public PasswordPnl(String dialogLabel, boolean useDontAddRegex, LoginInfo loginInfo, Parent parent) {
        super(dialogLabel, RegEx.Password, useDontAddRegex, loginInfo, parent);
        showPasswordBtn = new MyJButton() {{
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    showText();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    hideText();
                }
            });
            setPreferredSize(getPreferredSize());
            setMaximumSize(getPreferredSize());
        }};
        addSecondaryComp(showPasswordBtn);
        setForeground(Color.BLUE);
        hideText();
    }

    private void showText() {
        getPasswordField().setEchoChar((char) 0);
        setBtnIcon(true);
    }

    private void hideText() {
        getPasswordField().setEchoChar('*');
        setBtnIcon(false);
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (textField != null)
            getPasswordField().setForeground(fg);
    }

    private JPasswordField getPasswordField() {
        return (JPasswordField) textField;
    }

    private void setBtnIcon(boolean showing) {
        ImageIcon icon = showing ? IconManager.hidePassword : IconManager.showPassword;
        int btnSize = (showPasswordBtn.getMinSize());
        int sizeNum = btnSize == 0 ? IconManager.SECONDARY_COMP_SIZE.width : btnSize;
        IconManager.Size size = new IconManager.Size(sizeNum);
        size.multBy(iconRatio);
        icon = IconManager.scaleImage(icon, size);
        showPasswordBtn.setIcon(icon);
    }

    @Override
    protected JTextField createTextField() {
        return new JPasswordField();
    }

    @Override
    protected void onUpdate() {
        loginInfo.setPassword(getPassword());
        super.onUpdate();
    }

    public String getPassword() {
        return new String(getPasswordField().getPassword());
    }
}