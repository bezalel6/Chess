package ver5.SharedClasses.ui;

import javax.swing.*;
import java.awt.*;

public class MyLbl extends JLabel {
    public MyLbl(String text, Font font) {
        super(text);
        setFont(font);
    }

    public MyLbl(Icon image) {
        super(image);
    }

    public MyLbl(Icon icon, Font font) {
        setFont(font);
        setIcon(icon);
    }

    public MyLbl() {
    }

    public MyLbl(String text) {
        super(text);
    }

    public MyLbl(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    public static void toggle() {

    }
}