package ver14.SharedClasses.UI;

import javax.swing.*;
import java.awt.*;

/*
 * MyLbl
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * MyLbl -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * MyLbl -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class MyLbl extends JLabel {
    private StringModifier modifier;

    public MyLbl(String text, Font font) {
        this(text);
        setFont(font);
    }

    public MyLbl(String text) {
        this(text, null, SwingConstants.LEADING);

    }

    public MyLbl(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        this.modifier = (str) -> str;
        setText(text);//for modifier
    }

    @Override
    public void setText(String text) {
        if (modifier != null)
            super.setText(modifier.modify(text));
        else super.setText(text);
    }


    public MyLbl(Icon icon, Font font) {
        this();
        setFont(font);
        setIcon(icon);
    }

    public MyLbl() {
        this("");
    }

    public static void toggle() {

    }

    public void underline() {
        modifier = "<html><u>%s</u></html>"::formatted;
        setText(getText());
    }

    public void setAllSizes(Dimension size) {
        this.setSize(size);
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.invalidate();
    }

    public interface StringModifier {
        String modify(String modifying);
    }
}