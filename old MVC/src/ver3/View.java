package ver3;


import ver3.types.Path;
import ver3.types.Piece;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class View {
    private final int ROWS;
    private final int COLS;
    private final Font logFont = new Font(null, 1, 25);
    private final String WON_BY_MATE = "Won By Checkmate", WON_ON_TIME = "Won On Time", STALEMATE = "Tie By Stalemate", TIE_BY_INSUFFICIENT_MATERIAL = "Tie By Insufficient Material", TIE_BY_TIME_OUT_VS_INSUFFICIENT_MATERIAL = "Time Out vs Insufficient Material";
    private final Font messagesFont = new Font(null, 1, 30);
    public PromotingDialog promotingDialog;
    public int offset = 25;
    Color checkColor = new Color(186, 11, 11, 255);
    boolean isBlack = true;
    Color brown = new Color(79, 60, 33, 255);
    Color white = new Color(222, 213, 187);
    Color red = Color.red, yellow = Color.yellow, blue = Color.BLUE;
    private JButton[][] btnMat;
    private Controller controller;
    private JPanel boardContainerPnl, topPnl, leftPnl, bottomPnl, moveLogPnl;
    private JPanel boardPnl, colsCoordinatesPnl, rowsCoordinatesPnl;
    private JLabel statusLbl;
    private JFrameResizing win;
    private JDialog messageDialog;
    private ImageIcon wp, wn, wb, wr, wk, wq, bp, bn, bb, br, bk, bq;
    private ImageIcon whiteWonIcon, blackWonIcon, tieIcon;
    private ImageIcon[] rowsNums, colsChars;
    private Dimension btnDimension = new Dimension(100, 100);
    private GroupLayout layout;
    private float btnIconRatio = 0.7f;

    public View(Controller controller, int boardSize) {
        ROWS = COLS = boardSize;
        this.controller = controller;
        createGui();
    }

    public void initGame(Piece[][] pieces) {

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
                    switch (piece.getType()) {
                        case (Piece.KNIGHT):
                            if (piece.isWhite())
                                btnMat[r][l].setIcon(wn);
                            else btnMat[r][l].setIcon(bn);
                            break;
                        case (Piece.BISHOP):
                            if (piece.isWhite())
                                btnMat[r][l].setIcon(wb);
                            else btnMat[r][l].setIcon(bb);
                            break;
                        case (Piece.ROOK):
                            if (piece.isWhite())
                                btnMat[r][l].setIcon(wr);
                            else btnMat[r][l].setIcon(br);
                            break;
                        case (Piece.KING):
                            if (piece.isWhite())
                                btnMat[r][l].setIcon(wk);
                            else btnMat[r][l].setIcon(bk);
                            break;
                        case (Piece.QUEEN):
                            if (piece.isWhite())
                                btnMat[r][l].setIcon(wq);
                            else btnMat[r][l].setIcon(bq);
                            break;
                        default:
                            if (piece.isWhite())
                                btnMat[r][l].setIcon(wp);
                            else btnMat[r][l].setIcon(bp);
                            break;

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

            col.setSize(btnDimension);
            row.setSize(btnDimension);
            row.setMaximumSize(btnDimension);
            col.setMaximumSize(btnDimension);
            row.setPreferredSize(btnDimension);
            col.setPreferredSize(btnDimension);
            row.setMinimumSize(btnDimension);
            col.setMinimumSize(btnDimension);

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
//    private Positions choosePos()
//    {
//        messageDialog = new JDialog();
//        messageDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        JPanel topPanel = new JPanel(), bottomPanel = new JPanel();
//
//        JLabel iconLbl = new JLabel();
//        iconLbl.setFont(messagesFont);
//        iconLbl.setIcon(icon);
//        topPanel.add(iconLbl);
//        JLabel msgLbl = new JLabel(message);
//        msgLbl.setFont(messagesFont);
//        topPanel.add(msgLbl);
//
//        JButton okBtn = new JButton("OK");
//        okBtn.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                messageDialog.dispose();
//            }
//        });
//        okBtn.setFocusable(false);
//
//        bottomPanel.add(okBtn);
//
//        messageDialog.add(topPanel, BorderLayout.NORTH);
//        messageDialog.add(bottomPanel, BorderLayout.SOUTH);
//        messageDialog.pack();
//        messageDialog.setVisible(true);
//    }

    public void createGui() {
        loadAllIcons();

        boardContainerPnl = new JPanel();
        boardPnl = new JPanel();
        setCoordinates();

        layout = new GroupLayout(boardContainerPnl);
        boardContainerPnl.setLayout(layout);

        topPnl = new JPanel();
        leftPnl = new JPanel();
        bottomPnl = new JPanel();
        moveLogPnl = new JPanel();

        statusLbl = new JLabel("White to move");
        statusLbl.setFont(messagesFont);
        bottomPnl.add(statusLbl);

        moveLogPnl.setLayout(new GridLayout(1, 2));
        moveLogPnl.setPreferredSize(new Dimension(333, 1000));
        leftPnl.add(moveLogPnl);

        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setSize(1000, 20);
        newGameBtn.setFont(new Font(null, 1, 30));
        newGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.newGameBtnPressed();
            }
        });
        topPnl.add(newGameBtn);
        btnMat = new JButton[ROWS][COLS];
        boardPnl.setLayout(new GridLayout(ROWS, COLS));
        for (int i = 0; i < ROWS; i++) {
            isBlack = !isBlack;
            for (int j = 0; j < COLS; j++) {
                btnMat[i][j] = new JButton();
                JButton btn = btnMat[i][j];

                btn.setFont(new Font(null, 1, 50));
                btn.setActionCommand(i + "" + j);
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Location loc = new Location(e.getActionCommand().charAt(0) - '0', e.getActionCommand().charAt(1) - '0');
                        controller.boardButtonPressed(loc);
                    }
                });
                btn.setFocusable(false);

                boardPnl.add(btn);
            }
        }
        resetBackground();
        layout.setHonorsVisibility(false);
        layout.setAutoCreateGaps(false);
        layout.setAutoCreateContainerGaps(false);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addComponent(rowsCoordinatesPnl)
