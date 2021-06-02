package ver2;

import java.awt.event.*;
import javax.swing.*;

public class JFrameResizing extends JFrame implements ComponentListener {

    JFrameResizing(){
        getContentPane().addComponentListener(this);
    }

    public void componentHidden(ComponentEvent ce) {};
    public void componentShown(ComponentEvent ce) {};
    public void componentMoved(ComponentEvent ce) { };
    public void componentResized(ComponentEvent ce) {
        View.refreshIconSizes();

    };
//
//    public static void main(String[] arguments) {
//
//        JFrame.setDefaultLookAndFeelDecorated(true);
//        JFrameResizing frame = new JFrameResizing();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setTitle("JFrame Resizing Example");
//        frame.setSize(300,150);
//        frame.setVisible(true);
//
//    }
}