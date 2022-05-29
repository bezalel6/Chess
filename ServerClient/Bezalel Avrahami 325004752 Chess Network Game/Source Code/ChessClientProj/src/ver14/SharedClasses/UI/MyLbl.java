package ver14.SharedClasses.UI;

import javax.swing.*;
import java.awt.*;


/**
 * represents a label.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MyLbl extends JLabel {
    /**
     * The Modifier.
     */
    private StringModifier modifier;

    /**
     * Instantiates a new My lbl.
     *
     * @param text the text
     * @param font the font
     */
    public MyLbl(String text, Font font) {
        this(text);
        setFont(font);
    }

    /**
     * Instantiates a new My lbl.
     *
     * @param text the text
     */
    public MyLbl(String text) {
        this(text, null, SwingConstants.LEADING);

    }

    /**
     * Instantiates a new My lbl.
     *
     * @param text                the text
     * @param icon                the icon
     * @param horizontalAlignment the horizontal alignment
     */
    public MyLbl(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        this.modifier = (str) -> str;
        setText(text);//for modifier
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    @Override
    public void setText(String text) {
        if (modifier != null)
            super.setText(modifier.modify(text));
        else super.setText(text);
    }


    /**
     * Instantiates a new My lbl.
     *
     * @param icon the icon
     * @param font the font
     */
    public MyLbl(Icon icon, Font font) {
        this();
        setFont(font);
        setIcon(icon);
    }

    /**
     * Instantiates a new My lbl.
     */
    public MyLbl() {
        this("");
    }


    /**
     * make the text in this label <u>underlined</u>
     */
    public void underline() {
        modifier = "<html><u>%s</u></html>"::formatted;
        setText(getText());
    }

    /**
     * Sets all sizes.
     *
     * @param size the size
     */
    public void setAllSizes(Dimension size) {
        this.setSize(size);
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.invalidate();
    }

    /**
     * represents a string modifier.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public interface StringModifier {
        /**
         * Modify string.
         *
         * @param modifying the string to modify
         * @return the modified string
         */
        String modify(String modifying);
    }
}