//                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
//                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(colsCoordinatesPnl)
                                .addComponent(boardPnl))
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(colsCoordinatesPnl)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)

                                .addComponent(rowsCoordinatesPnl)
                                .addComponent(boardPnl))

        );
        //LayerUI<JPanel> layerUI = new MyLayerUISubclass();
        //JLayer<JPanel> jlayer = new JLayer<JPanel>(middle, layerUI);
        win = new JFrameResizing(this);
        promotingDialog = new PromotingDialog(win);
        promotingDialog.setSize(1000, 300);
        win.setSize(1000, 1000);
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.setLocationRelativeTo(null);
        win.add(boardContainerPnl, BorderLayout.CENTER);

        win.add(topPnl, BorderLayout.NORTH);
        //leftPnl.setSize(boardPnl.getSize().width / 3, boardPnl.getSize().height / 3);
        win.add(leftPnl, BorderLayout.WEST);
        win.add(bottomPnl, BorderLayout.SOUTH);
        win.setVisible(true);

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

    public void updateMoveLog(String move) {
        JLabel lbl = new JLabel(move + " ");
        lbl.setFont(logFont);
        moveLogPnl.add(lbl);
        System.out.println(moveLogPnl.getSize());

    }

    public int[] getBtnLoc(int mx, int my) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton btn = btnMat[i][j];
                if (btn.getBounds().contains(mx, my))
                    return new int[]{btn.getX(), btn.getY(), btn.getWidth(), btn.getHeight()};
            }
        }
        return null;
    }

    public void refreshIconSizes() {
        boardPnl.setPreferredSize(boardPnl.getPreferredSize());
        Dimension dimension = new Dimension(boardPnl.getWidth() / 8, boardPnl.getHeight() / 8);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton btn = btnMat[i][j];
                btn.setSize(dimension);
                if (btn == null) continue;
                ImageIcon image = (ImageIcon) btn.getIcon();
                if (image == null) continue;
                image = scaleImage(image);
                btn.setIcon(image);
                btn.setDisabledIcon(image);

            }
        }
        btnDimension = dimension;

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

    public int[][] getCharsLoc() {
        int arr[][] = new int[COLS][2];
        for (int i = 0; i < COLS; i++) {
            arr[i][0] = btnMat[0][i].getX();
            arr[i][1] = offset;
        }
        return arr;
    }

    public void updateBoardButton(Location prevLoc, Location newLoc) {
        JButton prevBtn = btnMat[prevLoc.getRow()][prevLoc.getCol()];
        JButton newBtn = btnMat[newLoc.getRow()][newLoc.getCol()];
        newBtn.setIcon(prevBtn.getIcon());
        newBtn.setDisabledIcon(prevBtn.getIcon());
        prevBtn.setIcon(null);
        prevBtn.setDisabledIcon(null);
    }

    public void highlightPath(ArrayList<Path> movableSquares) {

        enableAllSquares(false);
        if (movableSquares != null)
            for (Path path : movableSquares) {
                if (path.isCapturing())
                    highlightSquare(path, red);
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

    private String getPlayerColor(int player) {
        return Piece.colorAStringArr[player];
    }

    private ImageIcon getWinningIcon(int player) {
        if (player == Piece.WHITE)
            return whiteWonIcon;
        return blackWonIcon;
    }

    public void wonByCheckmate(int currentPlayer) {
        String player = getPlayerColor(currentPlayer);
        showMessage(player + " " + WON_BY_MATE, player + " Won", getWinningIcon(currentPlayer));
    }

    public void tieByInsufficientMaterial() {
        showMessage(TIE_BY_INSUFFICIENT_MATERIAL, "Its a Tie", tieIcon);
    }

    public void wonByOpponentTimedOut(int currentPlayer) {
        String player = getPlayerColor(currentPlayer);
        showMessage(player + " " + WON_ON_TIME, player + " Won", getWinningIcon(currentPlayer));
    }

    public void tieByTimeOutVsInsufficientMaterial() {
        showMessage(TIE_BY_TIME_OUT_VS_INSUFFICIENT_MATERIAL, "Its a Tie", tieIcon);
    }

    public void tieByStalemate(int currentPlayer) {
        showMessage(STALEMATE, "Its a Tie", tieIcon);
    }
}
