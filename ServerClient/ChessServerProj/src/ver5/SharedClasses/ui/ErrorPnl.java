package ver5.SharedClasses.ui;

import ver5.SharedClasses.FontManager;

import javax.swing.*;
import java.awt.*;

public class ErrorPnl extends JPanel {
    private String emptyString = "";
    private JTextArea jTextArea;

    public ErrorPnl() {
        this("");
    }

    public ErrorPnl(String str) {
        setLayout(new BorderLayout());
        jTextArea = new JTextArea() {{

            //todo set line wrap - offset when centering
            setLineWrap(true);
            setWrapStyleWord(true);

            setForeground(Color.RED);
            setFont(FontManager.errorLbl);
            setEditable(false);
            setBackground(null);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }};
        setText(str);

        add(jTextArea, BorderLayout.CENTER);
    }

    public void setEmptyString(String emptyString) {
        this.emptyString = emptyString;
    }

    public boolean isEmpty() {
        return getText().equals(emptyString);
    }

    public String getText() {
        return jTextArea.getText();
    }

    public void setText(String text) {
//        text = StrUtils.fitInside(text, getWidth());
        if (text == null || text.equals("")) {
            text = emptyString;
        }
        jTextArea.setText(text);
    }

    public void clear() {
        setText("");
    }
}
