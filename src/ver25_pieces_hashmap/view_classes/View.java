package ver25_pieces_hashmap.view_classes;


import ver25_pieces_hashmap.Controller;
import ver25_pieces_hashmap.Location;
import ver25_pieces_hashmap.model_classes.pieces.Piece;
import ver25_pieces_hashmap.moves.Move;
import ver25_pieces_hashmap.moves.PromotionMove;
import ver25_pieces_hashmap.view_classes.dialogs_classes.Message;
import ver25_pieces_hashmap.view_classes.dialogs_classes.YesOrNo;
import ver25_pieces_hashmap.view_classes.dialogs_classes.dialog_objects.DialogLabel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;


public class View implements Iterable<BoardButton[]> {
    private final int ROWS;
    private final int COLS;


    private final Font menuItemsFont = new Font(null, Font.BOLD, 20);
    private final Font messagesFont = new Font(null, Font.BOLD, 30);
    private final Font coordinatesFont = new Font(null, Font.BOLD, 30);
    private final Font debugItemsFont = new Font(null, Font.BOLD, 20);
    private final Color checkColor = new Color(186, 11, 11, 255);
    private final Color brown = new Color(79, 60, 33, 255);
    private final Color white = new Color(222, 213, 187);
    private final Color red = Color.red;
    private final Color yellow = Color.yellow;
    private final Color currentBtnColor = Color.BLUE;
    private final Color promotingSquareColor = new Color(151, 109, 3);
    private final Color moveTextFieldBackgroundColor = Color.WHITE;
    private final Color moveTextFieldWrongMoveBackgroundColor = new Color(255, 0, 0, 50);
    private final Controller controller;
    private final Dimension btnDimension = new Dimension(90, 100);
    //private double btnToScreenResolutionRatio =
    private final double winToScreenResolutionRatio = 0.8;
    private BoardPanel boardPnl;
    private JPanel colsCoordinatesPnl, rowsCoordinatesPnl;
    private JMenuBar menuBar;
    private JMenu debugMenu;
    private Dimension winSize;
    private JPanel boardContainerPnl, topPnl, runningProcessPnl, bottomPnl;
    //    private JLabel scanTimeSliderLabel;
    private JLabel statusLbl;
    private int boardOrientation;
    private boolean isBoardFlipped;
    private BoardButton[][] btnMat;
    private MyJframe win;
    private LayerUI<JPanel> layerUI;
    private JLabel runningProcessLbl;

    public View(int boardSize, Controller controller, int boardOrientation) {
        ROWS = COLS = boardSize;
        this.controller = controller;
        this.boardOrientation = boardOrientation;
        this.isBoardFlipped = boardOrientation != Piece.Player.WHITE;
        createGui();
    }

    public BoardPanel getBoardPnl() {
        return boardPnl;
    }

