package ver8.view;

import ver8.SharedClasses.FontManager;
import ver8.SharedClasses.StrUtils;
import ver8.view.OldDialogs.VerifiedComponent;

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
            setFont(FontManager.error);
            setEditable(false);
            setFocusable(false);
            setBackground(null);
//            setCursor(new Cursor(Cursor.MOVE_CURSOR));
        }};
        setText(str);

        add(jTextArea, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }

    public boolean verify(VerifiedComponent comp) {
        boolean verified = comp.verify();
        if (verified) {
            clear();
        } else {
            setText(comp.getError());
        }
        return verified;
    }

    public void clear() {
        setText("");
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
        jTextArea.setText(StrUtils.format(text));
    }
}