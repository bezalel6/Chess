package ver12_myJbutton;

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