    public void showOnScreen(int screen, JFrame frame) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        if (screen > -1 && screen < gd.length) {
//            frame.setAlwaysOnTop(true);
            Dimension d = new Dimension(gd[screen].getDefaultConfiguration().getBounds().getSize());
//            d.width = d.height;
            frame.setSize(d);
            int x = gd[screen].getDefaultConfiguration().getBounds().getLocation().x;
            int y = gd[screen].getDefaultConfiguration().getBounds().getLocation().y;
//            y += d.width;
            frame.setLocation(x, y);
            //frame.setLocation(gd[screen].setFullScreenWindow(frame).getBounds().x, frame.getY());
        } else if (gd.length > 0) {
            frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY());
        } else {
            throw new RuntimeException("No Screens Found");
        }
        frame.setVisible(true);
    }

    public void initGame() {
        ((BoardOverlay) layerUI).clearAllArrows();
        resetAllBtns();
    }

    public void resetAllBtns() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                btnMat[i][j].setIcon(null);
            }
        }
        resetBackground();
    }

    public BoardOverlay getBoardOverlay() {
        return (BoardOverlay) layerUI;
    }

    public void setBtnIcon(Location loc, ImageIcon icon) {
        getBtn(loc).setIcon(icon);
    }

    public BoardButton[][] getBtnMat() {
        return btnMat;
    }

    private void setCoordinates() {
        setCoordinates(true);
    }

    private void setCoordinates(boolean initialize) {
        if (initialize) {
            colsCoordinatesPnl = new JPanel();
            rowsCoordinatesPnl = new JPanel();

            colsCoordinatesPnl.setLayout(new GridLayout(1, 8));
            rowsCoordinatesPnl.setLayout(new GridLayout(8, 1));
        } else {
            colsCoordinatesPnl.removeAll();
            rowsCoordinatesPnl.removeAll();
        }
        for (int i = 0; i < 8; i++) {
            JLabel col = new JLabel(Character.toString(i + 'a') + "");
            JLabel row = new JLabel((i + 1) + "");
            col.setPreferredSize(btnDimension);
            row.setPreferredSize(btnDimension);
            col.setFont(coordinatesFont);
            row.setFont(coordinatesFont);
            colsCoordinatesPnl.add(col);
            rowsCoordinatesPnl.add(row);
        }
        colsCoordinatesPnl.setPreferredSize(new Dimension(btnDimension.width * 8, btnDimension.height * 8));
        rowsCoordinatesPnl.setPreferredSize(new Dimension(btnDimension.width * 8, btnDimension.height * 8));

    }

    public void createGui() {
        setRelativeSizes();
        win = new MyJframe(this);
        win.setForeground(Color.BLACK);
        win.setSize(winSize);
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                YesOrNo confirm = new YesOrNo("Exit Confirmation", "Are You Sure You Want To Exit?");
                int res = (int) confirm.run();
                if (res == YesOrNo.YES) {
                    controller.exitButtonPressed();
                }
            }
        };
        win.addWindowListener(exitListener);
        win.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        win.setLocationRelativeTo(null);
        win.setLayout(new GridBagLayout());

        boardPnl = new BoardPanel();
        boardContainerPnl = new JPanel();
        boardContainerPnl.setLayout(new GridBagLayout());
        setCoordinates();

        topPnl = new JPanel();
        runningProcessPnl = new JPanel();
        bottomPnl = new JPanel();
        menuBar = new JMenuBar();

        JMenu settingsMenu = new JMenu("Menu");
        settingsMenu.setFont(menuItemsFont);

//        Dimension d = new Dimension(1000, 1000);
//        movesLog.setMaximumSize(d);
//        movesLog.setPreferredSize(d);
//        movesLog.setPreferredSize(d);

        JCheckBoxMenuItem toggleMovesLog = new JCheckBoxMenuItem("Moves Log");
        toggleMovesLog.setFont(menuItemsFont);
        toggleMovesLog.addActionListener(e -> enableMovesLog(toggleMovesLog.isSelected()));
        toggleMovesLog.setState(false);
        enableMovesLog(toggleMovesLog.isSelected());
        settingsMenu.add(toggleMovesLog);

        JCheckBoxMenuItem flipBoard = new JCheckBoxMenuItem("Flip Board");
        flipBoard.setFont(menuItemsFont);
        flipBoard.addActionListener(e -> {
            isBoardFlipped = flipBoard.isSelected();
            controller.flipBoard(isBoardFlipped);
        });
        flipBoard.setState(isBoardFlipped);
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
        topPnl.add(runningProcessPnl);

//        moveTextField.addActionListener(e -> {
//            System.out.println(e);
//            JTextField textField = (JTextField) e.getSource();
//            textField.setBackground(null);
//            textField.setText("");
//            if (controller.enteredMoveText(textField.getText())) {
//                textField.setBackground(moveTextFieldBackgroundColor);
//
//            } else {
//                textField.setBackground(moveTextFieldWrongMoveBackgroundColor);
//
//            }
//
//        });

        btnMat = new BoardButton[ROWS][COLS];
        boardPnl.setLayout(new GridLayout(ROWS, COLS));
        resetMat();

        resetBackground();
        layoutSetup();
