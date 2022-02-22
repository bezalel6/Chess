package ver9;

import javax.swing.*;
import java.awt.*;

public class Tests {

    public static void main(String[] args) {
        new JFrame() {{
            setSize(500, 500);
            setLayout(new GridLayout(0, 3));

            add(new JLabel("lbl 1"));
            add(new JLabel("lbl 2"));

            setVisible(true);
        }};
    }

}
