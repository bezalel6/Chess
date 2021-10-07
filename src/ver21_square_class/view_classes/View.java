package ver21_square_class.view_classes;


import ver21_square_class.Controller;
import ver21_square_class.Location;
import ver21_square_class.moves.Move;
import ver21_square_class.moves.PromotionMove;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Function;


public class View {
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
    private final Dimension btnDimension = new Dimension(100, 100);
    //private double btnToScreenResolutionRatio =
    private final double winToScreenResolutionRatio = 0.8;
    private JPanel boardPnl, colsCoordinatesPnl, rowsCoordinatesPnl;
    private JMenuBar menuBar;
    private JMenu debugMenu;
    private Dimension winSize;
    private JPanel boardContainerPnl, topPnl, leftPnl, bottomPnl;
    private JTextField moveTextField;
    private JLabel scanTimeSliderLabel;
    private JLabel statusLbl;
    private JTextArea movesLog;
    private boolean isBoardFlipped = true;
    private BoardButton[][] btnMat;
    private JFrameResizing win;
    private LayerUI<JPanel> layerUI;

    public View(Controller controller, int boardSize) {
        ROWS = COLS = boardSize;
        this.controller = controller;
        createGui();
    }

    public JPanel getBoardPnl() {
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
        movesLog.setText("");
        resetAllBtns();
    }

    public void resetAllBtns() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                btnMat[i][j].setIcon(null);
                btnMat[i][j].setDisabledIcon(null);
            }
        }

        resetBackground();
