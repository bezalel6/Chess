package ver19_square_control.View_classes;


import ver19_square_control.Controller;
import ver19_square_control.Location;
import ver19_square_control.moves.Move;
import ver19_square_control.moves.PromotionMove;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.LayerUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.function.Function;


public class View {
    private final int ROWS;
    private final int COLS;


    private final Font logFont = new Font(null, 1, 25), menuItemsFont = new Font(null, 1, 20);
    private final Font messagesFont = new Font(null, 1, 30);
    private final Font coordinatesFont = new Font(null, 1, 30);
    private JPanel boardPnl, colsCoordinatesPnl, rowsCoordinatesPnl;
    private Font debugItemsFont = new Font(null, 1, 20);
    private JMenuBar menuBar;
    private JMenu settingsMenu, debugMenu;
    private Dimension winSize;
    private JPanel boardContainerPnl, topPnl, leftPnl, bottomPnl;
    private JTextField moveTextField;
    private JLabel scanTimeSliderLabel;
    private JLabel statusLbl;
    private Color checkColor = new Color(186, 11, 11, 255);
    private Color brown = new Color(79, 60, 33, 255);
    private Color white = new Color(222, 213, 187);
    private Color red = Color.red, yellow = Color.yellow;
    private Color currentBtnColor = Color.BLUE;
    private Color promotingSquareColor = new Color(151, 109, 3);
    private Color moveTextFieldBackgroundColor = Color.WHITE;
    private Color moveTextFieldWrongMoveBackgroundColor = new Color(255, 0, 0, 50);
    private boolean isBoardFlipped = true;
    private BoardButton[][] btnMat;
    private Controller controller;
    private JFrameResizing win;
    private Dimension btnDimension = new Dimension(100, 100);
    private JTable table;
    private LayerUI<JPanel> layerUI;
    private float btnIconRatio = 0.7f;
    //private double btnToScreenResolutionRatio =
    private double winToScreenResolutionRatio = 0.8;

    public View(Controller controller, int boardSize) {
        ROWS = COLS = boardSize;
        this.controller = controller;
        initTable();
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
            Dimension d = gd[screen].getDefaultConfiguration().getBounds().getSize();
            d.width = d.height;
            frame.setSize(d);
            int x = gd[screen].getDefaultConfiguration().getBounds().getLocation().x;
            int y = gd[screen].getDefaultConfiguration().getBounds().getLocation().y;
            y += d.width;
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
        resetTable();
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

    private void initTable() {
        String[] columnNames = {"Move Num",
                "White Move",
                "Black Move"};
        Object[][] data = {};
        table = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        table.setFont(logFont);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint()) - 1;
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    controller.gotToMove(row);
                }
            }
        });
        resetTable();
    }

    private void resetTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        String[] columnNames = {"Move Num", "White Move", "Black Move"};
        model.addRow(columnNames);
        updateRowDimensions();
    }

    public void createGui() {
        setRelativeSizes();
        initTable();
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

        settingsMenu = new JMenu("Menu");
        settingsMenu.setFont(menuItemsFont);

        JCheckBoxMenuItem toggleMovesLog = new JCheckBoxMenuItem("Moves Log");
        toggleMovesLog.setFont(menuItemsFont);
        toggleMovesLog.addActionListener(e -> enableMovesLog(toggleMovesLog.isSelected()));
        toggleMovesLog.setState(false);
        enableMovesLog(toggleMovesLog.isSelected());
        settingsMenu.add(toggleMovesLog);

        Font uiBtnsFont = new Font(null, 1, 30);
        //create a debug menu and add it to the settings menu
        createDebugMenu(settingsMenu);

        menuBar.add(settingsMenu);

        statusLbl = new JLabel();
        statusLbl.setFont(messagesFont);
        bottomPnl.add(statusLbl);

        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setFont(uiBtnsFont);
        newGameBtn.setFocusable(false);
        newGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.newGameBtnPressed();
            }
        });
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

                btn.setFont(new Font(null, 1, 50));
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

        JSlider scanTimeSlider = new JSlider(JSlider.HORIZONTAL, controller.MIN_SCAN_TIME, controller.MAX_SCAN_TIME, controller.DEFAULT_SCAN_TIME);
        scanTimeSliderLabel = new JLabel(controller.DEFAULT_SCAN_TIME + "", JLabel.CENTER);
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
            javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();

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
        gbc.weighty = 8;
        gbc.weightx = 5;
        gbc.fill = GridBagConstraints.BOTH;
        win.add(table, gbc);

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
        table.setVisible(bool);
    }


    public void boardButtonPressed(Location to) {
        controller.boardButtonPressed(to);
    }


    public void setLbl(String str) {
        statusLbl.setText(str);
    }

    private int getButtonSize() {
        return btnDimension.height;
    }

    public void updateMoveLog(String move, int moveNum) {
//        DefaultTableModel model = (DefaultTableModel) table.getModel();
//        int row = moveNum / 2, col = 1;
//        if (moveNum % 2 != 0) {
//            model.addRow(new Object[]{moveNum / 2 + 1 + ".", "", ""});
//        } else {
//            row--;
//            col = 2;
//        }
//        model.setValueAt(move, row + 1, col);
//        updateRowDimensions();
    }

    private void updateRowDimensions() {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();

            for (int column = 0; column < table.getColumnCount(); column++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }

            table.setRowHeight(row, rowHeight);
        }
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
