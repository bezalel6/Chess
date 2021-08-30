package ver19_square_control.View_classes;

import ver19_square_control.Location;
import ver19_square_control.moves.Move;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;

class Arrow {
    private static Color defaultColor = Color.WHITE;
    int barb = 50;                   // barb length WAS 50
    double phi = Math.PI / 6;             // 30 degrees barb angle WAS 6
    private Point start, end;
    private Color clr;

    public Arrow(Point start, Point end) {
        this.start = start;
        this.end = end;
        clr = defaultColor;
    }

    public Arrow(Point start, Point end, Color clr) {
        this.start = start;
        this.end = end;
        this.clr = clr;
    }

    public void draw(Graphics2D g2) {
        if (start == null || end == null || start.equals(end)) return;
        g2.setStroke(new BasicStroke(10));
        g2.setColor(clr);
        g2.draw(new Line2D.Double(start.x, start.y, end.x, end.y));
        double theta = Math.atan2(end.y - start.y, end.x - start.x);
        int x0 = end.x, y0 = end.y;
        double x = x0 - barb * Math.cos(theta + phi);
        double y = y0 - barb * Math.sin(theta + phi);
        g2.draw(new Line2D.Double(x0, y0, x, y));
//        g2.setColor(Color.yellow);
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
    JButton prevBtn;
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
            int h = img.getHeight(null) / 2;
            int w = img.getWidth(null) / 2;
            g2.drawImage(img, mouseCoordinates.x - h, mouseCoordinates.y - w, view.getBoardPnl());
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
                if (btn.isEnabled()) {
                    if (isSameBtn(btn) && !isClicking) {
                        startClicking();
                    } else if (isClicking) {
                        stopClicking();
                    }
                    stopDragging(btn);
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
        prevBtn = newBtn;
    }

    public void stopDragging(JButton btn) {
        if (isDragging) {
            //System.out.println("stopping dragging");
            if (btn.isEnabled() && btn.equals(prevBtn)) {
                btn.setIcon(btnIcon);
                clickOnBtn();

            }
            isDragging = false;
        }
    }

    public void startClicking() {
        if (!isDragging)
            clickOnBtn();
        else {
            prevBtn.setIcon(btnIcon);
        }
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

    private Arrow newArrow(Point start, Point end, Color clr) {
        start = centerPoint(start);
        end = centerPoint(end);
        return new Arrow(start, end, clr);
    }

    private Point centerPoint(Point point) {
        Point newPoint = new Point(point);

        newPoint = view.getBtn(newPoint).getLocation();
        JButton btn = view.getBtn();
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

    public void drawArrow(Location from, Location to, Color clr) {
        Point start = view.getBtn(from).getLocation();
        Point end = view.getBtn(to).getLocation();
        arrows.add(newArrow(start, end, clr));
    }
}