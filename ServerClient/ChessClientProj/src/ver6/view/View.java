package ver6.view;


import ver6.Client;
import ver6.SharedClasses.*;
import ver6.SharedClasses.Sync.SyncedListType;
import ver6.SharedClasses.board_setup.Board;
import ver6.SharedClasses.moves.BasicMove;
import ver6.SharedClasses.moves.Move;
import ver6.SharedClasses.pieces.Piece;
import ver6.SharedClasses.ui.MyLbl;
import ver6.SharedClasses.ui.dialogs.MyDialogs;
import ver6.SharedClasses.ui.windows.CloseConfirmationJFrame;
import ver6.view.Graph.Graph;
import ver6.view.List.Synced.SyncableList;
import ver6.view.List.Synced.SyncedJMenu;
import ver6.view.SidePanel.SidePanel;
import ver6.view.board.BoardButton;
import ver6.view.board.BoardPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class View implements Iterable<BoardButton[]> {

    public static final String CLIENT_WIN_TITLE = "Chess Client";
    private final static Dimension winSize;

    static {
        GraphicsEnvironment gbd = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Dimension d = gbd.getScreenDevices()[0].getDefaultConfiguration().getBounds().getSize();
        winSize = new Dimension((int) (d.width / 3), (int) (d.height / 2));

//        FlatLightLaf.setup();
    }

    private final int ROWS;
    private final int COLS;
    private final Client client;
    private final boolean visible;
    private JMenu connectedPlayersMenu;
    private SyncableList connectedUsers;
    private BoardPanel boardPnl;
    private JMenuItem statisticsBtn;
    private SidePanel sidePanel;
    private JMenuBar menuBar;
    private JPanel topPnl, bottomPnl;
    private MyLbl statusLbl;
    private PlayerColor boardOrientation;
    private CloseConfirmationJFrame win;
    private String username = "";
    private String currentGameStr = "";

    public View(Client client, boolean visible) {
        this.visible = visible;

        ROWS = COLS = 8;
        this.client = client;
        this.boardOrientation = PlayerColor.WHITE;
        sidePanel = new SidePanel(0, isBoardFlipped(), client);
        createGui();
        sidePanel.moveLog.setBoardPanel(boardPnl);
        enableAllSquares(false);
    }

    public boolean isBoardFlipped() {
        return boardOrientation != PlayerColor.WHITE;
    }

    public void createGui() {
        win = new CloseConfirmationJFrame(client::disconnectedFromServer, this::winResized) {
            {
                setForeground(Color.BLACK);
                setSize(winSize);
                setLayout(new GridBagLayout());
                setLocationRelativeTo(null);
                setAlwaysOnTop(false);
                addKeyListener(sidePanel.moveLog.createNavAdapter());
                addKeyListener(new KeyAdapter() {
                    private final static String keyword = "ilan|yaniv|אילן|יניב";
                    private String typing = "";

                    @Override
                    public void keyTyped(KeyEvent e) {
                        super.keyTyped(e);
                        System.out.println(e);
                        typing += e.getKeyChar();
                        System.out.println(typing);
                        if (typing.matches("(?i)" + keyword)) {
                            MyLbl.toggle();
                            typing = "";
                        }
                    }
                });

            }

            @Override
            public void add(Component comp, Object constraints) {
                if (comp instanceof JComponent jcomp)
                    jcomp.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
                super.add(comp, constraints);
            }

        };

        boardPnl = new BoardPanel(ROWS, COLS, this);

        topPnl = new JPanel();
        bottomPnl = new JPanel();
        menuBar = new JMenuBar();

        Font menuFont = FontManager.menuItems;

        MyLbl menuHeader = new MyLbl("Menu", menuFont);
        JMenu settingsMenu = new JMenu() {{
            FontMetrics metrics = getFontMetrics(menuFont);
            setPreferredSize(new Dimension(metrics.stringWidth(menuHeader.getText()) * 2, metrics.getHeight() * 2));
            setFont(menuFont);
        }};

        settingsMenu.add(menuHeader, BorderLayout.CENTER);
        settingsMenu.setFont(menuFont);

        statisticsBtn = new JMenuItem("Get Statistics", IconManager.statisticsIcon) {{
            addActionListener(e -> {
                client.statisticsBtnClicked();
            });
        }};

        settingsMenu.add(statisticsBtn);

        connectedUsers = new SyncedJMenu("connected", e -> {
        }, SyncedListType.CONNECTED_PLAYERS);
//        connectedUsers.sync(new SyncedItems<UserInfo>(SyncedListType.CONNECTED_PLAYERS) {{
//            add(new UserInfo("idddd"));
//        }});

        settingsMenu.add(connectedUsers.list().navigationComp());

        settingsMenu.add(new JMenuItem("About") {{
            addActionListener(e -> {
                showMessage("i really hope i remembered to change this", "About", null);
            });
        }});

        menuBar.add(settingsMenu);

        statusLbl = new MyLbl(" ");
        statusLbl.setFont(FontManager.statusLbl);
        bottomPnl.add(statusLbl);

        boardPnl.createMat();
        resetBackground();
        layoutSetup();

        if (!visible)
            win.setState(JFrame.ICONIFIED);
        win.setVisible(true);


    }

    public void enableAllSquares(boolean bool) {
        for (BoardButton[] row : this) {
            for (BoardButton btn : row) {
                btn.setEnabled(bool);
            }
        }
    }


    private void winResized() {
        repaint();
        boardPnl.onResize();
    }

    public void showMessage(String message, String title, ImageIcon icon) {
        MyDialogs.showInfoMessage(win, message, title, icon);
    }

    public void resetBackground() {
        boardPnl.forEachBtnParallel(BoardButton::resetBackground);
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
        gbc.gridwidth = 3;
        gbc.gridheight = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.weightx = 2;
        gbc.weighty = 2;
        gbc.fill = GridBagConstraints.BOTH;
        win.add(boardPnl, gbc);

        gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 3;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        win.add(sidePanel, gbc);

        //שורה תחתונה

        gbc = new GridBagConstraints();

        win.add(bottomPnl, gbc);

//        win.pack();
    }


    public void repaint() {
        win.repaint();
    }

    private void flipBoard() {
        setBoardOrientation(boardOrientation.getOpponent());
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public BoardPanel getBoardPnl() {
        return boardPnl;
    }

    public void initGame(GameTime gameTime, Board board, PlayerColor playerColor, String otherPlayer) {
        setBoardOrientation(playerColor);
        resetStatusLbl();
        resetAllBtns();
        boardPnl.setBoardButtons(board);
        boardPnl.getBoardOverlay().clearAllArrows();
        sidePanel.reset(gameTime);
        currentGameStr = playerColor.getName() + " vs " + otherPlayer;
        updateTitle();
    }

    public void resetStatusLbl() {
        resetStatusLblClr();
        setStatusLbl("");
    }

    public void resetAllBtns() {
        boardPnl.resetAllButtons(true);
    }

    private void updateTitle() {
        win.setTitle(CLIENT_WIN_TITLE + " " + username + " " + currentGameStr);
    }

    public void resetStatusLblClr() {
        statusLbl.setForeground(Color.BLACK);

    }

    public void setStatusLbl(String str) {
        if (str == null || str.equals(""))
            str = " ";
        else
            str = StrUtils.uppercaseFirstLetters(str);
//        str = StrUtils.wrapInHtml(str);
        statusLbl.setText(str);
    }

    public void setUsername(String username) {
        this.username = username;
        updateTitle();
    }

    public void setGameTime(GameTime gameTime) {
        if (gameTime != null)
            sidePanel.sync(gameTime);
    }

    public void setBtnPiece(Location loc, Piece piece) {
        getBtn(loc).setPiece(piece);
    }

    public BoardButton getBtn(Location loc) {
        return getBtn(loc.getRow(), loc.getCol());
    }

    public BoardButton getBtn(int r, int c) {
        return boardPnl.getBtn(r, c);
    }

    public JFrame getWin() {
        return win;
    }

    public PlayerColor getBoardOrientation() {
        return boardOrientation;
    }

    public void setBoardOrientation(PlayerColor boardOrientation) {
        this.boardOrientation = boardOrientation;
        boardPnl.resetOrientation();
    }

    public void clearAllDrawings() {
        boardPnl.getBoardOverlay().clearAllArrows();
    }

    public void drawMove(Move move) {
        boardPnl.getBoardOverlay().drawMove(move);
    }

    public void boardButtonPressed(Location loc) {
        client.boardButtonPressed(loc);
    }

    public Location getBtnLoc(JButton source) {
        return ((BoardButton) source).getBtnLoc();
    }

    public void highlightPath(ArrayList<Move> movableSquares) {
        for (Move move : movableSquares) {
            Location movingTo = move.getMovingTo();
            BoardButton btn = getBtn(movingTo);
            if (move.isCapturing())
                btn.setAsCapture();
            if (move.getMoveFlag() == Move.MoveFlag.Promotion)
                btn.setAsPromotion();
            else
                btn.setAsMovable();
            enableSquare(movingTo, true);
        }
    }

    public void enableSquare(Location loc, boolean enabled) {
        getBtn(loc).setEnabled(enabled);
    }

    public void enablePath(ArrayList<Move> movableSquares) {
        enableAllSquares(false);
        if (movableSquares != null) {
            for (Move move : movableSquares) {
                Location movingTo = move.getMovingTo();
                enableSquare(movingTo, true);
            }
        }
    }

    public void enableSources(ArrayList<Move> movableSquares) {
        enableAllSquares(false);
        if (movableSquares != null) {
            for (Move move : movableSquares) {
                Location movingTo = move.getMovingFrom();
                enableSquare(movingTo, true);
            }
            sidePanel.moveLog.resetCurrentBoard();
        }
    }

    public void inCheck(Location kingLoc) {
        getBtn(kingLoc).setAsCheck();
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
        getBtn(loc).setAsCurrent();
    }

    public void drawArrow(Location from, Location loc, Color clr) {
        boardPnl.getBoardOverlay().drawArrow(from, loc, clr);
    }

    @Override
    public Iterator<BoardButton[]> iterator() {
        return Arrays.stream(boardPnl.getBtnMat()).parallel().iterator();
    }

    public void updateByMove(Move move) {
        enableAllSquares(false);
        sidePanel.moveLog.preAdding();
        makeMove(move);
        boardPnl.resizeIcons();
        sidePanel.moveLog.addMove(move);
    }

    public void makeMove(Move move) {
        if (move.getIntermediateMove() != null) {
            makeBasicMove(move.getIntermediateMove());
        }
        makeBasicMove(move);
    }

    public void makeBasicMove(BasicMove basicMove) {
        BoardButton prevBtn = getBtn(basicMove.getMovingFrom());
        BoardButton newBtn = getBtn(basicMove.getMovingTo());
        newBtn.setPiece(prevBtn);
        prevBtn.reset();
    }

    public void gameOver(String str) {
        enableAllSquares(false);
        setStatusLbl("Game Over " + str);
        statusLbl.setForeground(Color.BLUE);
    }

    public void showStatistics(PlayerStatistics playerStatistics) {

        MyDialogs.showInfo(win, Graph.createStatisticsGraph(playerStatistics), "statistics", null);
    }

    public void enableStatisticsBtn(boolean b) {
        statisticsBtn.setEnabled(b);
    }

    public void disposeWin() {
        win.dispose();

    }

    public void connectedToServer() {
        if (connectedUsers != null)
            client.getMessagesHandler().registerSyncedList(connectedUsers);
    }
}
