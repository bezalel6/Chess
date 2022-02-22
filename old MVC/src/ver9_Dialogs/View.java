package ver9_Dialogs;


import ver9_Dialogs.types.Piece.colors;
import ver9_Dialogs.types.Piece.types;
import ver9_Dialogs.types.Piece;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.LayerUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class JFrameResizing extends JFrame implements ComponentListener {
    private View view;

    JFrameResizing(View view) {
        getContentPane().addComponentListener(this);
        this.view = view;
    }

    public void componentHidden(ComponentEvent ce) {
    }

    ;

    public void componentShown(ComponentEvent ce) {
    }

    ;

    public void componentMoved(ComponentEvent ce) {
    }

    ;

    public void componentResized(ComponentEvent ce) {
        view.refreshIconSizes();

    }

    ;
}

public class View {
    private final int ROWS;
    private final int COLS;
    private final String WON_BY_MATE = "Won By Checkmate", WON_ON_TIME = "Won On Time", STALEMATE = "Tie By Stalemate", TIE_BY_INSUFFICIENT_MATERIAL = "Tie By Insufficient Material", TIE_BY_TIME_OUT_VS_INSUFFICIENT_MATERIAL = "Time Out vs Insufficient Material", TIE_BY_REPETITION = "Tie By Repetition";
    public JPanel boardPnl, colsCoordinatesPnl, rowsCoordinatesPnl;
    public ImageIcon wp, wn, wb, wr, wk, wq, bp, bn, bb, br, bk, bq;
    Color checkColor = new Color(186, 11, 11, 255);
    boolean isBlack = true;
    Color brown = new Color(79, 60, 33, 255);
    Color white = new Color(222, 213, 187);
    Color red = Color.red, yellow = Color.yellow, blue = Color.BLUE;
    Color currentBtnColor = Color.BLUE;
    private int fontBaseSize = 10;
    private final Font logFont = new Font(null, 1, 25 + fontBaseSize), menuItemsFont = new Font(null, 1, 20 + fontBaseSize);
    private final Font messagesFont = new Font(null, 1, 30 + fontBaseSize);
    Font coordinatesFont = new Font(null, 1, 30 + fontBaseSize);
    private JButton[][] btnMat;
    private Controller controller;
    private JPanel boardContainerPnl, topPnl, leftPnl, bottomPnl;
    private JPanel moveLogPnl;
    private JLabel statusLbl;
    private JFrameResizing win;
    private JDialog messageDialog;
    private ImageIcon whiteWonIcon, blackWonIcon, tieIcon;
    private Dimension btnDimension = new Dimension(100, 100);
    private JTable table;
    private LayerUI<JPanel> layerUI;
    private float btnIconRatio = 0.7f;
    private boolean isDragging = false, isDrawing = false;
    private Color promotingSquareColor = new Color(151, 109, 3);
    private boolean isBoardFlipped = true;

    public View(Controller controller, int boardSize) {
        ROWS = COLS = boardSize;
        this.controller = controller;
        createGui();
    }

    public void initGame(Piece[][] pieces) {
        isDragging = isDrawing = false;
        ((BoardOverlay) layerUI).clearAllArrows();
        resetTable();
        setPieces(pieces);
    }

