package ver34_faster_move_generation.view_classes;


import ver34_faster_move_generation.Controller;
import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.Player;
import ver34_faster_move_generation.model_classes.moves.Move;
import ver34_faster_move_generation.view_classes.dialogs_classes.Message;
import ver34_faster_move_generation.view_classes.dialogs_classes.dialog_objects.DialogLabel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;


public class View implements Iterable<BoardButton[]> {
    private final int ROWS;
    private final int COLS;


    private final Font menuItemsFont = new Font(null, Font.BOLD, 20);
    private final Font messagesFont = new Font(null, Font.BOLD, 30);
    private final Font debugItemsFont = new Font(null, Font.BOLD, 20);
    private final Color checkColor = new Color(186, 11, 11, 255);

    private final Color red = Color.red;
    private final Color yellow = Color.yellow;
    private final Color currentBtnColor = Color.BLUE;
    private final Color promotingSquareColor = new Color(151, 109, 3);
    private final Controller controller;
    private final double winToScreenResolutionRatio = 0.8;
    private BoardPanel boardPnl;
    private SidePanel sidePanel;
    private JMenuBar menuBar;
    private JMenu debugMenu;
    private Dimension winSize;
    private JPanel topPnl, runningProcessPnl, bottomPnl;
    private JLabel statusLbl;
    private int boardOrientation;
    private MyJframe win;
    private JLabel runningProcessLbl;

    public View(int boardSize, Controller controller, int boardOrientation, long millis) {
        ROWS = COLS = boardSize;
        this.controller = controller;
        this.boardOrientation = boardOrientation;
        sidePanel = new SidePanel(millis, isBoardFlipped());
        createGui();
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public BoardPanel getBoardPnl() {
        return boardPnl;
    }

    public void showOnScreen(int screen, JFrame frame) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        if (screen >= gd.length || screen <= -1)
            screen = 0;
        Dimension d = new Dimension(gd[screen].getDefaultConfiguration().getBounds().getSize());
        frame.setSize(d);
        int div = 1;
        int x = gd[screen].getDefaultConfiguration().getBounds().getLocation().x / div;
        int y = gd[screen].getDefaultConfiguration().getBounds().getLocation().y / div;
        frame.setLocation(x, y);

        frame.setVisible(true);
    }

    public void initGame() {
        boardPnl.getBoardOverlay().clearAllArrows();
        resetAllBtns();
    }

    public void resetAllBtns() {
        boardPnl.resetAllButtons(true);
    }


    public void setBtnIcon(Location loc, ImageIcon icon) {
        getBtn(loc).setIcon(icon);
    }


    public void createGui() {
        setRelativeSizes();
        win = new MyJframe(this);
        win.setForeground(Color.BLACK);
        win.setSize(winSize);

        win.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        win.setLocationRelativeTo(null);
        win.setLayout(new GridBagLayout());

        boardPnl = new BoardPanel(ROWS, COLS, this);

        topPnl = new JPanel();
        runningProcessPnl = new JPanel();
        bottomPnl = new JPanel();
        menuBar = new JMenuBar();

        JMenu settingsMenu = new JMenu("Menu");
        settingsMenu.setFont(menuItemsFont);

        JCheckBoxMenuItem flipBoard = new JCheckBoxMenuItem("Flip Board");
        flipBoard.setFont(menuItemsFont);
        flipBoard.addActionListener(e -> {
//            isBoardFlipped = flipBoard.isSelected();
            controller.flipBoard();
        });
        flipBoard.setState(isBoardFlipped());
        settingsMenu.add(flipBoard);

        Font uiBtnsFont = new Font(null, Font.BOLD, 30);
        //create a debug menu and add it to the settings menu
        createDebugMenu(settingsMenu);

        menuBar.add(settingsMenu);

        statusLbl = new JLabel("STATUS LBL");
        statusLbl.setFont(messagesFont);
        bottomPnl.add(statusLbl);

        MyJButton newGameBtn = new MyJButton("New Game", uiBtnsFont, (Void) -> {
            controller.newGameBtnPressed();
            return null;
        }, topPnl);

        MyJButton evalBtn = new MyJButton("EVAL", uiBtnsFont, (Void) -> {
            controller.evalBtnPressed();
            return null;
        }, topPnl);

        MyJButton aiMoveBtn = new MyJButton("Ai Move", uiBtnsFont, (Void) -> {
            controller.aiMoveButtonPressed();
            return null;
        }, topPnl);

        runningProcessLbl = new JLabel();
        runningProcessLbl.setFont(uiBtnsFont);
        runningProcessPnl.add(runningProcessLbl);

        MyJButton runningProcessBtn = new MyJButton("Stop", uiBtnsFont, (Void) -> {
            controller.stopRunningProcess();
            return null;
        }, runningProcessPnl);
        runningProcessPnl.setVisible(false);
//        topPnl.add(runningProcessPnl);

        boardPnl.createMat();
        resetBackground();
        layoutSetup();
        showOnScreen(1, win);
//        win.setVisible(true);

    }

