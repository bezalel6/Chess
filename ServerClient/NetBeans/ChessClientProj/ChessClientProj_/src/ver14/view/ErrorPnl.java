package ver14.view;

import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Error pnl - a panel that will show an error if one is found.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ErrorPnl extends JPanel {
    /**
     * The Empty string.
     */
    private String emptyString = "";
    /**
     * The J text area.
     */
    private JTextArea jTextArea;

    /**
     * Instantiates a new Error pnl.
     */
    public ErrorPnl() {
        this("");
    }

    /**
     * Instantiates a new Error pnl.
     *
     * @param str the str
     */
    public ErrorPnl(String str) {
        setLayout(new BorderLayout());
        jTextArea = new JTextArea() {{

            //fixme set line wrap - offset when centering
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


    /**
     * Clear error.
     */
    public void clear() {
        setText("");
    }

    /**
     * Sets empty string.
     *
     * @param emptyString the empty string
     */
    public void setEmptyString(String emptyString) {
        this.emptyString = emptyString;
    }

    /**
     * Is error empty.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return getText().equals(emptyString);
    }

    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return jTextArea.getText();
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
//        text = StrUtils.fitInside(text, getWidth());
        if (text == null || text.trim().equals("")) {
            text = emptyString;
        }
        jTextArea.setText(StrUtils.format(text));
    }
}