    public void setPieces(Piece[][] pieces) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                btnMat[i][j].setIcon(null);
                btnMat[i][j].setDisabledIcon(null);
            }
        }

        resetBackground();
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null) {
                    Location loc = piece.getLoc().getViewLocation();
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
            JLabel row = new JLabel((i + 1) + "");
            col.setFont(coordinatesFont);
            row.setFont(coordinatesFont);
            colsCoordinatesPnl.add(col);
            rowsCoordinatesPnl.add(row);
        }
        for (Component comp : colsCoordinatesPnl.getComponents()) {
            comp.setSize(btnDimension);
        }
        for (Component comp : rowsCoordinatesPnl.getComponents()) {
            comp.setSize(btnDimension);
        }

    }

    private void initTable() {
        String[] columnNames = {"Move Num",
                "White Move",
                "Black Move"};
        Object[][] data = {};
        table = new JTable(new DefaultTableModel(data, columnNames));
        table.setFont(logFont);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        resetTable();
    }

    private void resetTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        String[] columnNames = {"Move Num", "White Move", "Black Move"};
        model.addRow(columnNames);
        updateRowHeights();
    }

    public void createGui() {
        initTable();
        loadAllIcons();
        win = new JFrameResizing(this);
        win.setSize(750, 750);
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.setLocationRelativeTo(null);
        win.setLayout(new GridBagLayout());

        boardPnl = new JPanel();
        boardContainerPnl = new JPanel();
        GridBagConstraints gbc;

        setCoordinates();

        topPnl = new JPanel();
        leftPnl = new JPanel();
        bottomPnl = new JPanel();

        JMenuBar menuBar = new JMenuBar();

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

        JSlider scanDepthSlider = new JSlider(JSlider.HORIZONTAL, controller.MIN_SCAN_DEPTH, controller.MAX_SCAN_DEPTH, controller.SCAN_INIT_VALUE);
        ChangeListener cl = e -> {
            JSlider x = (JSlider) e.getSource();
            controller.setScanDepth(x.getValue());
        };
        scanDepthSlider.setFont(menuItemsFont);
        scanDepthSlider.addChangeListener(cl);
        scanDepthSlider.setMajorTickSpacing(1);
        scanDepthSlider.setPaintLabels(true);
        scanDepthSlider.setPaintTicks(true);
        settingsMenu.add(scanDepthSlider);

        menuBar.add(settingsMenu);

        statusLbl = new JLabel("White to move");
        statusLbl.setFont(messagesFont);
        bottomPnl.add(statusLbl);

        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setFont(new Font(null, 1, 30));
        newGameBtn.setFocusable(false);
        newGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((BoardOverlay) layerUI).reset();
                controller.newGameBtnPressed();
            }
        });
        topPnl.add(newGameBtn);

        JButton evalBtn = new JButton("EVAL");
        evalBtn.setFont(new Font(null, 1, 30));
        evalBtn.setFocusable(false);
        evalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.evalBtnPressed();
            }
        });
        topPnl.add(evalBtn);

        JButton aiMoveBtn = new JButton("ai move");
        aiMoveBtn.setFont(new Font(null, 1, 30));
        aiMoveBtn.setFocusable(false);
        aiMoveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.aiMoveButtonPressed();
            }
        });
        topPnl.add(aiMoveBtn);

        btnMat = new JButton[ROWS][COLS];
        boardPnl.setLayout(new GridLayout(ROWS, COLS));
        for (int i = 0; i < ROWS; i++) {
            isBlack = !isBlack;
            for (int j = 0; j < COLS; j++) {
                btnMat[i][j] = new JButton();
                JButton btn = btnMat[i][j];

                btn.setFont(new Font(null, 1, 50));
                btn.setActionCommand(i + "" + j);
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        JButton btn = (JButton) e.getSource();
                        switch (e.getButton()) {
                            case MouseEvent.BUTTON1:
                                deleteAllDrawings();
                                if (btn.isEnabled() && !isDragging) {
                                    Location loc = getBtnLoc(btn);
                                    controller.boardButtonPressed(loc);
                                    startDragging((JButton) e.getSource());
                                }
                                break;
                            case MouseEvent.BUTTON3:
                                startDrawing(btn);
                                break;
                        }


                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        JButton btn = (JButton) e.getSource();
                        switch (e.getButton()) {
                            case MouseEvent.BUTTON1:
                                if (btn.isEnabled()) {
                                    if (!isSameBtn(btn) && isDragging) {
                                        controller.boardButtonPressed(stopDragging(btn));
                                    } else
                                        stopDragging(btn);
                                }
                                break;
                            case MouseEvent.BUTTON3:
                                stopDrawing(btn);
                                break;
                        }
                    }
                });
                btn.setFocusable(false);

                boardPnl.add(btn);
            }
        }
        resetBackground();
        //שורת התפריטים
        gbc = new GridBagConstraints();
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        win.add(menuBar, gbc);
        //שורה עליונה
        gbc = new GridBagConstraints();
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        win.add(topPnl, gbc);
        //תיעוד המשחק
        gbc = new GridBagConstraints();
        gbc.gridwidth = 15;
        gbc.weighty = 4;
        gbc.weightx = 15;
        gbc.fill = GridBagConstraints.BOTH;
        win.add(table, gbc);
        //לוח המשחק

        //הוספת הקורדינאטות