//        for (Piece[] row : board) {
//            for (Piece piece : row) {
//                if (piece != null) {
//                    Location loc = piece.getLoc();
//
//                }
//            }
//        }
//        refreshIconSizes();
    }

    public void setBtnIcon(Location loc, ImageIcon icon) {
        btnMat[loc.getRow()][loc.getCol()].setIcon(icon);
        btnMat[loc.getRow()][loc.getCol()].setDisabledIcon(icon);
    }


    private void setCoordinates() {
        colsCoordinatesPnl = new JPanel();
        rowsCoordinatesPnl = new JPanel();
        colsCoordinatesPnl.setLayout(new GridLayout(1, 8));
        rowsCoordinatesPnl.setLayout(new GridLayout(8, 1));

        for (int i = 0; i < 8; i++) {
            JLabel col = new JLabel(Character.toString((char) (i + 'a')) + "");
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
        win = new JFrameResizing(this);
        win.setForeground(Color.BLACK);
        win.setSize(winSize);
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.setLocationRelativeTo(null);
        win.setLayout(new GridBagLayout());

        boardPnl = new BoardPanel();
        boardContainerPnl = new BoardPanel();
        boardContainerPnl.setLayout(new GridBagLayout());
        setCoordinates();

        topPnl = new JPanel();
        leftPnl = new JPanel();
        bottomPnl = new JPanel();
        menuBar = new JMenuBar();

        JMenu settingsMenu = new JMenu("Menu");
        settingsMenu.setFont(menuItemsFont);

        movesLog = new JTextArea();
        movesLog.setEditable(false);
        movesLog.setFont(menuItemsFont);
        movesLog.setBorder(null);
        movesLog.setLineWrap(true);
        movesLog.setWrapStyleWord(true);
//        movesLog.setMaximumSize(movesLog.getPreferredSize());
        movesLog.setPreferredSize(movesLog.getPreferredSize());

//        movesLog.setPreferredSize(movesLog.getPreferredSize());

        JCheckBoxMenuItem toggleMovesLog = new JCheckBoxMenuItem("Moves Log");
        toggleMovesLog.setFont(menuItemsFont);
        toggleMovesLog.addActionListener(e -> enableMovesLog(toggleMovesLog.isSelected()));
        toggleMovesLog.setState(true);
        enableMovesLog(toggleMovesLog.isSelected());
        settingsMenu.add(toggleMovesLog);

        Font uiBtnsFont = new Font(null, Font.BOLD, 30);
        //create a debug menu and add it to the settings menu
        createDebugMenu(settingsMenu);

        menuBar.add(settingsMenu);

        statusLbl = new JLabel();
        statusLbl.setFont(messagesFont);
        bottomPnl.add(statusLbl);

        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setFont(uiBtnsFont);
        newGameBtn.setFocusable(false);
        newGameBtn.addActionListener(e -> controller.newGameBtnPressed());
        topPnl.add(newGameBtn);

        JButton evalBtn = new JButton("EVAL");
        evalBtn.setFont(uiBtnsFont);
        evalBtn.setFocusable(false);
        evalBtn.addActionListener(e -> controller.evalBtnPressed());
        topPnl.add(evalBtn);

        JButton aiMoveBtn = new JButton("ai move");
        aiMoveBtn.setFont(uiBtnsFont);
        aiMoveBtn.setFocusable(false);
        aiMoveBtn.addActionListener(e -> controller.aiMoveButtonPressed());
        topPnl.add(aiMoveBtn);

        moveTextField = new JFormattedTextField();
        moveTextField.setFont(messagesFont);
        moveTextField.setOpaque(true);
        moveTextField.addActionListener(e -> {
            JTextField textField = (JTextField) e.getSource();
            if (controller.enteredMoveText(textField.getText())) {
                textField.setBackground(moveTextFieldBackgroundColor);

            } else {
                textField.setBackground(moveTextFieldWrongMoveBackgroundColor);

            }
            textField.setText("");
        });
        btnMat = new BoardButton[ROWS][COLS];
        boardPnl.setLayout(new GridLayout(ROWS, COLS));
        boolean isBlack = true;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                btnMat[i][j] = new BoardButton(new Location(i, j), isBlack ? brown : white);
                JButton btn = btnMat[i][j];

                btn.setFont(new Font(null, Font.BOLD, 50));
                btn.setFocusable(false);

                boardPnl.add(btn);
                isBlack = !isBlack;
            }
            isBlack = !isBlack;
        }

        resetBackground();
        layoutSetup();
        showOnScreen(1, win);
        win.setVisible(true);

    }

    private void createDebugMenu(JMenu settingsMenu) {
        debugMenu = new JMenu("Debug");

        JSlider scanTimeSlider = new JSlider(JSlider.HORIZONTAL, Controller.MIN_SCAN_TIME, Controller.MAX_SCAN_TIME, Controller.DEFAULT_SCAN_TIME);
        scanTimeSliderLabel = new JLabel(Controller.DEFAULT_SCAN_TIME + "", JLabel.CENTER);
        scanTimeSliderLabel.setFont(debugItemsFont);
        ChangeListener cl_t = e -> {
            JSlider x = (JSlider) e.getSource();
            scanTimeSliderLabel.setText(x.getValue() + "");
            controller.setScanTime(x.getValue());
        };
        scanTimeSlider.setFont(debugItemsFont);
        scanTimeSlider.addChangeListener(cl_t);
        scanTimeSlider.setMajorTickSpacing(10);
        scanTimeSlider.setMinorTickSpacing(1);
        scanTimeSlider.setPaintLabels(true);
        scanTimeSlider.setPaintTicks(true);

        debugMenu.add(new JLabel("Scan Time:"));
        debugMenu.add(scanTimeSliderLabel);
        debugMenu.add(scanTimeSlider);

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
        createDebugBtn("Compare Pieces Arr And Mat", (Void) -> {
            controller.comparePiecesArrAndMat();
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

        settingsMenu.add(debugMenu);

    }


    private void createDebugBtn(String text, Function callback) {
        JButton btn = new JButton(text);
        btn.setFont(debugItemsFont);
        btn.setFocusable(false);
        btn.addActionListener(e -> {
            callback.apply(null);
            MenuSelectionManager.defaultManager().clearSelectedPath();

        });
        debugMenu.add(btn);
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
        gbc.weighty = 80;
        gbc.weightx = 10;
        gbc.gridwidth = 20;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scroll = new JScrollPane(movesLog,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        win.add(scroll, gbc);

        //לוח המשחק
        boardContainerSetup();
        gbc = new GridBagConstraints();
        gbc.weightx = 20;
        gbc.weighty = 20;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;

        win.add(boardContainerPnl, gbc);

        //שורה תחתונה

        gbc = new GridBagConstraints();
//        gbc.weighty = 2;
//        gbc.weightx = 12;
        //gbc.gridheight = 2;
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

        gbc = new GridBagConstraints();
        gbc.gridheight = 20;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        boardContainerPnl.add(moveTextField, gbc);

    }

    private void setRelativeSizes() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = (int) (gd.getDisplayMode().getWidth() * winToScreenResolutionRatio);
        int height = (int) (gd.getDisplayMode().getHeight() * winToScreenResolutionRatio);

        //btnDimension = new Dimension(btnDimension.width, btnDimension.height);
        winSize = new Dimension(height, height);
    }

    public void deleteAllDrawings() {
        ((BoardOverlay) layerUI).clearAllArrows();
    }

    public void drawMove(Move move) {
        ((BoardOverlay) layerUI).drawMove(move);
    }

    private void enableMovesLog(boolean bool) {
        movesLog.setVisible(bool);
    }


    public void boardButtonPressed(Location to) {
        controller.boardButtonPressed(to);
    }


    public void setStatusLbl(String str) {
        statusLbl.setText(str);
    }


    public void updateMoveLog(String move) {
        movesLog.setText(movesLog.getText() + " " + move);
    }

    public Location getBtnLoc(Point point) {
        int mx = point.x;
        int my = point.y;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton btn = getBtn(new Location(i, j));
                if (btn.getBounds().contains(mx, my))
                    return new Location(i, j);
            }
        }
        return null;
    }

    public JButton getBtn(Point point) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton btn = getBtn(new Location(i, j));
                if (btn.getBounds().contains(point.x, point.y))
                    return btn;
            }
        }
        return null;
    }

    public Location getBtnLoc(JButton source) {
        return ((BoardButton) source).getBtnLoc();
    }
