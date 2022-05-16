package ver14.view.Board;

import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.UI.MyJframe.MyAdapter;
import ver14.view.View;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * represents the Board's overlay. responsible for drawing arrows, detecting button clicks, and detecting held down buttons for selecting colors.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class BoardOverlay extends LayerUI<JPanel> {
    /**
     * The constant defaultColor.
     */
    final static Color defaultColor = new Color(0, 0, 0, (int) (255 * 0.9));
    /**
     * The constant keyClrMap.
     */
    private static final Map<Integer, Color> keyClrMap;
    /**
     * The constant NO_KEY.
     */
    private final static Integer NO_KEY = null;
    /**
     * The constant mouseCoordinates.
     */
    public static Point mouseCoordinates, /**
     * The Started at.
     */
    startedAt;

    static {
        keyClrMap = new HashMap<>();
        keyClrMap.put(KeyEvent.VK_SHIFT, Color.decode("#9FC0A2"));
        keyClrMap.put(KeyEvent.VK_CONTROL, Color.decode("#D36D6D"));
        keyClrMap.put(KeyEvent.VK_ALT, Color.decode("#E8A43F"));
    }

    /**
     * The View.
     */
    private final View view;
    /**
     * The Is drawing.
     */
    public boolean isDrawing = false;
    /**
     * The Arrows.
     */
    private ArrayList<Arrow> arrows;
    /**
     * The Jlayer.
     */
    private JLayer<?> jlayer;
    /**
     * The Block board.
     */
    private boolean blockBoard = false;
    /**
     * The Pressed key.
     */
    private Integer pressedKey = NO_KEY;
    /**
     * The Current btn.
     */
    private BoardButton currentBtn = null;

    /**
     * Instantiates a new Board overlay.
     *
     * @param view the view
     */
    public BoardOverlay(View view) {
        this.view = view;
        arrows = new ArrayList<>();


    }

    /**
     * Create polygon.
     *
     * @param pointPairs the point pairs
     * @return the polygon
     */
    public static Polygon createPolygon(int... pointPairs) {
        assert pointPairs.length % 2 == 0;
        Point[] points = new Point[pointPairs.length / 2];
        for (int i = 0, j = 0; j < pointPairs.length; i++, j += 2) {
            points[i] = new Point(pointPairs[j], pointPairs[j + 1]);
        }
        return createPolygon(points);
    }

    /**
     * Create polygon.
     *
     * @param points the points
     * @return the polygon
     */
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

    /**
     * Create clrs adapters. used for detecting selected colors.
     *
     * @return the array list
     */
    public ArrayList<MyAdapter.HeldDown> createClrs() {

        var list = new ArrayList<MyAdapter.HeldDown>() {
        };

        keyClrMap.forEach((k, clr) -> list.add(adapter(k)));
        return list;
    }

    /**
     * create an adapter for a helddown key .
     *
     * @param key the key
     * @return the created adapter
     */
    private MyAdapter.HeldDown adapter(int key) {
        return new MyAdapter.HeldDown() {
            @Override
            public void startPress() {
                pressedKey = key();
                repaintLayer();
            }

            @Override
            public void endPress() {
                pressedKey = NO_KEY;
                repaintLayer();
            }

            @Override
            public int key() {
                return key;
            }
        };
    }

    /**
     * Repaint layer.
     */
    public void repaintLayer() {
        jlayer.repaint();
    }

    /**
     * Sets block board.
     *
     * @param blockBoard the block board
     */
    public void setBlockBoard(boolean blockBoard) {
        this.blockBoard = blockBoard;
        repaintLayer();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        super.paint(g2, c);
        if (mouseCoordinates != null) {
            if (currentBtn != null) {
                currentBtn.globalPaint(g2, mouseCoordinates, c);
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
            g2.setColor(new Color(0, 0, 0, (int) (255 * 0.1)));
            g2.fill(shape);
        }
    }

    /**
     * create a New arrow.
     *
     * @param start the start
     * @param end   the end
     * @return the arrow
     */
    private Arrow newArrow(Point start, Point end) {
        return newArrow(start, end, currentColor());
    }

    /**
     * create a New arrow.
     *
     * @param start the start
     * @param end   the end
     * @param clr   the clr
     * @return the arrow
     */
    private Arrow newArrow(Point start, Point end, Color clr) {
        start = centerPoint(start);
        end = centerPoint(end);
        return new Arrow(start, end, clr);
    }

    /**
     * Current selected color. if no color is selected, the default color is returned.
     *
     * @return the color
     */
    public Color currentColor() {
        return ((pressedKey == NO_KEY) || !keyClrMap.containsKey(pressedKey)) ? defaultColor : keyClrMap.get(pressedKey);
    }

    /**
     * Center point in the middle of a button.
     *
     * @param point the point
     * @return the point
     */
    private Point centerPoint(Point point) {
        BoardButton btn = getBtn(point);
        assert btn != null;
        point = btn.getLocation();
        point.x += btn.getWidth() / 2;
        point.y += btn.getHeight() / 2;
        return point;
    }

    /**
     * Gets a button that the given point is inside.
     *
     * @param point the point
     * @return the btn
     */
    private BoardButton getBtn(Point point) {
        return view.getBtn(createPointLoc(point));
    }

    /**
     * Creates a location from a point on the board.
     *
     * @param point the point
     * @return the location
     */
    private Location createPointLoc(Point point) {
        Location loc = getLoc(point);
        return loc;
    }

    /**
     * converts a {@link Point} (x,y) to a {@link Location}
     *
     * @param point the point
     * @return the loc
     */
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

    /**
     * Is the mouse inside the board pnl.
     *
     * @return the boolean
     */
    private boolean isInsideBoardPnl() {
        return mouseCoordinates != null;
//        return mouseCoordinates == null || view.getBoardPnl().getBounds().contains(mouseCoordinates);
    }

    /**
     * Process mouse.
     *
     * @param e the event
     */
    private void processMouse(MouseEvent e) {
        BoardButton btn = (BoardButton) e.getSource();
//        if (e.getID() != MouseEvent.MOUSE_ENTERED && e.getID() != MouseEvent.MOUSE_EXITED)
//            debugCurrentMouseInfo(e);
        switch (e.getID()) {
            case MouseEvent.MOUSE_ENTERED:
                btn.startHover();
                break;
            case MouseEvent.MOUSE_EXITED:
                btn.endHover();
                break;
            case MouseEvent.MOUSE_PRESSED:
                if (e.getButton() != MouseEvent.BUTTON1) {
                    stopCurrent();
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    startDrawing();
                } else {
                    if (btn.isEnabled()) {
                        currentBtn = btn;

                        if (!currentBtn.is(State.CLICKED_ONCE)) {
                            currentBtn.clickMe();
                        }
                        if (!currentBtn.is(State.MOVING_TO)) {
//                            currentBtn.addState(BoardButton.State.DRAGGING);
                        } else stopCurrent();
                    } else {
                        stopCurrent();
                    }
                    stopDrawingArrows();
                }
                break;
            case MouseEvent.MOUSE_RELEASED:
                if (!isInsideBoardPnl()) {
                    stopCurrent();
                    return;
                }
                BoardButton currentlyAbove = getBtn(mouseCoordinates);

                switch (e.getButton()) {
                    case MouseEvent.BUTTON1 -> {

                        if (currentBtn != null) {
                            if (currentBtn.is(State.CLICKED_ONCE)) {
                                if (currentBtn == currentlyAbove || !currentlyAbove.canMoveTo()) {
                                    currentBtn.clickMe();
                                } else {
                                    currentlyAbove.clickMe();
                                }
                                currentBtn.removeState(State.CLICKED_ONCE);
                            } else if (currentBtn.is(State.DRAGGING)) {
                                if (currentlyAbove != currentBtn && !currentlyAbove.canMoveTo()) {
                                    currentBtn.clickMe();
                                } else if (currentlyAbove.canMoveTo()) {
                                    currentlyAbove.clickMe();
                                }
                                currentBtn.removeState(State.DRAGGING);
                                if (currentBtn == currentlyAbove) {
                                    currentBtn.addState(State.CLICKED_ONCE);
                                }
                            }
                        }

                        clearAllArrows();
                        view.resetSelectedButtons();
                    }
                    case MouseEvent.BUTTON3 -> {
                        stopCurrent();
                        if (isSameBtn(btn) && isDrawing)
                            btn.toggleSelected();
                        stopDrawingArrows();
                    }
                }
                break;
        }
    }

    /**
     * Stop current button.
     */
    private void stopCurrent() {
        if (currentBtn != null) {
            boolean clickAfter = currentBtn.is(State.DRAGGING | State.HOVERED);
            currentBtn.removeState(State.CLICKED_ONCE | State.DRAGGING | State.HOVERED);
            if (clickAfter)
                currentBtn.clickMe();
            currentBtn = null;
        }
    }

    /**
     * Stop drawing arrows.
     */
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

    /**
     * Start drawing arrow.
     */
    public void startDrawing() {
        startedAt = new Point(mouseCoordinates);
        isDrawing = true;
    }

    /**
     * is the given button the same one the mouse is over rn.
     *
     * @param btn the btn
     * @return the boolean
     */
    private boolean isSameBtn(JButton btn) {
        return btn.getBounds().contains(mouseCoordinates);
    }

    /**
     * Clear all arrows.
     */
    public void clearAllArrows() {
        arrows = new ArrayList<>();
    }

    /**
     * Gets jlayer.
     *
     * @return the jlayer
     */
    public JLayer getJlayer() {
        return jlayer;
    }

    /**
     * Draws an arrow from the source to the destination of a move.
     *
     * @param move the move
     */
    public void drawMove(Move move) {
        Point start = view.getBtn(move.getSource()).getLocation();
        Point end = view.getBtn(move.getDestination()).getLocation();
        arrows.add(newArrow(start, end));
    }


    /**
     * Draw arrow.
     *
     * @param from the from
     * @param loc  the loc
     * @param clr  the clr
     */
    public void drawArrow(Location from, Location loc, Color clr) {
        Point start = view.getBtn(from).getLocation();
        Point end = view.getBtn(loc).getLocation();
        arrows.add(newArrow(start, end, clr));
        jlayer.repaint();
    }

}