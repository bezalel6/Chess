package ver5;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class JFrameResizing extends JFrame implements ComponentListener {
    private View view;
    JFrameResizing(View view){
        getContentPane().addComponentListener(this);
        this.view = view;
    }
    public void componentHidden(ComponentEvent ce) {};
    public void componentShown(ComponentEvent ce) {};
    public void componentMoved(ComponentEvent ce) { };
    public void componentResized(ComponentEvent ce) {
        view.refreshIconSizes();

    };
}