package ver4.view;


import ver4.Client;
import ver4.SharedClasses.Location;
import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.board_setup.Board;
import ver4.SharedClasses.moves.BasicMove;
import ver4.SharedClasses.moves.Move;
import ver4.SharedClasses.ui.ShowOnScreen;
import ver4.SharedClasses.ui.dialogs.MyDialogs;
import ver4.SharedClasses.ui.windows.CloseConfirmationJFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class View implements Iterable<BoardButton[]> {

    private final int ROWS;
    private final int COLS;


    private final Font menuItemsFont = new Font(null, Font.BOLD, 20);
    private final Font messagesFont = new Font(null, Font.BOLD, 30);
    private final Color checkColor = new Color(186, 11, 11, 255);

    private final Color red = Color.red;
    private final Color yellow = Color.yellow;
    private final Color currentBtnColor = Color.BLUE;
    private final Color promotingSquareColor = new Color(151, 109, 3);
    private final Client client;
    private final double winToScreenResolutionRatio = 0.8;
    private BoardPanel boardPnl;
    private SidePanel sidePanel;
    private JMenuBar menuBar;
    private Dimension winSize;
    private JPanel topPnl, bottomPnl;
    private JLabel statusLbl;
    private PlayerColor boardOrientation;
    private CloseConfirmationJFrame win;

    public View(int boardSize, Client client, PlayerColor boardOrientation, long millis) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException e) {
//
//        } catch (UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
//
//        }
        ROWS = COLS = boardSize;
        this.client = client;
        this.boardOrientation = boardOrientation;
        sidePanel = new SidePanel(millis, isBoardFlipped(), client);
        createGui();
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public BoardPanel getBoardPnl() {
        return boardPnl;
    }


    public void initGame(long[] clocks, Board board, PlayerColor playerColor) {
        setBoardOrientation(playerColor);
        sidePanel.reset(clocks);
        boardPnl.getBoardOverlay().clearAllArrows();
        resetAllBtns();
        boardPnl.setBoardButtons(board);

    }

    public void resetAllBtns() {
        boardPnl.resetAllButtons(true);
    }


    public void setBtnIcon(Location loc, ImageIcon icon) {
        getBtn(loc).setIcon(icon);
    }


    public void createGui() {
        setRelativeSizes();
        win = new CloseConfirmationJFrame(client::stopClient) {{
            setForeground(Color.BLACK);
            setSize(winSize);
            setLayout(new GridBagLayout());
        }};

        boardPnl = new BoardPanel(ROWS, COLS, this);

        topPnl = new JPanel();
        bottomPnl = new JPanel();
        menuBar = new JMenuBar();

        JMenu settingsMenu = new JMenu("Menu");
        settingsMenu.setFont(menuItemsFont);

        JCheckBoxMenuItem flipBoard = new JCheckBoxMenuItem("Flip Board");
        flipBoard.setFont(menuItemsFont);
        flipBoard.addActionListener(e -> {
            flipBoard();
        });
        flipBoard.setState(isBoardFlipped());
        settingsMenu.add(flipBoard);

        Font uiBtnsFont = new Font(null, Font.BOLD, 30);

        menuBar.add(settingsMenu);

        statusLbl = new JLabel();
        statusLbl.setFont(messagesFont);
        bottomPnl.add(statusLbl);

        boardPnl.createMat();
        resetBackground();
        layoutSetup();
        ShowOnScreen.showOnScreen(win);
//        win.setVisible(true);

    }

    private void flipBoard() {
        setBoardOrientation(boardOrientation.getOpponent());
    }

    public JFrame getWin() {
        return win;
    }

    public PlayerColor getBoardOrientation() {
        return boardOrientation;
    }

    public void setBoardOrientation(PlayerColor boardOrientation) {
        this.boardOrientation = boardOrientation;
        boardPnl.setCoordinates(false);
        boardPnl.createMat();
        boardPnl.getButtonsPnl().repaint();
        boardPnl.getBoardOverlay().repaintLayer();
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
        return boardOrientation != PlayerColor.WHITE;
    }

    public BoardButton boardButtonPressed(Location loc) {
        client.boardButtonPressed(loc);
        return getBtn(loc);
    }

    public void setStatusLbl(String str) {
        if (str.equals(""))
            str = " ";
        statusLbl.setText(str);
    }

    public void updateMoveLog(String move, int moveNum, int moveIndex) {
        sidePanel.getMoveLog().addMoveStr(move, moveNum, moveIndex);
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


    public void highlightPath(ArrayList<Move> movableSquares) {
        for (Move move : movableSquares) {
            Location movingTo = move.getMovingTo();

            if (move.isCapturing())
                highlightSquare(movingTo, red);
            else if (move.getMoveFlag() == Move.MoveFlag.Promotion)
                highlightSquare(movingTo, promotingSquareColor);
            else
                highlightSquare(movingTo, yellow);
            enableSquare(movingTo, true);
        }
    }

    public void enablePath(ArrayList<Move> movableSquares) {
        enableAllSquares(false);
        if (movableSquares != null)
            for (Move move : movableSquares) {
                Location movingTo = move.getMovingTo();
                enableSquare(movingTo, true);
            }
    }

    public void enableSquares(ArrayList<Location> list) {
        enableAllSquares(false);
        for (Location loc : list) {
            enableSquare(loc, true);
        }
    }

    public void showMessage(String message, String title, ImageIcon icon) {
        MyDialogs.showInfoMessage(win, message, title, icon);
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
                btn.setSelected(false);
            }
        }
    }

    public void colorCurrentPiece(Location loc) {
        getBtn(loc).setBackground(currentBtnColor);
    }

    public void drawArrow(Location from, Location loc, Color clr) {
        boardPnl.getBoardOverlay().drawArrow(from, loc, clr);
    }

    @Override
    public Iterator<BoardButton[]> iterator() {
        return Arrays.stream(boardPnl.getBtnMat()).iterator();
    }

    public void setTitle(String title) {
        win.setTitle(title);
    }

    public void makeMove(Move move) {
        enableAllSquares(false);

        if (move.getIntermediateMove() != null) {
            makeBasicMove(move.getIntermediateMove());
        }
        makeBasicMove(move);
    }

    public void makeBasicMove(BasicMove basicMove) {
        BoardButton prevBtn = getBtn(basicMove.getMovingFrom());
        BoardButton newBtn = getBtn(basicMove.getMovingTo());
        newBtn.setIcon(prevBtn.getIcon());
        prevBtn.resetIcon();
    }
}
