package ver25_pieces_hashmap.view_classes;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class MyJButton extends JButton {
    public MyJButton(String text) {
        super(text);
        setFocusable(false);
    }

    public MyJButton() {
        this("");
    }

    public MyJButton(String text, Font font, Function<Void, Void> onClick, JPanel addTo) {
        this(text, font, onClick);
        addTo.add(this);
    }

    public MyJButton(String text, Font font, Function<Void, Void> onClick) {
        this(text);
        setFont(font);
        addActionListener(e -> onClick.apply(null));
    }
}
