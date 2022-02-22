package ver4;

import ver4.SharedClasses.networking.LoginInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * LoginDialogDemo - דוגמת קוד לדיאלוג כניסת משתמש.
 * <p>
 * Ilan Peretz(ilanperets@gmail.com) | 20/10/2021
 */
public class Login {
    private static final String LOGIN = "Login", GUEST = "Guest", CANCEL_AND_EXIT = "Cancel & Exit";

    public static LoginInfo showLoginDialog(String errMsg, JFrame win) {
        // הצגת תמונת כניסה
        ImageIcon loginIcon = new ImageIcon(Login.class.getResource("/assets/login.png"));
        loginIcon = new ImageIcon(loginIcon.getImage().getScaledInstance(250, 150, Image.SCALE_SMOOTH));
        JLabel lblSplash = new JLabel(loginIcon);
        lblSplash.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));

        // שדות קלט עבור קליטת נתונים
        JLabel errLabel = new JLabel(errMsg) {{
            setHorizontalAlignment(SwingConstants.CENTER);
            setForeground(Color.RED);
            setFont(new Font(Font.MONOSPACED, Font.BOLD | Font.ITALIC, 12));
        }};          // להצגת הודעת שגיאה

        JTextField unField = new JTextField() {{
            setForeground(Color.BLUE);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    errLabel.setText("");
                }
            });
        }};   // לקליטת שם המשתמש
        JTextField pwField = new JTextField() {{
            setForeground(Color.BLUE);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    errLabel.setText("");
                }
            });
        }};   // לקליטת הסיסמה

        JCheckBox aiCheckBox = new JCheckBox("Play with AI", false);
        Object[] inputFields =
                {
                        lblSplash,
                        errLabel,
                        "Enter User Name",
                        unField,
                        "Enter Password",
                        pwField,
                        aiCheckBox,
                };

        JOptionPane optionPane = new JOptionPane(inputFields, -1, 1, null, new Object[]{LOGIN, GUEST, CANCEL_AND_EXIT}, GUEST);

        JDialog dialog = optionPane.createDialog(win, LOGIN);
        // Add and define the KeyListener here!
        dialog.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    JDialog d = (JDialog) e.getSource();
                    d.dispose();
                }
            }
        });

        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);  // WILL BLOCK the program
        dialog.dispose();   // close dialog

        String status = (String) optionPane.getValue();
        return switch (status) {
            case CANCEL_AND_EXIT -> new LoginInfo();
            case GUEST -> new LoginInfo(aiCheckBox.isSelected());
            default -> new LoginInfo(unField.getText(), pwField.getText(), aiCheckBox.isSelected());
        };
    }

}
