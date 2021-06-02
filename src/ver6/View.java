package ver6;


import ver6.types.Path;
import ver6.types.Piece;
import ver6.types.Piece.types;
import ver6.types.Piece.colors;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.LayerUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class View {
    private final int ROWS;
    private final int COLS;
    private final Font logFont = new Font(null, 1, 25), menuItemsFont = new Font(null, 1, 20);
    private final String WON_BY_MATE = "Won By Checkmate", WON_ON_TIME = "Won On Time", STALEMATE = "Tie By Stalemate", TIE_BY_INSUFFICIENT_MATERIAL = "Tie By Insufficient Material", TIE_BY_TIME_OUT_VS_INSUFFICIENT_MATERIAL = "Time Out vs Insufficient Material", TIE_BY_REPETITION = "Tie By Repetition";
    private final Font messagesFont = new Font(null, 1, 30);
    public JPanel boardPnl, colsCoordinatesPnl, rowsCoordinatesPnl;
    public PromotingDialog promotingDialog;
    Color checkColor = new Color(186, 11, 11, 255);
    boolean isBlack = true;
    Color brown = new Color(79, 60, 33, 255);
    Color white = new Color(222, 213, 187);
    Color red = Color.red, yellow = Color.yellow, blue = Color.BLUE;
    Color currentBtnColor = Color.BLUE;
    private JButton[][] btnMat;
    private Controller controller;
    private JPanel boardContainerPnl, topPnl, leftPnl, bottomPnl;
    private JPanel moveLogPnl;
    private JLabel statusLbl;
    private JFrameResizing win;
    private JDialog messageDialog;
    private ImageIcon wp, wn, wb, wr, wk, wq, bp, bn, bb, br, bk, bq;
    private ImageIcon whiteWonIcon, blackWonIcon, tieIcon;
    private ImageIcon[] rowsNums, colsChars;
    private Dimension btnDimension = new Dimension(100, 100);
    private JTable table;
    private LayerUI<JPanel> layerUI;
    private boolean isClicking = false;
    private float btnIconRatio = 0.7f;
    private boolean isDragging = false;
    private Color promotingSquareColor = new Color(151, 109, 3);

    public View(Controller controller, int boardSize) {
        ROWS = COLS = boardSize;
        this.controller = controller;
        createGui();
    }

    public void initGame(Piece[][] pieces) {
        isDragging = false;
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
                    Location loc = piece.getLoc();
                    int r = loc.getRow(), l = loc.getCol();
                    if (piece.getPieceType() == types.KNIGHT) {
                        if (piece.isWhite())
                            btnMat[r][l].setIcon(wn);
                        else btnMat[r][l].setIcon(bn);
                    } else if (piece.getPieceType() == types.BISHOP) {
                        if (piece.isWhite())
                            btnMat[r][l].setIcon(wb);
                        else btnMat[r][l].setIcon(bb);
                    } else if (piece.getPieceType() == types.ROOK) {
                        if (piece.isWhite())
                            btnMat[r][l].setIcon(wr);
                        else btnMat[r][l].setIcon(br);
                    } else if (piece.getPieceType() == types.KING) {
                        if (piece.isWhite())
                            btnMat[r][l].setIcon(wk);
                        else btnMat[r][l].setIcon(bk);
                    } else if (piece.getPieceType() == types.QUEEN) {
                        if (piece.isWhite())
                            btnMat[r][l].setIcon(wq);
                        else btnMat[r][l].setIcon(bq);
                    } else {
                        if (piece.isWhite())
                            btnMat[r][l].setIcon(wp);
                        else btnMat[r][l].setIcon(bp);
                    }
                    btnMat[r][l].setDisabledIcon(btnMat[r][l].getIcon());
                }
            }
        }
        refreshIconSizes();
    }

    private void loadAllIcons() {
        rowsNums = new ImageIcon[ROWS];
        colsChars = new ImageIcon[COLS];
        for (int i = 0; i < ROWS; i++) {
            rowsNums[i] = loadImage("Coordinates/Rows/" + (i + 1));
        }
        char c = 'a';
        for (int i = 0; i < COLS; i++) {
            colsChars[i] = loadImage("Coordinates/Cols/" + c);
            c++;
        }

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
            JLabel col = new JLabel(colsChars[i]);
            JLabel row = new JLabel(rowsNums[i]);
            Border border = BorderFactory.createLineBorder(Color.BLUE, 5);
            col.setBorder(border);
            row.setBorder(border);

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
        win.setSize(1000, 1000);
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.setLocationRelativeTo(null);
        win.setLayout(new GridBagLayout());

        boardPnl = new JPanel();
        boardContainerPnl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

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
                ((MyLayerUISubclass) layerUI).reset();
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
                        if (btn.isEnabled() && !isDragging) {
                            Location loc = getBtnLoc(btn);
                            controller.boardButtonPressed(loc);

                            startDragging((JButton) e.getSource());
                        }

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        JButton btn = (JButton) e.getSource();
                        if (btn.isEnabled()) {
                            if (!isSameBtn(btn) && isDragging) {
                                controller.boardButtonPressed(stopDragging(btn));
                            } else
                                stopDragging(btn);
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
        gbc = new GridBagConstraints();
        gbc.weightx = 20;
        gbc.weighty = 4;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        layerUI = new MyLayerUISubclass(this);
        JLayer<JPanel> jlayer = new JLayer<>(boardPnl, layerUI);
        win.add(jlayer, gbc);
        //הוספת הקורדינאטות
        gbc = new GridBagConstraints();
        /*boardContainerPnl.add(colsCoordinatesPnl);
        boardContainerPnl.add(rowsCoordinatesPnl);
        win.add(boardContainerPnl);*/
        //שורה תחתונה
        gbc = new GridBagConstraints();
        gbc.weightx = 0;
        gbc.weighty = 20;
        gbc.gridheight = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        //win.add(bottomPnl, gbc);

        promotingDialog = new PromotingDialog(win);
        promotingDialog.setSize(1000, 300);
        win.setVisible(true);

    }

    /**
     * האם זה אותו כפתור שאני מצביע עליו עם העכבר
     *
     * @param btn
     * @return
     */
    private boolean isSameBtn(JButton btn) {
        return btn.getBounds().contains(((MyLayerUISubclass) layerUI).getPoint());
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

    public Location getBtnLoc(int mx, int my) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton btn = btnMat[i][j];
                if (btn.getBounds().contains(mx, my))
                    return new Location(i, j);
            }
        }
        return null;
    }

    public Location getBtnLoc(JButton source) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton btn = btnMat[i][j];
                if (btn.getLocation().x == source.getLocation().x && btn.getLocation().y == source.getLocation().y)
                    return new Location(i, j);
            }
        }
        return null;
    }

    public void refreshIconSizes() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton btn = btnMat[i][j];
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
                if (isBlack) btnMat[i][j].setBackground(brown);
                else btnMat[i][j].setBackground(white);
                isBlack = !isBlack;
            }
        }

    }

    public void highlightSquare(Path p, Color color) {
        btnMat[p.getLoc().getRow()][p.getLoc().getCol()].setBackground(color);
    }

    public void enableSquare(Location loc, boolean bool) {
        btnMat[loc.getRow()][loc.getCol()].setEnabled(bool);
    }

    public void enableAllSquares(boolean bool) {
        for (JButton[] row : btnMat) {
            for (JButton btn : row) {
                btn.setEnabled(bool);
            }
        }
    }

    public void updateBoardButton(Location prevLoc, Location newLoc) {
        JButton prevBtn = btnMat[prevLoc.getRow()][prevLoc.getCol()];
        JButton newBtn = btnMat[newLoc.getRow()][newLoc.getCol()];
        stopDragging(prevBtn);
        newBtn.setIcon(prevBtn.getIcon());
        newBtn.setDisabledIcon(prevBtn.getIcon());
        prevBtn.setIcon(null);
        prevBtn.setDisabledIcon(null);
    }

    public void updateBoardButtonWithoutStoppingDrag(Location prevLoc, Location newLoc) {
        JButton prevBtn = btnMat[prevLoc.getRow()][prevLoc.getCol()];
        JButton newBtn = btnMat[newLoc.getRow()][newLoc.getCol()];
        newBtn.setIcon(prevBtn.getIcon());
        newBtn.setDisabledIcon(prevBtn.getIcon());
        prevBtn.setIcon(null);
        prevBtn.setDisabledIcon(null);
    }

    private void startDragging(JButton source) {
        isDragging = true;
        ((MyLayerUISubclass) layerUI).startDragging((ImageIcon) source.getIcon());
        source.setIcon(null);
    }

    private Location stopDragging(JButton btn) {
        PointAndImageIcon pi = ((MyLayerUISubclass) layerUI).stopDragging();
        isDragging = false;
        btn.setIcon(pi.getIcon());
        ((MyLayerUISubclass) layerUI).reset();
        return getBtnLoc(pi.getX(), pi.getY());
    }

    public void highlightPath(ArrayList<Path> movableSquares) {
        isClicking = isDragging = false;
        enableAllSquares(false);
        if (movableSquares != null)
            for (Path path : movableSquares) {
                if (path.isCapturing())
                    highlightSquare(path, red);
                else if (path.isPromoting())
                    highlightSquare(path, promotingSquareColor);
                else
                    highlightSquare(path, yellow);

                enableSquare(path.getLoc(), true);
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
        messageDialog = new JDialog();
        messageDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel(), bottomPanel = new JPanel();

        JLabel iconLbl = new JLabel();
        iconLbl.setFont(messagesFont);
        iconLbl.setIcon(icon);
        topPanel.add(iconLbl);
        JLabel msgLbl = new JLabel(message);
        msgLbl.setFont(messagesFont);
        topPanel.add(msgLbl);

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messageDialog.dispose();
            }
        });
        okBtn.setFocusable(false);

        bottomPanel.add(okBtn);

        messageDialog.add(topPanel, BorderLayout.NORTH);
        messageDialog.add(bottomPanel, BorderLayout.SOUTH);
        messageDialog.pack();
        messageDialog.setVisible(true);
    }

    public void inCheck(Location kingLoc) {
        btnMat[kingLoc.getRow()][kingLoc.getCol()].setBackground(checkColor);
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
        highlightSquare(new Path(loc, false), currentBtnColor);
    }

    public void tieByRepetition() {
        showMessage(TIE_BY_REPETITION, "Its a Tie", tieIcon);
    }
}
