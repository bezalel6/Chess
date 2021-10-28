package ver32_negamax.view_classes;

import ver32_negamax.view_classes.dialogs_classes.YesOrNo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

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
