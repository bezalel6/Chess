package ver14.SharedClasses.UI.Buttons;

import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;

/**
 * MyJButton- represents a button.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MyJButton extends JButton {


    /**
     * The Og txt.
     */
    private String ogTxt = null;
    /**
     * The Og on click.
     */
    private VoidCallback ogOnClick = null;
    /**
     * The On click.
     */
    private VoidCallback onClick = null;

    /**
     * Instantiates a new My j button.
     *
     * @param text     the text
     * @param callback the callback
     */
    public MyJButton(String text, VoidCallback callback) {
        this(text);
        setOnClick(callback);
    }

    /**
     * Instantiates a new My j button.
     *
     * @param text the text
     */
    public MyJButton(String text) {
        text = StrUtils.format(text);
        setText(text);
        setFocusable(false);

        addActionListener(l -> {
            onClick();
        });
    }

    /**
     * Sets on click.
     *
     * @param onClick the on click
     */
    public void setOnClick(VoidCallback onClick) {
        this.onClick = onClick;
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    @Override
    public void setText(String text) {
        super.setText(StrUtils.format(text));

    }

    /**
     * On click.
     */
    private void onClick() {
        if (this.onClick != null)
            onClick.callback();
    }

    /**
     * Instantiates a new My j button.
     */
    public MyJButton() {
        this("");
    }

    /**
     * Instantiates a new My j button.
     *
     * @param text    the text
     * @param font    the font
     * @param onClick the on click
     * @param addTo   the add to
     */
    public MyJButton(String text, Font font, VoidCallback onClick, JPanel addTo) {
        this(text, font, onClick);
        addTo.add(this);
    }

    /**
     * Instantiates a new My j button.
     *
     * @param text    the text
     * @param font    the font
     * @param onClick the on click
     */
    public MyJButton(String text, Font font, VoidCallback onClick) {
        this(text, font);
        setOnClick(onClick);

    }

    /**
     * Instantiates a new My j button.
     *
     * @param text the text
     * @param font the font
     */
    public MyJButton(String text, Font font) {
        this(text);
        setFont(font);
    }

    /**
     * Sets font.
     *
     * @param font the font
     */
    @Override
    public void setFont(Font font) {
        super.setFont(font);
    }

    /**
     * Gets min size.
     *
     * @return the min size
     */
    public int getMinSize() {
        return Math.min(getHeight(), getWidth());
    }

    /**
     * Replace with cancel.
     *
     * @param onCancelled the on cancelled
     */
    public void replaceWithCancel(VoidCallback onCancelled) {
        replaceText("Cancel");
        ogOnClick = onClick;
        setOnClick(onCancelled);
    }

    /**
     * Replace text.
     *
     * @param replacement the replacement
     */
    private void replaceText(String replacement) {
        ogTxt = getText();
        setText(replacement);
    }

    /**
     * Reset state.
     *
     * @param e the e
     */
    public void resetState(boolean e) {
        setEnabled(e);
        if (ogTxt != null && ogOnClick != null) {
            setText(ogTxt);
            onClick = ogOnClick;
            ogOnClick = null;
            ogTxt = null;
        }

    }
}