    public MyJframe getWin() {
        return win;
    }

    public int getBoardOrientation() {
        return boardOrientation;
    }

    public void setBoardOrientation(int boardOrientation) {
        this.boardOrientation = boardOrientation;
        boardPnl.setCoordinates(false);
        boardPnl.createMat();
        boardPnl.getBoardOverlay().repaintLayer();
    }


    private void createDebugMenu(JMenu settingsMenu) {
        debugMenu = new JMenu("Debug");

        createDebugSlider("Scan Time:",
                Controller.MIN_SCAN_TIME,
                Controller.MAX_SCAN_TIME,
                Controller.DEFAULT_SCAN_TIME,
                value -> {
                    controller.setScanTime(value);
                    return null;
                });
        createDebugSlider("Scan Flexibility:",
                Controller.MIN_SCAN_TIME_FLEXIBILITY,
                Controller.MAX_SCAN_TIME_FLEXIBILITY,
                Controller.DEFAULT_SCAN_TIME_FLEXIBILITY,
                value -> {
                    controller.setScanTimeFlexibility(value);
                    return null;
                });
        createDebugBtn("Print Board", (Void) -> {
            controller.printBoard();
            return null;
        });
        createDebugBtn("Print Current Board FEN", (Void) -> {
            controller.printFEN();
            return null;
        });
        createDebugBtn("Print All Pieces", (Void) -> {
            controller.printAllPieces();
            return null;
        });
        createDebugBtn("Print and Draw All Possible Moves", (Void) -> {
            controller.printAllPossibleMoves();
            return null;
        });
        createDebugBtn("Highlight En Passant Target Square", (Void) -> {
            controller.highlightEnPassantTargetSquare();
            return null;
        });
        createDebugBtn("Print num of positions", (Void) -> {
            controller.printNumOfPositions();

            return null;
        });
        createDebugBtn("Select Starting Position", (Void) -> {
            controller.selectStartingPosition();
            return null;
        });
        createDebugBtn("Draw Controlled Squares", (Void) -> {
            controller.drawControlledSquares();
            return null;
        });
        createDebugBtn("Captures Eval", (Void) -> {
            controller.printCapturesEval();
            return null;
        });
//        createDebugBtn("Flip Buttons", (Void) -> {
//            getBoardOverlay().flipButtons();
//            return null;
//        });
        settingsMenu.add(debugMenu);

    }

    private void createDebugBtn(String text, Function<Void, Void> callback) {
        JButton btn = new JButton(text);
        btn.setFont(debugItemsFont);
        btn.setFocusable(false);
        btn.addActionListener(e -> {
            callback.apply(null);
            MenuSelectionManager.defaultManager().clearSelectedPath();
        });
        debugMenu.add(btn);
    }

