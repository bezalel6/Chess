package ver16_calculating_pinned_pieces;

import javax.swing.*;

public class MyJButton extends JButton {
    public MyJButton() {
        super();
        setFocusable(false);
    }

    public MyJButton(String text) {
        super(text);
        setFocusable(false);
    }
}
