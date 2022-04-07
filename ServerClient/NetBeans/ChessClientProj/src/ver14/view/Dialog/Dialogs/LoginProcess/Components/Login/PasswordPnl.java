package ver14.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.RegEx;
import ver14.SharedClasses.ui.MyJButton;
import ver14.view.Dialog.Components.Parent;
import ver14.view.IconManager.IconManager;
import ver14.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PasswordPnl extends LoginField {
    private static final double iconRatio = 1;
    private final MyJButton showPasswordBtn;

    public PasswordPnl(boolean useDontAddRegex, LoginInfo loginInfo, Parent parent) {
        this("Password", useDontAddRegex, loginInfo, parent);
    }

    public PasswordPnl(String dialogLabel, boolean useDontAddRegex, LoginInfo loginInfo, Parent parent) {
        super(dialogLabel, RegEx.Password.get(useDontAddRegex), loginInfo, parent);
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
            setPreferredSize(IconManager.SECONDARY_COMP_SIZE);
//            setPreferredSize(getPreferredSize());
//            setMaximumSize(getPreferredSize());
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
        Size size = new Size(sizeNum);
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
