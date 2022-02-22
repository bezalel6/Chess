package ver5;//package ver5;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseEvent;


public class MyLayerUISubclass extends LayerUI<JPanel> {
    public boolean drag = false;
    ImageIcon draggingIcon;
    ver5.View view;
    private int mX, mY;

    public MyLayerUISubclass(View view) {
        this.view = view;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JLayer jlayer = (JLayer) c;
        jlayer.setLayerEventMask(
                AWTEvent.MOUSE_EVENT_MASK |
                        AWTEvent.MOUSE_MOTION_EVENT_MASK
        );
    }

    @Override
    public void uninstallUI(JComponent c) {
        JLayer jlayer = (JLayer) c;
        jlayer.setLayerEventMask(0);
        super.uninstallUI(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        super.paint(g2, c);
        if (drag && draggingIcon != null) {
            g2.drawImage(draggingIcon.getImage(), mX, mY, view.boardPnl);
        }

        g2.dispose();
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer l) {
        l.repaint();
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer l) {
        Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);
        mX = p.x;
        mY = p.y;
        l.repaint();
    }

    public void startDragging(ImageIcon icon) {
        draggingIcon = icon;
        drag = true;
    }

    public PointAndImageIcon stopDragging() {
        drag = false;
        return new PointAndImageIcon(mX, mY, draggingIcon);
    }

    public Point getPoint() {
        return new Point(mX, mY);
    }

    public void reset() {
        drag = false;
    }

}