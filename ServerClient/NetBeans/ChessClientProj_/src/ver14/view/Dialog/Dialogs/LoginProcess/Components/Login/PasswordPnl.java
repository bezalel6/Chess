package ver14.view.Dialog.Dialogs.LoginProcess.Components.Login;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.SharedClasses.Utils.RegEx;
import ver14.view.Dialog.Components.Parent;
import ver14.view.IconManager.IconManager;
import ver14.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * represents a Password field.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class PasswordPnl extends LoginField {
    /**
     * The constant iconRatio.
     */
    private static final double iconRatio = 1;
    /**
     * The Show password btn.
     */
    private final MyJButton showPasswordBtn;

    /**
     * Instantiates a new Password pnl.
     *
     * @param useDontAddRegex the use dont add regex
     * @param loginInfo       the login info
     * @param parent          the parent
     */
    public PasswordPnl(boolean useDontAddRegex, LoginInfo loginInfo, Parent parent) {
        this("Password", useDontAddRegex, loginInfo, parent);
    }

    /**
     * Instantiates a new Password pnl.
     *
     * @param dialogLabel     the dialog label
     * @param useDontAddRegex the use dont add regex
     * @param loginInfo       the login info
     * @param parent          the parent
     */
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

    /**
     * Show text.
     */
    private void showText() {
        getPasswordField().setEchoChar((char) 0);
        setBtnIcon(true);
    }

    /**
     * Hide text.
     */
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

    /**
     * Gets password field.
     *
     * @return the password field
     */
    private JPasswordField getPasswordField() {
        return (JPasswordField) textField;
    }

    /**
     * Sets btn icon.
     *
     * @param showing the showing
     */
    private void setBtnIcon(boolean showing) {
        ImageIcon icon = showing ? IconManager.hidePassword : IconManager.showPassword;
        int btnSize = (showPasswordBtn.getMinSize());
        int sizeNum = btnSize == 0 ? IconManager.SECONDARY_COMP_SIZE.width : btnSize;
        Size size = new Size(sizeNum);
        size.multMe(iconRatio);
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

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return new String(getPasswordField().getPassword());
    }
}
