package ver9.view;


import com.formdev.flatlaf.FlatLightLaf;
import ver9.Client;
import ver9.SharedClasses.*;
import ver9.SharedClasses.DBActions.DBResponse;
import ver9.SharedClasses.DBActions.Graphable.Graphable;
import ver9.SharedClasses.DBActions.RequestBuilder;
import ver9.SharedClasses.board_setup.Board;
import ver9.SharedClasses.moves.BasicMove;
import ver9.SharedClasses.moves.Move;
import ver9.SharedClasses.pieces.Piece;
import ver9.SharedClasses.ui.MyLbl;
import ver9.SharedClasses.ui.dialogs.MyDialogs;
import ver9.SharedClasses.ui.windows.CloseConfirmationJFrame;
import ver9.view.AuthorizedComponents.AuthorizedComponent;
import ver9.view.AuthorizedComponents.Menu;
import ver9.view.AuthorizedComponents.RequestMenuItem;
import ver9.view.Board.BoardButton;
import ver9.view.Board.BoardPanel;
import ver9.view.Dialog.SyncableList;
import ver9.view.Graph.Graph;
import ver9.view.SidePanel.SidePanel;
import ver9.view.SyncedJMenus.ConnectedUsers;
import ver9.view.SyncedJMenus.OngoingGames;
import ver9.view.SyncedJMenus.SyncedJMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class View implements Iterable<BoardButton[]> {


    public static final String CLIENT_WIN_TITLE = "Chess Client";
    private final static boolean WIREFRAME = false;
    private final static Dimension winSize;

    static {
        GraphicsEnvironment gbd = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Dimension d = gbd.getScreenDevices()[0].getDefaultConfiguration().getBounds().getSize();
        winSize = new Dimension((int) (d.width / 3), (int) (d.height / 2));

        FlatLightLaf.setup();
    }

    private final int ROWS;
    private final int COLS;
    private final Client client;
    private final ArrayList<AuthorizedComponent> authorizedComponents = new ArrayList<>();
    private final ArrayList<SyncableList> listsToRegister = new ArrayList<>();
    private BoardPanel boardPnl;
    private SidePanel sidePanel;
    private JMenuBar menuBar;
    private JPanel topPnl, bottomPnl;
    private MyLbl statusLbl;
    private PlayerColor boardOrientation;
    private CloseConfirmationJFrame win;
    private String username = "";
    private String currentGameStr = "";

    public View(Client client) {

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
        win = new CloseConfirmationJFrame(client::disconnectFromServer, this::winResized) {
            {
                setLayout(new GridBagLayout());
                setForeground(Color.BLACK);
                setSize(winSize);
                setLocationRelativeTo(null);
                setAlwaysOnTop(true);
                addKeyListener(sidePanel.moveLog.createNavAdapter());
            }

            @Override
            public void add(Component comp, Object constraints) {
                if (WIREFRAME && comp instanceof JComponent jcomp)
                    jcomp.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
                super.add(comp, constraints);
            }

        };

        boardPnl = new BoardPanel(ROWS, COLS, this);

        topPnl = new JPanel();
        bottomPnl = new JPanel();
        menuBar = new JMenuBar();
        menuBar.setLayout(new BorderLayout());
        Font menuFont = FontManager.menuFont;
        Font menuItemsFont = FontManager.menuItemsFont;

        JMenu settingsMenu = new JMenu("Settings") {{
            setFont(menuFont);
            setIcon(IconManager.settingsIcon);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    setIcon(IconManager.settingsGif);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    setIcon(IconManager.settingsIcon);
                }
            });
        }};
        Menu statisticsMenu = new Menu("Statistics", AuthSettings.ANY_LOGIN) {{
            setFont(menuFont);
            setIcon(IconManager.statisticsIcon);
        }};
        authorizedComponents.add(statisticsMenu);

        for (RequestBuilder.PreMadeRequest preMadeRequest : RequestBuilder.PreMadeRequest.values()) {
            RequestMenuItem requestMenuItem = new RequestMenuItem(preMadeRequest) {{
                setFont(menuItemsFont);
                addActionListener(l -> {
                    client.requestStats(preMadeRequest);
                });
            }};
            authorizedComponents.add(requestMenuItem);
            statisticsMenu.add(requestMenuItem);
        }
        menuBar.add(statisticsMenu, BorderLayout.LINE_START);

        SyncedJMenu connectedUsers = new ConnectedUsers();
        settingsMenu.add(connectedUsers.getJMenu());
        listsToRegister.add(connectedUsers);

        SyncedJMenu ongoingGames = new OngoingGames();
        settingsMenu.add(ongoingGames.getJMenu());
        listsToRegister.add(ongoingGames);

        settingsMenu.add(new JMenuItem("about") {{
            setFont(menuItemsFont);
            addActionListener(e -> {
                showMessage("i really hope i remembered to change this", "About", null);
            });
        }});
        menuBar.add(settingsMenu, BorderLayout.LINE_END);

        statusLbl = new MyLbl(" ");
        statusLbl.setFont(FontManager.statusLbl);
        bottomPnl.add(statusLbl);

        boardPnl.createMat();
        resetBackground();
        layoutSetup();

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

    public void authChange(LoginInfo loginInfo) {
        for (AuthorizedComponent component : authorizedComponents) {
            boolean enabled = component.setAuth(loginInfo);
            System.out.println("authing " + component + "\nenabled: " + enabled);
        }
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
            str = StrUtils.format(str);
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
            else
                btn.setAsMovable();

            if (move.getMoveFlag() == Move.MoveFlag.Promotion)
                btn.setAsPromotion();

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

    public void showDBResponse(DBResponse response) {
        JPanel pnl;
        if (response instanceof Graphable graphable)
            pnl = Graph.createStatisticsGraph(graphable);
        else {
            String[][] rowData = response.getRows();
            String[] colsNames = response.getColumns();
            JTable table = new JTable(rowData, colsNames);
            table.setEnabled(false);
            pnl = new JPanel();
            pnl.add(table);
        }
        MyDialogs.showInfo(win, pnl, "db res", null);

    }

    public void dispose() {
        win.dispose();

    }

    public void connectedToServer() {

        for (SyncableList list : listsToRegister) {
            client.getMessagesHandler().registerSyncableList(list);
        }
    }
}
