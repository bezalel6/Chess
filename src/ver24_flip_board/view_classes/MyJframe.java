package ver24_flip_board.view_classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class MyJframe extends JFrame implements ComponentListener {
    private View view;

    MyJframe(View view) {
        getContentPane().addComponentListener(this);
        this.view = view;
    }

    public void componentHidden(ComponentEvent ce) {
    }


    public void componentShown(ComponentEvent ce) {
    }


    public void componentMoved(ComponentEvent ce) {
    }


    public void componentResized(ComponentEvent ce) {
//        view.refreshIconSizes();

    }
}
