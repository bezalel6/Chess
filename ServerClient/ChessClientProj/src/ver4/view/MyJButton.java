package ver4.view;

import ver4.SharedClasses.ui.windows.Callback;

import javax.swing.*;
import java.awt.*;

public class MyJButton extends JButton {
    public MyJButton(String text) {
        super(text);
        setFocusable(false);
    }

    public MyJButton(String text, Font font) {
        this(text);
        setFont(font);
    }


    public MyJButton() {
        this("");
    }

    public MyJButton(String text, Font font, Callback onClick, JPanel addTo) {
        this(text, font, onClick);
        addTo.add(this);
    }

    public MyJButton(String text, Font font, Callback onClick) {
        this(text, font);
        addActionListener(e -> onClick.callback());
    }
}
