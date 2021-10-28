package ver32_negamax.view_classes;

import ver32_negamax.Location;
import ver32_negamax.model_classes.moves.Move;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;

class Arrow {
    private final static Color defaultColor = Color.WHITE;
    private final Point start, end;
    private final Color clr;
    int barb = 50;                   // barb length WAS 50
    double phi = Math.PI / 6;             // 30 degrees barb angle WAS 6

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
        x = x0 - barb * Math.cos(theta - phi);
        y = y0 - barb * Math.sin(theta - phi);
        g2.draw(new Line2D.Double(x0, y0, x, y));
    }


    public boolean equals(Arrow other) {
        return start.equals(other.start) && end.equals(other.end);
    }
}

public class BoardOverlay extends LayerUI<JPanel> {


    private static final boolean BTN_CLICKS = false;
    public static Point mouseCoordinates, startedAt;
    public boolean isDrawing = false;
    private View view;
    private ArrayList<Arrow> arrows;
    private JLayer jlayer;


    public BoardOverlay(View view) {
        this.view = view;
        arrows = new ArrayList<>();

    }

    public void animateAndSetIcon(BoardButton button, ImageIcon icon) {
        animateAndSetIcon(button, icon, 1);
//        animateAndSetIcon(button, icon, 2);
    }

    public void animateAndSetIcon(BoardButton button, ImageIcon icon, int duration) {
        if (icon == null) {
            return;
        }
        int frames = 20 * duration;
//        int interval = 10 / frames;
        int interval = 1;
        int widthMult = icon.getIconWidth() / frames;
        int heightMult = icon.getIconHeight() / frames;
        ImageIcon temp = IconManager.copyImage(icon);
        new Timer(interval, new ActionListener() {
            int currentFrame = 1;

            public void actionPerformed(ActionEvent e) {
                ImageIcon scaled = IconManager.scaleImage(temp, widthMult * currentFrame, heightMult * currentFrame);
                button.setIcon(scaled, false);
                if (currentFrame <= frames)
                    currentFrame++;
                else
                    ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    public void flipButtons() {
        JPanel bp = view.getBoardPnl();
//        LayoutManager lm = bp.getLayout();
//        bp.setLayout(null);
//        bp.removeAll();
//        bp.setLayout(new GridLayout(8, 8));
        for (BoardButton[] row : view) {
            for (BoardButton btn : row) {
                BoardButton opposite = view.getBtn(Location.flipLocation(btn.getBtnLoc()));
                animate(btn, opposite.getLocation(), 10, 1);
//                bp.add(opposite);
            }
        }
    }

    public void animate(JComponent component, Point newPoint, int frames, int interval) {
        Rectangle compBounds = component.getBounds();
        Point oldPoint = new Point(compBounds.x, compBounds.y),
                animFrame = new Point((newPoint.x - oldPoint.x) / frames,
                        (newPoint.y - oldPoint.y) / frames);

        new Timer(interval, new ActionListener() {
            int currentFrame = 0;

            public void actionPerformed(ActionEvent e) {
                int x = oldPoint.x + (animFrame.x * currentFrame);
                int y = oldPoint.y + (animFrame.y * currentFrame);
                int w = compBounds.width;
                int h = compBounds.height;
                component.setBounds(x, y, w, h);
                component.setLocation(x, y);
                compBounds.setBounds(x, y, w, h);
                if (currentFrame != frames)
                    currentFrame++;
                else
                    ((Timer) e.getSource()).stop();

            }
        }).start();

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
        if (isInsideBoardPnl()) {
            handleCurrentDrawings(g2);
        }

        for (Arrow arrow : arrows) {
            arrow.draw(g2);
        }

        g2.dispose();
    }

    private void handleCurrentDrawings(Graphics2D g2) {
        g2.setStroke(new BasicStroke(10));

        if (isDrawing) {
            Arrow arrow = newArrow(startedAt, mouseCoordinates);
            arrow.draw(g2);
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer l) {
        if (isInsideBoardPnl() && e.getSource() instanceof JButton) {
            processMouse(e);
        }
        l.repaint();
    }

    private void processMouse(MouseEvent e) {
        BoardButton btn = (BoardButton) e.getSource();
        if (e.paramString().contains("MOUSE_PRESSED")) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                stopDrawingArrows();
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                startDrawing();
            }
        } else if (e.paramString().contains("MOUSE_RELEASED")) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (isSameBtn(btn) && isDrawing)
                    view.highLightButton(btn);
                stopDrawingArrows();

            } else {
                clearAllArrows();
                view.resetSelectedButtons();
            }
        }
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

    private boolean isInsideBoardPnl() {
        return mouseCoordinates != null;
//        return mouseCoordinates == null || view.getBoardPnl().getBounds().contains(mouseCoordinates);
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
        BoardButton btn = view.getBtn(createPointLoc(point));
        assert btn != null;
        point = btn.getLocation();
        point.x += btn.getWidth() / 2;
        point.y += btn.getHeight() / 2;
        return point;
    }

    private Location createPointLoc(Point point) {
        Location loc = new Location(point, jlayer.getWidth(), jlayer.getHeight(), view);
        return loc;
    }

    public void clearAllArrows() {
        arrows = new ArrayList<>();
    }

    public Point getPoint() {
        return mouseCoordinates;
    }

    public void drawArrow(Location from, Location to, Color clr) {
        Point start = view.getBtn(from).getLocation();
        Point end = view.getBtn(to).getLocation();
        arrows.add(newArrow(start, end, clr));
        jlayer.repaint();
    }

    public void repaintLayer() {
        jlayer.repaint();
    }
}