//
//    public void refreshIconSizes() {
//        for (int i = 0; i < ROWS; i++) {
//            for (int j = 0; j < COLS; j++) {
//                JButton btn = getBtn(new Location(i, j));
//                if (btn == null) continue;
//                ImageIcon image = (ImageIcon) btn.getIcon();
//                if (image == null) continue;
//                image = scaleImage(image);
//                btn.setIcon(image);
//                btn.setDisabledIcon(image);
//
//            }
//        }
//        setCoordinates();
//    }

    public void resetBackground() {
        for (BoardButton[] row : btnMat) {
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

    public void enableSquare(Location loc, boolean bool) {
        getBtn(loc).setEnabled(bool);
    }

    public void enableAllSquares(boolean bool) {
        for (JButton[] row : btnMat) {
            for (JButton btn : row) {
                btn.setEnabled(bool);
            }
        }
    }

    public JButton getBtn(Location loc) {
        return btnMat[loc.getRow()][loc.getCol()];
    }

    public void updateBoardButton(Location prevLoc, Location newLoc) {
        JButton prevBtn = getBtn(prevLoc);
        JButton newBtn = getBtn(newLoc);
        newBtn.setIcon(prevBtn.getIcon());
        newBtn.setDisabledIcon(prevBtn.getIcon());
        prevBtn.setIcon(null);
        prevBtn.setDisabledIcon(null);
    }

    public void highlightPath(ArrayList<Move> movableSquares) {
        enableAllSquares(false);
        if (movableSquares != null)
            for (Move move : movableSquares) {
                Location movingTo = move.getMovingTo();
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
        if (list != null)
            for (Location loc : list) {
                enableSquare(loc, true);
            }
    }

    public void gameOver() {
        enableAllSquares(false);
    }

    public void showMessage(String message, String title, ImageIcon icon) {
        Dialogs messageDialog = new Dialogs(DialogTypes.MESSAGE, title);
        ArrayList<DialogObject> object = new ArrayList<>();
        object.add(new DialogObject(message, icon));
        messageDialog.run(object);
    }

    public void inCheck(Location kingLoc) {
        getBtn(kingLoc).setBackground(checkColor);
    }

    public void highLightButton(JButton btn) {
        ((BoardButton) btn).toggleSelected();
    }

    public void resetSelectedButtons() {
        for (BoardButton[] row : btnMat) {
            for (BoardButton btn : row) {
                btn.setIsSelected(false);
            }
        }
    }


    public JButton getBtn() {
        return btnMat[0][0];
    }

    public void colorCurrentPiece(Location loc) {
        getBtn(loc).setBackground(currentBtnColor);
    }

    public void drawArrow(Location from, Location to, Color clr) {
        ((BoardOverlay) layerUI).drawArrow(from, to, clr);
    }
}
