package ver14_correct_piece_location;

import ver14_correct_piece_location.moves.Move;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;

class MKeyListener extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent event) {
        char ch = event.getKeyChar();
        System.out.println("wdada" + event);
    }
}

class Arrow {
    int barb = 50;                   // barb length
    double phi = Math.PI / 6;             // 30 degrees barb angle
    private Point start, end;

    public Arrow(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public void draw(Graphics2D g2) {
        if (start == null || end == null || start.equals(end)) return;
        g2.setStroke(new BasicStroke(20));
        g2.setColor(Color.green.brighter());
        g2.draw(new Line2D.Double(start.x, start.y, end.x, end.y));
        double theta = Math.atan2(end.y - start.y, end.x - start.x);
        int x0 = end.x, y0 = end.y;
        double x = x0 - barb * Math.cos(theta + phi);
        double y = y0 - barb * Math.sin(theta + phi);
        g2.draw(new Line2D.Double(x0, y0, x, y));
        x = x0 - barb * Math.cos(theta - phi);
        y = y0 - barb * Math.sin(theta - phi);
        g2.draw(new Line2D.Double(x0, y0, x, y));
    }

    public boolean equals(Arrow other) {
        return start.equals(other.start) && end.equals(other.end);
    }
}

public class BoardOverlay extends LayerUI<JPanel> {


    public static Point mouseCoordinates, startedAt;
    public boolean isDragging = false, isDrawing = false, isClicking = false;

    private ImageIcon btnIcon;
    private View view;
    private ArrayList<Arrow> arrows;
    private JLayer jlayer;

    public BoardOverlay(View view) {

        this.view = view;
        arrows = new ArrayList<>();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        jlayer = (JLayer) c;
        jlayer.setLayerEventMask(
                AWTEvent.MOUSE_EVENT_MASK |
                        AWTEvent.MOUSE_MOTION_EVENT_MASK
        );
    }

    @Override
    public void uninstallUI(JComponent c) {
        jlayer = (JLayer) c;
        jlayer.setLayerEventMask(0);
        super.uninstallUI(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        super.paint(g2, c);
        if (isDragging && btnIcon != null) {
            Image img = btnIcon.getImage();
            g2.drawImage(img, mouseCoordinates.x - img.getHeight(null) / 2, mouseCoordinates.y - img.getWidth(null) / 2, view.boardPnl);
        }
        g2.setStroke(new BasicStroke(10));

        if (isDrawing) {
            Arrow arrow = newArrow(startedAt, mouseCoordinates);
            arrow.draw(g2);
        }
        if (arrows != null && !arrows.isEmpty()) {
            for (Arrow arrow : arrows) {
                arrow.draw(g2);
            }
        }
        g2.dispose();
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer l) {
        if (!(e.getSource() instanceof JButton)) return;
        JButton btn = (JButton) e.getSource();
        if (e.paramString().indexOf("MOUSE_PRESSED") != -1) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                stopDrawingArrows();
                if (btn.isEnabled())
                    if (!isClicking && !isDragging) {
                        startDragging(btn);
                    }
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                startDrawing();
            }
        } else if (e.paramString().indexOf("MOUSE_RELEASED") != -1) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                stopDragging(btn, btn.isEnabled());
                if (btn.isEnabled()) {
                    if (isSameBtn(btn) && !isClicking) {
                        startClicking();
                    } else if (isClicking) {
                        stopClicking();
                    }
                }
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (isSameBtn(btn) && isDrawing && !isClicking)
                    view.highLightButton(btn);
                stopDrawingArrows();

            } else {
                clearAllArrows();
                //if (!isClicking)
                view.resetSelectedButtons();
            }
        }
        l.repaint();
    }

    private boolean isSameBtn(JButton btn) {
        return btn.getBounds().contains(mouseCoordinates);
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer l) {
        Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);
        mouseCoordinates = new Point(p);
        l.repaint();
    }

    public void startDragging(JButton btn) {
        //System.out.println("starting dragging");
        btnIcon = (ImageIcon) btn.getIcon();
        btn.setIcon(null);
        clickOnBtn();
        isDragging = true;
        isClicking = false;
    }

    private void clickOnBtn() {
        JButton newBtn = view.getBtn(mouseCoordinates);
        Location to = view.getBtnLoc(newBtn);
        view.boardButtonPressed(to);
    }

    public void stopDragging(JButton btn, boolean click) {
        if (isDragging) {
            //System.out.println("stopping dragging");
            btn.setIcon(btnIcon);
            if (click)
                clickOnBtn();
            isDragging = false;
        }
    }

    public void startClicking() {
        clickOnBtn();
        isClicking = true;
        isDragging = false;
    }

    public void stopClicking() {
        clickOnBtn();
        isClicking = false;
        isDragging = false;
    }

    public void startDrawing() {
        startedAt = new Point(mouseCoordinates);
        isDrawing = true;
    }

    public void stopDrawingArrows() {
        if (isDrawing) {
            Arrow arrow = newArrow(startedAt, mouseCoordinates);
            Arrow found = null;
            for (Arrow tmp : arrows) {
                if (arrow.equals(tmp)) {
                    found = tmp;
                    break;
                }
            }
            if (found != null) {
                arrows.remove(found);
            } else
                arrows.add(arrow);
            isDrawing = false;
        }
    }

    public void drawMove(Move move) {
        Point start = view.getBtn(move.getMovingFrom()).getLocation();
        Point end = view.getBtn(move.getMovingTo()).getLocation();
        arrows.add(newArrow(start, end));
    }

    private Arrow newArrow(Point start, Point end) {
        start = centerPoint(start);
        end = centerPoint(end);
        return new Arrow(start, end);
    }

    private Point centerPoint(Point point) {
        Point newPoint = new Point(point);
        newPoint = view.getBtn(newPoint).getLocation();
        JButton btn = view.getBtn(mouseCoordinates);
        newPoint.x += btn.getWidth() / 2;
        newPoint.y += btn.getHeight() / 2;
        return newPoint;
    }

    public void clearAllArrows() {
        arrows = new ArrayList<>();
    }

    public Point getPoint() {
        return mouseCoordinates;
    }

    public void reset() {
        isDragging = false;
    }

}