package ver34_faster_move_generation.view_classes;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

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

    public MyJButton(String text, Font font, Function<Void, Void> onClick, JPanel addTo) {
        this(text, font, onClick);
        addTo.add(this);
    }

    public MyJButton(String text, Font font, Function<Void, Void> onClick) {
        this(text, font);
        addActionListener(e -> onClick.apply(null));
    }
}
