package ver11_board_class;

import ver11_board_class.moves.Move;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.ArrayList;


public class BoardOverlay extends LayerUI<JPanel> {


    public static Point mouseCoordinates, startedAt;
    public boolean isDragging = false, isDrawing = false, isClicking = false;
    private ImageIcon btnIcon;
    private View view;
    private ArrayList<Shape> arrows;
    private JLayer jlayer;

    public BoardOverlay(View view) {
        this.view = view;
        arrows = new ArrayList<>();
    }


    private static Point midpoint(Point p1, Point p2) {
        return new Point((int) ((p1.x + p2.x) / 2.0),
                (int) ((p1.y + p2.y) / 2.0));
    }

    public Shape createArrowShape(Point fromPt, Point toPt) {
        Polygon arrowPolygon = new Polygon();

        Point p1 = fromPt;
        Point p2 = toPt;
        if (p1.equals(p2)) return arrowPolygon;
        int x1 = p1.x, y1 = p1.y;
        int x2 = p2.x, y2 = p2.y;

        x2 = Math.abs(x2 - x1);
        y2 = Math.abs(y2 - y1);

        int arrowHeadHeight = 90;
        int lineWidth = 20;

//        arrowPolygon.addPoint(0, lineWidth);
//
//        arrowPolygon.addPoint(x2 - arrowHeadHeight, y2 + lineWidth);
//        arrowPolygon.addPoint(x2 - arrowHeadHeight, y2 + arrowHeadHeight);
//        arrowPolygon.addPoint(x2, y2);
//        arrowPolygon.addPoint(x2 - arrowHeadHeight, y2 - arrowHeadHeight);
//        arrowPolygon.addPoint(x2 - arrowHeadHeight, y2 - lineWidth);
//
//        arrowPolygon.addPoint(0, -lineWidth);

        arrowPolygon.addPoint(-6, 1);
        arrowPolygon.addPoint(3, 1);
        arrowPolygon.addPoint(3, 3);
        arrowPolygon.addPoint(6, 0);
        arrowPolygon.addPoint(3, -3);
        arrowPolygon.addPoint(3, -1);
        arrowPolygon.addPoint(-6, -1);

        Point midPoint = midpoint(fromPt, toPt);

        double rotate = Math.atan2(toPt.y - fromPt.y, toPt.x - fromPt.x);

        AffineTransform transform = new AffineTransform();
        Point pnt = midPoint;
        transform.translate(pnt.x, pnt.y);
        double ptDistance = fromPt.distance(toPt);
        double scale = ptDistance / 12; // 12 because it's the length of the arrow polygon.
        transform.scale(scale, scale);
        transform.rotate(rotate);

        return transform.createTransformedShape(arrowPolygon);
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
            Shape arrowShape = newArrow(startedAt, mouseCoordinates);
            g2.fill(arrowShape);
        }
        if (arrows != null && !arrows.isEmpty()) {
            for (Shape arrow : arrows) {
                g2.fill(arrow);
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
                stopDrawing();
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
                        startClicking(btn);
                    } else if (isClicking) {
                        stopClicking(btn);
                    }
                }
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (isSameBtn(btn) && isDrawing && !isClicking)
                    view.highLightButton(btn);
                stopDrawing();

            } else {
                clearAllArrows();
                if (!isClicking)
                    view.resetBackground();
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

    public void startClicking(JButton btn) {
        //System.out.println("started clicking");
        clickOnBtn();
        isClicking = true;
        isDragging = false;
    }

    public void stopClicking(JButton btn) {
        //System.out.println("stopped clicking");
        clickOnBtn();
        isClicking = false;
        isDragging = false;
    }

    public void startDrawing() {
        startedAt = new Point(mouseCoordinates);
        isDrawing = true;
    }

    public void stopDrawing() {
        if (isDrawing) {
            Shape arrow = newArrow(startedAt, mouseCoordinates);
            Shape found = null;
            for (Shape tmp : arrows) {
                if (arrow.getBounds().equals(tmp.getBounds())) {
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
        Point start = view.getBtn(move.getMovingTo()).getLocation();
        Point end = view.getBtn(move.getMovingFrom()).getLocation();
        arrows.add(newArrow(start, end));
    }

    private Shape newArrow(Point start, Point end) {
        start = correctPoint(start);
        end = correctPoint(end);
        return createArrowShape(start, end);
    }

    private Point correctPoint(Point point) {
        Point newPoint = new Point(point);
        newPoint = view.getBtn(newPoint).getLocation();
        JButton btn = view.getBtn(mouseCoordinates);
        newPoint.x += btn.getWidth() / 2;
        newPoint.y += btn.getHeight() / 2;
        return newPoint;
    }

    private boolean isLineEqual(Line2D shape1, Line2D shape2) {
        Shape shape = new Line2D.Float(0, 0, 0, 0);
        return shape1.getP1().equals(shape2.getP1()) && shape1.getP2().equals(shape2.getP2());
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