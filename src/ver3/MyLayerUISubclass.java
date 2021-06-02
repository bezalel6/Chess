//package ver3;
//
//import javax.swing.*;
//import javax.swing.border.Border;
//import javax.swing.plaf.LayerUI;
//import java.awt.*;
//import java.awt.event.MouseEvent;
//
//public class MyLayerUISubclass extends LayerUI<JPanel> {
//    private boolean mActive;
//    private int mX, mY;
//
//    @Override
//    public void installUI(JComponent c) {
//        super.installUI(c);
//        JLayer jlayer = (JLayer)c;
//        jlayer.setLayerEventMask(
//                AWTEvent.MOUSE_EVENT_MASK |
//                        AWTEvent.MOUSE_MOTION_EVENT_MASK
//        );
//    }
//
//    @Override
//    public void uninstallUI(JComponent c) {
//        JLayer jlayer = (JLayer)c;
//        jlayer.setLayerEventMask(0);
//        super.uninstallUI(c);
//    }
//
//    @Override
//    public void paint (Graphics g, JComponent c) {
//        Graphics2D g2 = (Graphics2D)g.create();
//
//        // Paint the view.
//        super.paint (g2, c);
//        g2.setFont(new Font("TimesRoman", Font.PLAIN, View.offset));
//        int charLocArr[][] = View.getCharsLoc();
//        for (int i = 0; i <View.ROWS ; i++) {
//            char ch = (char)('a'+i);
//            g2.drawString(ch+"",charLocArr[i][0]+1,charLocArr[i][1]);
//        }
//
//        if (isClicking&&View.getBtnLoc(mX,mY)!=null) {
//            int arr[] = View.getBtnLoc(mX,mY);
//            g2.setColor(new Color(255, 0, 0, 94));
//            g2.setStroke(new BasicStroke(15));
//            //g2.fillRect(arr[0],arr[1],arr[2],arr[3]);
//            g2.drawRect(arr[0],arr[1],arr[2],arr[3]);
//
//
//        }
//
//        g2.dispose();
//    }
//    boolean isClicking = false;
//    @Override
//    protected void processMouseEvent(MouseEvent e, JLayer l) {
//        if(e.getButton()==MouseEvent.BUTTON3)
//        {
//            if(!isClicking)
//            {
//                isClicking=true;
//
//            }
//            else isClicking=false;
//        }
//
//        l.repaint();
//    }
//
//    @Override
//    protected void processMouseMotionEvent(MouseEvent e, JLayer l) {
//        Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);
//        mX = p.x;
//        mY = p.y;
//        l.repaint();
//    }
//}