//        boardContainerPnl.add(colsCoordinatesPnl,BorderLayout.NORTH);
//        boardContainerPnl.add(rowsCoordinatesPnl);

        gbc = new GridBagConstraints();
        gbc.weightx = 20;
        gbc.weighty = 4;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        layerUI = new BoardOverlay(this);
        JLayer<JPanel> jlayer = new JLayer<>(boardPnl, layerUI);
        boardContainerPnl.add(jlayer);

        win.add(jlayer, gbc);
        //שורה תחתונה
        gbc = new GridBagConstraints();
        gbc.weightx = 0;
        gbc.weighty = 20;
        gbc.gridheight = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        //win.add(bottomPnl, gbc);

        win.setVisible(true);

    }

    private void deleteAllDrawings() {
        ((BoardOverlay) layerUI).clearAllArrows();
    }

    private void stopDrawing(JButton btn) {
        if (isDrawing) {
            ((BoardOverlay) layerUI).stopDrawing();
            isDrawing = false;
        }
    }

    private void startDrawing(JButton btn) {
        if (!isDrawing) {
            isDrawing = true;
            ((BoardOverlay) layerUI).startDrawing();
        }
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

    /**
     * האם זה אותו כפתור שאני מצביע עליו עם העכבר
     *
     * @param btn
     * @return
     */
    private boolean isSameBtn(JButton btn) {
        return btn.getBounds().contains(((BoardOverlay) layerUI).getPoint());
    }

    private void enableMovesLog(boolean bool) {
        table.setVisible(bool);
    }

    public ImageIcon loadImage(String fileName) {
        ImageIcon ret = new ImageIcon(View.class.getResource("/Assets/" + fileName + ".png"));
        ret = scaleImage(ret);

        return ret;
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

    public void updateMoveLog(String move, int moveNum) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int row = moveNum / 2, col = 1;
        if (moveNum % 2 != 0) {
            model.addRow(new Object[]{moveNum / 2 + 1 + ".", "", ""});
        } else {
            row--;
            col = 2;
        }
        model.setValueAt(move, row + 1, col);
        updateRowHeights();
    }

    private void updateRowHeights() {
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
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton btn = getBtn(new Location(i, j));
                if (btn.getLocation().x == source.getLocation().x && btn.getLocation().y == source.getLocation().y)
                    return new Location(i, j);
            }
        }
        return null;
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
        for (int i = 0, row = 0; row < ROWS; row++, i++) {
            isBlack = !isBlack;
            for (int j = 0, cell = 0; cell < COLS; cell++, j++) {
                btnMat[i][j].setBackground(isBlack ? brown : white);
                isBlack = !isBlack;
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

    private JButton getBtn(Location loc) {
        return btnMat[loc.getViewRow()][loc.getCol()];
    }

    public void updateBoardButton(Location prevLoc, Location newLoc) {
        JButton prevBtn = getBtn(prevLoc);
        JButton newBtn = getBtn(newLoc);
        stopDragging(prevBtn);
        newBtn.setIcon(prevBtn.getIcon());
        newBtn.setDisabledIcon(prevBtn.getIcon());
        prevBtn.setIcon(null);
        prevBtn.setDisabledIcon(null);
    }

    public void updateBoardButtonWithoutStoppingDrag(Location prevLoc, Location newLoc) {
        JButton prevBtn = getBtn(prevLoc);
        JButton newBtn = getBtn(newLoc);
        newBtn.setIcon(prevBtn.getIcon());
        newBtn.setDisabledIcon(prevBtn.getIcon());
        prevBtn.setIcon(null);
        prevBtn.setDisabledIcon(null);
    }

    private void startDragging(JButton source) {
        isDragging = true;
        ((BoardOverlay) layerUI).startDragging((ImageIcon) source.getIcon());
        source.setIcon(null);
    }

    private Location stopDragging(JButton btn) {
        PointAndImageIcon pi = ((BoardOverlay) layerUI).stopDragging();
        isDragging = false;
        btn.setIcon(pi.getIcon());
        ((BoardOverlay) layerUI).reset();
        return getBtnLoc(pi.getPoint());
    }

    public void highlightPath(ArrayList<Move> movableSquares) {
        isDragging = false;
        enableAllSquares(false);
        if (movableSquares != null)
            for (Move move : movableSquares) {
                Location movingTo = move.getMovingTo();
                //movingTo.setRow(getLogicMatRow(movingTo.getRow()));
                if (move.isCapturing())
                    highlightSquare(movingTo, red);
                else if (move.getSpecialMove() == Move.SpecialMoves.PROMOTION)
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

    private void showMessage(String message, String title, ImageIcon icon) {
        Dialogs messageDialog = new Dialogs(DialogTypes.MESSAGE, title);
        ArrayList<DialogObject> object = new ArrayList<>();
        object.add(new DialogObject(message, icon));
        messageDialog.run(object);
    }

    public void inCheck(Location kingLoc) {
        getBtn(kingLoc).setBackground(checkColor);
    }

    private ImageIcon getWinningIcon(colors player) {
        if (player == colors.WHITE)
            return whiteWonIcon;
        return blackWonIcon;
    }

    public void wonByCheckmate(colors currentPlayer) {
        String player = currentPlayer.name();
        showMessage(player + " " + WON_BY_MATE, player + " Won", getWinningIcon(currentPlayer));
    }

    public void tieByInsufficientMaterial() {
        showMessage(TIE_BY_INSUFFICIENT_MATERIAL, "Its a Tie", tieIcon);
    }

    public void wonByOpponentTimedOut(colors currentPlayer) {
        String player = currentPlayer.name();
        showMessage(player + " " + WON_ON_TIME, player + " Won", getWinningIcon(currentPlayer));
    }

    public void tieByTimeOutVsInsufficientMaterial() {
        showMessage(TIE_BY_TIME_OUT_VS_INSUFFICIENT_MATERIAL, "Its a Tie", tieIcon);
    }

    public void tieByStalemate(colors currentPlayer) {
        showMessage(STALEMATE, "Its a Tie", tieIcon);
    }

    public void colorCurrentPiece(Location loc) {
        highlightSquare(loc, currentBtnColor);
    }

    public void tieByRepetition() {
        showMessage(TIE_BY_REPETITION, "Its a Tie", tieIcon);
    }
}