//        showOnScreen(1, win);
        win.setVisible(true);

    }

    public MyJframe getWin() {
        return win;
    }

    public int getBoardOrientation() {
        return boardOrientation;
    }

    public void setBoardOrientation(int boardOrientation) {
        this.boardOrientation = boardOrientation;
        setCoordinates(false);
        getBoardOverlay().repaintLayer();
    }

    private void resetMat() {
        boolean isBlack = false;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Location btnLoc = new Location(i, j, boardOrientation == Piece.Player.WHITE);
                BoardButton currentBtn = new BoardButton(btnLoc, isBlack ? brown : white);
                currentBtn.setFont(new Font(null, Font.BOLD, 50));
                setButton(currentBtn, btnLoc);
                boardPnl.add(currentBtn);
                isBlack = !isBlack;
            }
            isBlack = !isBlack;
        }
    }

    private void setButton(BoardButton button, Location loc) {
        btnMat[loc.getRow()][loc.getCol()] = button;
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
        createDebugBtn("Load Buttons", (Void) -> {
            ((BoardOverlay) layerUI).loadButtons();
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
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        win.add(menuBar, gbc);
        //שורה עליונה
        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        win.add(topPnl, gbc);

        //תיעוד המשחק
        gbc = new GridBagConstraints();
        gbc.weighty = 10;
        gbc.weightx = 10;
        gbc.gridwidth = 10;
        gbc.fill = GridBagConstraints.BOTH;
//        JScrollPane scroll = new JScrollPane(movesLog,
//                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        win.add(movesLog, gbc);

        boardContainerSetup();
        gbc = new GridBagConstraints();
        gbc.weightx = 20;
        gbc.weighty = 20;
        gbc.gridheight = 15;
//        gbc.gridwidth = 10;
//        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        win.add(boardContainerPnl, gbc);

        //שורה תחתונה

        gbc = new GridBagConstraints();
        gbc.gridy = 100;
        gbc.weighty = 2;
        gbc.weightx = 12;
        gbc.gridheight = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        win.add(bottomPnl, gbc);
    }

    private void boardContainerSetup() {
        //הוספת הקורדינאטות
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        boardContainerPnl.add(colsCoordinatesPnl, gbc);

        gbc = new GridBagConstraints();
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weighty = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        boardContainerPnl.add(rowsCoordinatesPnl, gbc);

        layerUI = new BoardOverlay(this);
        JLayer<JPanel> jlayer = new JLayer<>(boardPnl, layerUI);

        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 100;
        gbc.weighty = gbc.weightx = 30;
        gbc.fill = GridBagConstraints.BOTH;
        boardContainerPnl.add(jlayer, gbc);


    }

    private void setRelativeSizes() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = (int) (gd.getDisplayMode().getWidth() * winToScreenResolutionRatio);
        int height = (int) (gd.getDisplayMode().getHeight() * winToScreenResolutionRatio);

        //btnDimension = new Dimension(btnDimension.width, btnDimension.height);
        winSize = new Dimension(height, height);
    }

    public void clearAllDrawings() {
        ((BoardOverlay) layerUI).clearAllArrows();
    }

    public void drawMove(Move move) {
        ((BoardOverlay) layerUI).drawMove(move);
    }

    private void enableMovesLog(boolean bool) {
//        movesLog.setVisible(bool);
    }

    public boolean isBoardFlipped() {
        return isBoardFlipped;
    }

    public BoardButton boardButtonPressed(Location to) {
        controller.boardButtonPressed(to);
        return getBtn(to);
    }

    public void setStatusLbl(String str) {
        statusLbl.setText(str);
    }

    public void updateMoveLog(String move) {
//        movesLog.setText(movesLog.getText() + " " + move);
    }


    public Location getBtnLoc(JButton source) {
        return ((BoardButton) source).getBtnLoc();
    }

    public void resetBackground() {
        for (BoardButton[] row : this) {
            for (BoardButton btn : row) {
                btn.resetButton();
            }
        }
    }

    public Dimension getBtnDimension() {
        return btnDimension;
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
        return btnMat[r][c];
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
                else if (move instanceof PromotionMove)
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

    public BoardButton getBtn() {
        return btnMat[0][0];
    }

    public void colorCurrentPiece(Location loc) {
        getBtn(loc).setBackground(currentBtnColor);
    }

    public void drawArrow(Location from, Location to, Color clr) {
        ((BoardOverlay) layerUI).drawArrow(from, to, clr);
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
        return Arrays.stream(btnMat).iterator();
    }
}