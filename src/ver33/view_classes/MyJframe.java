package ver33.view_classes;

import ver33.view_classes.dialogs_classes.YesOrNo;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MyJframe extends JFrame implements ComponentListener {
    private View view;

    public MyJframe(View view) {
        getContentPane().addComponentListener(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                YesOrNo confirm = new YesOrNo("Exit Confirmation", "Are You Sure You Want To Exit?");
                int res = (int) confirm.run();
                if (res == YesOrNo.YES) {
                    view.exitButtonPressed();
                }
            }
        });

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