    private void createDebugSlider(String text, int min, int max, int initialValue, Function<Integer, Void> callback) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, initialValue);
        JLabel sliderLbl = new JLabel(initialValue + "", JLabel.CENTER);
        sliderLbl.setFont(debugItemsFont);
        ChangeListener cl_t = e -> {
            JSlider x = (JSlider) e.getSource();
            sliderLbl.setText(x.getValue() + "");
            callback.apply(x.getValue());
        };
        slider.setFont(debugItemsFont);
        slider.addChangeListener(cl_t);
        slider.setMajorTickSpacing(9);
        slider.setMinorTickSpacing(0);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);

        debugMenu.add(new JLabel(text) {{
            setFont(debugItemsFont);
        }});
        debugMenu.add(sliderLbl);
        debugMenu.add(slider);
    }


    private void layoutSetup() {
        //שורת התפריטים
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        win.add(menuBar, gbc);
        //שורה עליונה
        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        win.add(topPnl, gbc);

        boardPnl.boardContainerSetup();

        gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridheight = GridBagConstraints.RELATIVE;
        gbc.weightx = 2;
        gbc.weighty = 2;
        gbc.fill = GridBagConstraints.BOTH;
        win.add(boardPnl, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 1;
        win.add(runningProcessPnl, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        win.add(sidePanel, gbc);

        //שורה תחתונה

        gbc = new GridBagConstraints();

        win.add(bottomPnl, gbc);

        win.pack();
    }


    private void setRelativeSizes() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = (int) (gd.getDisplayMode().getWidth() * winToScreenResolutionRatio);
        int height = (int) (gd.getDisplayMode().getHeight() * winToScreenResolutionRatio);

        //btnDimension = new Dimension(btnDimension.width, btnDimension.height);
        winSize = new Dimension(height, height);
    }

    public void clearAllDrawings() {
        boardPnl.getBoardOverlay().clearAllArrows();
    }

    public void drawMove(Move move) {
        boardPnl.getBoardOverlay().drawMove(move);
    }

    private void enableMovesLog(boolean bool) {
//        movesLog.setVisible(bool);
    }

    public boolean isBoardFlipped() {
        return boardOrientation != Player.WHITE;
    }

    public BoardButton boardButtonPressed(Location to) {
        controller.boardButtonPressed(to);
        return getBtn(to);
    }

    public void setStatusLbl(String str) {
        statusLbl.setText(str);
    }

    public void updateMoveLog(String move, int moveNum) {
        sidePanel.addMoveStr(move, moveNum);
    }


    public Location getBtnLoc(JButton source) {
        return ((BoardButton) source).getBtnLoc();
    }

    public void resetBackground() {
        for (BoardButton[] row : this) {
            for (BoardButton btn : row) {
                btn.resetBackground();
            }
        }
    }

    public void highlightSquare(Location p, Color color) {
        getBtn(p).setBackground(color);
    }

    public void enableSquare(Location loc, boolean enabled) {
//        if (isBoardFlipped)
//            loc = Location.flipLocation(loc);
        getBtn(loc).setEnabled(enabled);
    }

    public void enableAllSquares(boolean bool) {
        for (BoardButton[] row : this) {
            for (BoardButton btn : row) {
                btn.setEnabled(bool);
            }
        }
    }


    public BoardButton getBtn(int r, int c) {
        return boardPnl.getBtn(r, c);
    }
//
//    public void setBtn(int r, int c, BoardButton button) {
//        btnMat[r][c] = button;
//    }
//
//    public void setBtn(Location loc, BoardButton button) {
//        setBtn(loc.getRow(), loc.getCol(), button);
//    }

    public BoardButton getBtn(Location loc) {
        return getBtn(loc.getRow(), loc.getCol());
    }

    public void updateBoardButton(Location prevLoc, Location newLoc) {
        BoardButton prevBtn = getBtn(prevLoc);
        BoardButton newBtn = getBtn(newLoc);
        newBtn.setIcon(prevBtn.getIcon());
        prevBtn.setIcon(null);
    }

    public void highlightPath(ArrayList<Move> movableSquares) {
        enableAllSquares(false);
        if (movableSquares != null)
            for (Move move : movableSquares) {
                Location movingTo = move.getMovingTo();
//                if (isBoardFlipped)
//                    movingTo.flipRow();
                if (move.isCapturing())
                    highlightSquare(movingTo, red);
                else if (move.getMoveFlag() == Move.MoveFlag.Promotion)
                    highlightSquare(movingTo, promotingSquareColor);
                else
                    highlightSquare(movingTo, yellow);

                enableSquare(movingTo, true);
            }
    }

    public void enableSquares(ArrayList<Location> list) {
        enableAllSquares(false);
        for (Location loc : list) {
            enableSquare(loc, true);
        }
    }

    public void gameOver() {
        enableAllSquares(false);
    }

    public void showMessage(String message, String title, ImageIcon icon) {
        Message msgDialog = new Message(title, new DialogLabel(message, icon));
        msgDialog.run();
    }

    public void inCheck(Location kingLoc) {
        getBtn(kingLoc).setBackground(checkColor);
    }

    public void highLightButton(JButton btn) {
        ((BoardButton) btn).toggleSelected();
    }

    public void resetSelectedButtons() {
        for (BoardButton[] row : this) {
            for (BoardButton btn : row) {
                btn.setIsSelected(false);
            }
        }
    }

    public void colorCurrentPiece(Location loc) {
        getBtn(loc).setBackground(currentBtnColor);
    }

    public void drawArrow(Location from, Location to, Color clr) {
        boardPnl.getBoardOverlay().drawArrow(from, to, clr);
    }

    public void setRunningProcessStr(String runningProcess) {
        if (runningProcess == null) {
            runningProcessPnl.setVisible(false);
            return;
        }
        runningProcessLbl.setText(runningProcess);
        runningProcessPnl.setVisible(true);
    }

    @Override
    public Iterator<BoardButton[]> iterator() {
        return Arrays.stream(boardPnl.getBtnMat()).iterator();
    }

    public void exitButtonPressed() {
        controller.exitButtonPressed();
    }
}
