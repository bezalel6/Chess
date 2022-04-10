package ver14.view.Board;

import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.moves.Move;
import ver14.view.View;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Arrow {
    private final Point start, end;
    int barb = 50;                   // barb length WAS 50
    double phi = Math.PI / 6;             // 30 degrees barb angle WAS 6
    private Color clr;


    public Arrow(Point start, Point end, Color clr) {
        this.start = start;
        this.end = end;
        this.clr = clr;
    }

    public void setClr(Color clr) {
        this.clr = clr;
    }

    public void draw(Graphics2D g2) {
        if (start == null || end == null || start.equals(end)) return;
        g2.setStroke(new BasicStroke(10));
        g2.setColor(clr);
        g2.draw(new Line2D.Double(start.x, start.y, end.x, end.y));
//        g2.draw(new Line2D.Double(start.x, start.y, end.x - 1, end.y - 1));
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
    final static Color defaultColor = Color.BLACK;
    private static final Map<Integer, Color> keyClrMap;
    private final static int NO_KEY = -1;
    public static Point mouseCoordinates, startedAt;

    static {
        keyClrMap = new HashMap<>();
        keyClrMap.put(KeyEvent.VK_SHIFT, Color.decode("#9FC0A2"));
        keyClrMap.put(KeyEvent.VK_CONTROL, Color.decode("#d36d6d"));
        keyClrMap.put(KeyEvent.VK_ALT, Color.decode("#E8A43F"));
    }

    private final View view;
    public boolean isDrawing = false;
    private ArrayList<Arrow> arrows;
    private JLayer jlayer;
    private boolean blockBoard = false;
    private int pressedKey = NO_KEY;
    private BoardButton currentDragging = null;


    public BoardOverlay(View view) {
        this.view = view;
        arrows = new ArrayList<>();
    }

    public static Polygon createPolygon(int... pointPairs) {
        assert pointPairs.length % 2 == 0;
        Point[] points = new Point[pointPairs.length / 2];
        for (int i = 0, j = 0; j < pointPairs.length; i++, j += 2) {
            points[i] = new Point(pointPairs[j], pointPairs[j + 1]);
        }
        return createPolygon(points);
    }

    public static Polygon createPolygon(Point... points) {
        int[] xpoints = new int[points.length];
        int[] ypoints = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            Point point = points[i];
            xpoints[i] = point.x;
            ypoints[i] = point.y;
        }
        return new Polygon(xpoints, ypoints, points.length);
    }

    public KeyAdapter createKeyAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                pressedKey = e.getKeyCode();
                repaintLayer();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                pressedKey = NO_KEY;
                repaintLayer();
            }
        };
    }

    public void repaintLayer() {
        jlayer.repaint();
    }

    public void setBlockBoard(boolean blockBoard) {
        this.blockBoard = blockBoard;
        repaintLayer();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        super.paint(g2, c);

        if (isInsideBoardPnl()) {
            if (isDragging()) {
                int x = mouseCoordinates.x - currentDragging.getHeight() / 2;
                int y = mouseCoordinates.y - currentDragging.getWidth() / 2;
                currentDragging.getHiddenIcon().paintIcon(c, g, x, y);
            }
            g2.setStroke(new BasicStroke(10));

            if (isDrawing) {
                Arrow arrow = newArrow(startedAt, mouseCoordinates);
                arrow.draw(g2);
            }
        }

        for (Arrow arrow : arrows) {
            arrow.draw(g2);
        }

        if (blockBoard) {
            Shape shape = new Rectangle(0, 0, jlayer.getWidth(), jlayer.getHeight());
            g2.setStroke(new BasicStroke(1));
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fill(shape);
        }
    }

    private boolean isInsideBoardPnl() {
        return mouseCoordinates != null;
//        return mouseCoordinates == null || view.getBoardPnl().getBounds().contains(mouseCoordinates);
    }

    private boolean isDragging() {
        return currentDragging != null;
    }

    private Arrow newArrow(Point start, Point end) {
        return newArrow(start, end, currentColor());
    }

    private Arrow newArrow(Point start, Point end, Color clr) {
        start = centerPoint(start);
        end = centerPoint(end);
        return new Arrow(start, end, clr);
    }

    public Color currentColor() {
        return pressedKey == NO_KEY || !keyClrMap.containsKey(pressedKey) ? defaultColor : keyClrMap.get(pressedKey);
    }

    private Point centerPoint(Point point) {
        BoardButton btn = getBtn(point);
        assert btn != null;
        point = btn.getLocation();
        point.x += btn.getWidth() / 2;
        point.y += btn.getHeight() / 2;
        return point;
    }

    private BoardButton getBtn(Point point) {
        return view.getBtn(createPointLoc(point));
    }

    private Location createPointLoc(Point point) {
        Location loc = getLoc(point);
        return loc;
    }

    public Location getLoc(Point point) {
        if (point != null) {
            int x = point.x;
            int y = point.y;
            int divYWidth = jlayer.getWidth() / 8;
            int divXHeight = jlayer.getHeight() / 8;
            int col = x / divYWidth;
            int row = y / divXHeight;
            return Location.getLoc(row, col, view.isBoardFlipped());
        }
        return null;
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer l) {
        if (isInsideBoardPnl() && e.getSource() instanceof JButton) {
            processMouse(e);
        }
        l.repaint();
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer l) {
        Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);
        mouseCoordinates = new Point(p);
        l.repaint();
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

    private void processMouse(MouseEvent e) {
        BoardButton btn = (BoardButton) e.getSource();
        switch (e.getID()) {
            case MouseEvent.MOUSE_ENTERED:
                view.unHoverAllBtns();
                btn.startHover();

                break;
            case MouseEvent.MOUSE_EXITED:
//                btn.endHover();
                break;
            case MouseEvent.MOUSE_PRESSED:
                if (e.getButton() == MouseEvent.BUTTON1) {
                    stopDrawingArrows();
                    if (btn != null && !isDragging() && btn.isEnabled() && btn.getIcon() != null) {
                        currentDragging = btn;
                        btn.hideIcon();
                        btn.clickMe();
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    startDrawing();
                }
                break;
            case MouseEvent.MOUSE_RELEASED:
                if (e.getButton() == MouseEvent.BUTTON1) {
                    BoardButton currentlyAbove = getBtn(mouseCoordinates);
                    stopDragging(currentlyAbove);
                    if (currentlyAbove.canMoveTo())
                        currentlyAbove.clickMe();

                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    if (isSameBtn(btn) && isDrawing)
                        view.highLightButton(btn);
                    stopDrawingArrows();

                } else {
                    clearAllArrows();
                    view.resetSelectedButtons();
                }
                break;
        }
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

    public void startDrawing() {
        startedAt = new Point(mouseCoordinates);
        isDrawing = true;
    }

    private void stopDragging(BoardButton currentlyHoveringOver) {
        if (isDragging()) {
            if (currentDragging != currentlyHoveringOver) {
                if (currentlyHoveringOver.isEnabled())
                    currentlyHoveringOver.clickMe();
                else
                    currentDragging.clickMe();
            }
            currentDragging.unHideIcon();
            currentDragging = null;
        }
    }

    private boolean isSameBtn(JButton btn) {
        return btn.getBounds().contains(mouseCoordinates);
    }

    public void clearAllArrows() {
        arrows = new ArrayList<>();
    }


    public JLayer getJlayer() {
        return jlayer;
    }

    public void drawMove(Move move) {
        Point start = view.getBtn(move.getMovingFrom()).getLocation();
        Point end = view.getBtn(move.getMovingTo()).getLocation();
        arrows.add(newArrow(start, end));
    }

    public Point getPoint() {
        return mouseCoordinates;
    }

    public void drawArrow(Location from, Location loc, Color clr) {
        Point start = view.getBtn(from).getLocation();
        Point end = view.getBtn(loc).getLocation();
        arrows.add(newArrow(start, end, clr));
        jlayer.repaint();
    }


}