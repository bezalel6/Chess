package ver8.view.Wishfull.dialogs.LoginProcess.Components;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.RegEx;
import ver8.SharedClasses.ui.MyJButton;
import ver8.view.IconManager;
import ver8.view.Wishfull.dialogs.CombinedKeyAdapter;
import ver8.view.Wishfull.dialogs.DialogField;
import ver8.view.Wishfull.dialogs.MyDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PasswordPnl extends DialogField {

    private static final double iconRatio = 1.5;
    private final JPasswordField passwordField;
    private final MyJButton showPasswordBtn;
    private final LoginInfo loginInfo;

    public PasswordPnl() {
        this(null, false, null);
    }

    public PasswordPnl(LoginInfo loginInfo, boolean useDontAddRegex, MyDialog parent) {
        this("Password", loginInfo, useDontAddRegex, parent);
    }

    public PasswordPnl(String label, LoginInfo loginInfo, boolean useDontAddRegex, MyDialog parent) {
        super(label, RegEx.Password, useDontAddRegex, parent);
        this.loginInfo = loginInfo;
        passwordField = new JPasswordField() {{
            addKeyListener(CombinedKeyAdapter.combinedKeyAdapter(() -> {
                loginInfo.setPassword(PasswordPnl.this.getPassword());
            }));
        }};
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
        addMainComp(passwordField);
        addSecondaryComp(showPasswordBtn);
        setForeground(Color.BLUE);
        hideText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    private void showText() {
        passwordField.setEchoChar((char) 0);
        setBtnIcon(true);
    }

    private void hideText() {
        passwordField.setEchoChar('*');
        setBtnIcon(false);
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (passwordField != null)
            passwordField.setForeground(fg);
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
    public synchronized void addKeyListener(KeyListener l) {
        super.addKeyListener(l);
        passwordField.addKeyListener(l);
    }

    @Override
    public String getText() {
        return getPassword();
    }

    @Override
    public int getMainCompWidth() {
        return passwordField.getWidth();
    }
}
