package OLD_ver14_pieceLocation;


import OLD_ver14_pieceLocation.types.Piece.Player;
import OLD_ver14_pieceLocation.types.Piece.types;
import OLD_ver14_pieceLocation.moves.Move;
import OLD_ver14_pieceLocation.moves.PromotionMove;
import OLD_ver14_pieceLocation.types.Piece;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.LayerUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class View {
    private final int ROWS;
    private final int COLS;

    private final String WON_BY_MATE = "Won By Checkmate", WON_ON_TIME = "Won On Time", STALEMATE = "Tie By Stalemate", TIE_BY_INSUFFICIENT_MATERIAL = "Tie By Insufficient Material", TIE_BY_TIME_OUT_VS_INSUFFICIENT_MATERIAL = "Time Out vs Insufficient Material", TIE_BY_REPETITION = "Tie By Repetition", TIE_BY_FIFTY_MOVES_RULE = "Tie By 50 Moves Rule";

    private final Font logFont = new Font(null, 1, 25), menuItemsFont = new Font(null, 1, 20);
    private final Font messagesFont = new Font(null, 1, 30);
    private final Font coordinatesFont = new Font(null, 1, 30);
    public JPanel boardPnl, colsCoordinatesPnl, rowsCoordinatesPnl;
    public ImageIcon wp, wn, wb, wr, wk, wq, bp, bn, bb, br, bk, bq;
    private JMenuBar menuBar;
    private Dimension winSize;
    private JPanel boardContainerPnl, topPnl, leftPnl, bottomPnl;
    private JTextField moveTextField;
    private ImageIcon whiteWonIcon, blackWonIcon, tieIcon;
    private JLabel scanDepthSliderLabel;
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

    public void showOnScreen(int screen, JFrame frame) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        if (screen > -1 && screen < gd.length) {
            frame.setAlwaysOnTop(true);
            frame.setSize(gd[screen].getDefaultConfiguration().getBounds().getSize());
            frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().getLocation());
            //frame.setLocation(gd[screen].setFullScreenWindow(frame).getBounds().x, frame.getY());
        } else if (gd.length > 0) {
            frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY());
        } else {
            throw new RuntimeException("No Screens Found");
        }
        frame.setVisible(true);
    }

    public void initGame(Board board) {
        ((BoardOverlay) layerUI).clearAllArrows();
        resetTable();
        setPieces(board);
    }

    public void setPieces(Board board) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                btnMat[i][j].setIcon(null);
                btnMat[i][j].setDisabledIcon(null);
            }
        }

        resetBackground();
        for (Piece[] row : board) {
            for (Piece piece : row) {
                if (piece != null) {
                    Location loc = piece.getLoc();
                    int r = loc.getRow(), c = loc.getCol();
                    if (piece.getPieceType() == types.KNIGHT) {
                        if (piece.isWhite())
                            btnMat[r][c].setIcon(wn);
                        else btnMat[r][c].setIcon(bn);
                    } else if (piece.getPieceType() == types.BISHOP) {
                        if (piece.isWhite())
                            btnMat[r][c].setIcon(wb);
                        else btnMat[r][c].setIcon(bb);
                    } else if (piece.getPieceType() == types.ROOK) {
                        if (piece.isWhite())
                            btnMat[r][c].setIcon(wr);
                        else btnMat[r][c].setIcon(br);
                    } else if (piece.getPieceType() == types.KING) {
                        if (piece.isWhite())
                            btnMat[r][c].setIcon(wk);
                        else btnMat[r][c].setIcon(bk);
                    } else if (piece.getPieceType() == types.QUEEN) {
                        if (piece.isWhite())
                            btnMat[r][c].setIcon(wq);
                        else btnMat[r][c].setIcon(bq);
                    } else {
                        if (piece.isWhite())
                            btnMat[r][c].setIcon(wp);
                        else btnMat[r][c].setIcon(bp);
                    }
                    btnMat[r][c].setDisabledIcon(btnMat[r][c].getIcon());
                }
            }
        }
        refreshIconSizes();
    }

    private void loadAllIcons() {

        wp = loadImage("White/Pawn");
        wn = loadImage("White/Knight");
        wb = loadImage("White/Bishop");
        wr = loadImage("White/Rook");
        wk = loadImage("White/King");
        wq = loadImage("White/Queen");

        bp = loadImage("Black/Pawn");
        bn = loadImage("Black/Knight");
        bb = loadImage("Black/Bishop");
        br = loadImage("Black/Rook");
        bk = loadImage("Black/King");
        bq = loadImage("Black/Queen");

        tieIcon = loadImage("Tie");
        whiteWonIcon = loadImage("White/King");
        blackWonIcon = loadImage("Black/King");
    }

    private void setCoordinates() {
        colsCoordinatesPnl = new JPanel();
        rowsCoordinatesPnl = new JPanel();
        colsCoordinatesPnl.setLayout(new GridLayout(1, 8));
        rowsCoordinatesPnl.setLayout(new GridLayout(8, 1));

        for (int i = 0; i < 8; i++) {
            JLabel col = new JLabel(Character.toString((char) (i + 'a')) + "");
            JLabel row = new JLabel(((i - 8) * -1) + "");
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
        loadAllIcons();
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

        JCheckBoxMenuItem toggleMovesLog = new JCheckBoxMenuItem("Moves Log");
        toggleMovesLog.setFont(menuItemsFont);
        toggleMovesLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableMovesLog(toggleMovesLog.isSelected());
            }
        });
        toggleMovesLog.setState(true);
        enableMovesLog(toggleMovesLog.isSelected());
        settingsMenu.add(toggleMovesLog);

        JCheckBoxMenuItem toggleBoardOrientation = new JCheckBoxMenuItem("Flip board");
        toggleBoardOrientation.setFont(menuItemsFont);
        toggleBoardOrientation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flipBoard();
            }
        });
        settingsMenu.add(toggleBoardOrientation);

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

        //showOnScreen(0, win);
        win.setVisible(true);

    }

    private void createDebugMenu(JMenu settingsMenu) {
        JMenu debugMenu = new JMenu("Debug");
        Font debugItemsFont = new Font(null, 1, 10);
        JSlider scanDepthSlider = new JSlider(JSlider.HORIZONTAL, controller.MIN_SCAN_DEPTH, controller.MAX_SCAN_DEPTH, controller.SCAN_INIT_VALUE);
        scanDepthSliderLabel = new JLabel(controller.SCAN_INIT_VALUE + "", JLabel.CENTER);
        scanDepthSliderLabel.setFont(debugItemsFont);
        ChangeListener cl = e -> {
            JSlider x = (JSlider) e.getSource();
            scanDepthSliderLabel.setText(x.getValue() + "");
            controller.setScanDepth(x.getValue());
        };
        scanDepthSlider.setFont(debugItemsFont);
        scanDepthSlider.addChangeListener(cl);
        scanDepthSlider.setMajorTickSpacing(1);
        scanDepthSlider.setPaintLabels(true);
        scanDepthSlider.setPaintTicks(true);
        debugMenu.add(new JLabel("Scan Depth:"));
        debugMenu.add(scanDepthSliderLabel);
        debugMenu.add(scanDepthSlider);

        JButton printBoardBtn = new JButton("Print Board");
        printBoardBtn.setFont(debugItemsFont);
        printBoardBtn.setFocusable(false);
        printBoardBtn.addActionListener(e -> controller.printBoard());
        debugMenu.add(printBoardBtn);

        JButton printFEN = new JButton("Print Current Board FEN");
        printFEN.setFont(debugItemsFont);
        printFEN.setFocusable(false);
        printFEN.addActionListener(e -> controller.printFEN());
        debugMenu.add(printFEN);

        JButton printAllPieces = new JButton("Print All Pieces");
        printAllPieces.setFont(debugItemsFont);
        printAllPieces.setFocusable(false);
        printAllPieces.addActionListener(e -> controller.printAllPieces());
        debugMenu.add(printAllPieces);

        settingsMenu.add(debugMenu);

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

    private void flipBoard() {
        isBoardFlipped = !isBoardFlipped;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Location newLoc = new Location(i, j, true);
                isBoardFlipped = !isBoardFlipped;
                Location oldLoc = new Location(i, j, true);
                isBoardFlipped = !isBoardFlipped;
                JButton oldBtn = getBtn(oldLoc);
                JButton newBtn = getBtn(newLoc);
                ImageIcon newIcon = (ImageIcon) oldBtn.getIcon();
                ImageIcon oldIcon = (ImageIcon) newBtn.getIcon();
                oldBtn.setIcon(oldIcon);
                oldBtn.setDisabledIcon(oldIcon);
                newBtn.setIcon(newIcon);
                newBtn.setDisabledIcon(newIcon);
            }
        }
        return;
    }

    private void enableMovesLog(boolean bool) {
        table.setVisible(bool);
    }

    public ImageIcon loadImage(String fileName) {
        ImageIcon ret = new ImageIcon(View.class.getResource("/Assets/" + fileName + ".png"));
        ret = scaleImage(ret);

        return ret;
    }

    public void boardButtonPressed(Location to) {
        controller.boardButtonPressed(to);
    }

    public ImageIcon scaleImage(ImageIcon img) {
        return new ImageIcon(img.getImage().getScaledInstance((int) (getButtonSize() * btnIconRatio), (int) (getButtonSize() * btnIconRatio), Image.SCALE_SMOOTH));
    }

    public void setLbl(String str) {
        statusLbl.setText(str);
    }

    private int getButtonSize() {
        return btnDimension.height;
    }

    public void updateMoveLog(Move move) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int row = model.getRowCount() - 1, col = 1;
        if (controller.getCurrentPlayer() != Player.WHITE) {
            col = 2;

        } else {
            model.addRow(new Object[]{controller.getNumOfMoves() + ".", "", ""});
            row++;
        }
        model.setValueAt(move.getAnnotation(), row, col);
        updateRowDimensions();
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

    public void refreshIconSizes() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton btn = getBtn(new Location(i, j));
                if (btn == null) continue;
                ImageIcon image = (ImageIcon) btn.getIcon();
                if (image == null) continue;
                image = scaleImage(image);
                btn.setIcon(image);
                btn.setDisabledIcon(image);

            }
        }
        setCoordinates();
    }

    void resetBackground() {
        for (BoardButton[] row : btnMat) {
            for (BoardButton btn : row) {
                btn.resetButton();
            }
        }
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
                highlightSquare(loc, Color.green);
            }
    }

    public void gameOver() {
        enableAllSquares(false);
    }

    private void showMessage(String message, String title, ImageIcon icon) {
        Dialogs messageDialog = new Dialogs(DialogTypes.MESSAGE, title);
        ArrayList<DialogObject> object = new ArrayList<>();
        object.add(new DialogObject(message, icon));
        messageDialog.run(object);
    }

    public void inCheck(Location kingLoc) {
        getBtn(kingLoc).setBackground(checkColor);
    }

    private ImageIcon getWinningIcon(Player player) {
        if (player == Player.WHITE)
            return whiteWonIcon;
        return blackWonIcon;
    }

    public void wonByCheckmate(Player currentPlayer) {
        String player = currentPlayer.name();
        showMessage(player + " " + WON_BY_MATE, player + " Won", getWinningIcon(currentPlayer));
    }

    public void tieByInsufficientMaterial() {
        showMessage(TIE_BY_INSUFFICIENT_MATERIAL, "Its a Tie", tieIcon);
    }

    public void wonByOpponentTimedOut(Player currentPlayer) {
        String player = currentPlayer.name();
        showMessage(player + " " + WON_ON_TIME, player + " Won", getWinningIcon(currentPlayer));
    }

    public void tieByTimeOutVsInsufficientMaterial() {
        showMessage(TIE_BY_TIME_OUT_VS_INSUFFICIENT_MATERIAL, "Its a Tie", tieIcon);
    }

    public void tieByStalemate(Player currentPlayer) {
        showMessage(STALEMATE, "Its a Tie", tieIcon);
    }

    public void colorCurrentPiece(Location loc) {
        highlightSquare(loc, currentBtnColor);
    }

    public void tieByRepetition() {
        showMessage(TIE_BY_REPETITION, "Its a Tie", tieIcon);
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


    public void tieByFiftyMoves() {
        showMessage(TIE_BY_FIFTY_MOVES_RULE, "Its a Tie", tieIcon);
    }
}
