package ver14.view;

import com.formdev.flatlaf.FlatLightLaf;
import ver14.Client;
import ver14.SharedClasses.AuthSettings;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.DBActions.DBResponse;
import ver14.SharedClasses.DBActions.Graphable.Graphable;
import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.Game.BoardSetup.Board;
import ver14.SharedClasses.Game.GameTime;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.moves.BasicMove;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Game.pieces.Piece;
import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.ui.MyLbl;
import ver14.SharedClasses.ui.windows.CloseConfirmationJFrame;
import ver14.view.AuthorizedComponents.AuthorizedComponent;
import ver14.view.AuthorizedComponents.Menu;
import ver14.view.AuthorizedComponents.MenuItem;
import ver14.view.Board.BoardButton;
import ver14.view.Board.BoardPanel;
import ver14.view.Board.ViewLocation;
import ver14.view.Dialog.Cards.MessageCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.DialogProperties.Properties;
import ver14.view.Dialog.Dialogs.SimpleDialogs.MessageDialog;
import ver14.view.Dialog.Dialogs.SimpleDialogs.SimpleDialog;
import ver14.view.Dialog.SyncableList;
import ver14.view.Graph.Graph;
import ver14.view.IconManager.IconManager;
import ver14.view.IconManager.Size;
import ver14.view.SidePanel.SidePanel;
import ver14.view.SyncedJMenus.ConnectedUsers;
import ver14.view.SyncedJMenus.OngoingGames;
import ver14.view.SyncedJMenus.SyncedJMenu;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;


public class View implements Iterable<BoardButton[]> {
    public static final String CLIENT_WIN_TITLE = "Chess Client";
    private final static boolean WIREFRAME = false;
    private final static Dimension winSize;
    private final static Color statusLblNormalClr = Color.BLACK;
    private final static Color statusLblHighlightClr = Color.BLUE;
    private static final int ROWS = 8;
    private static final int COLS = 8;

    static {
        GraphicsEnvironment gbd = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Dimension d = gbd.getScreenDevices()[0].getDefaultConfiguration().getBounds().getSize();
        winSize = new Dimension((int) (d.width / 3), (int) (d.height / 2));

        FlatLightLaf.setup();
    }

    private final ArrayList<Dialog> displayedDialogs;
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
        this.displayedDialogs = new ArrayList<>();
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
        createMenuBar();

        statusLbl = new MyLbl();
        statusLbl.setFont(FontManager.statusLbl);
        resetStatusLbl();
        bottomPnl.add(statusLbl);

        boardPnl.createMat();
        resetBackground();
        layoutSetup();

        win.setVisible(true);
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();
        ArrayList<JComponent> start = new ArrayList<>();
        ArrayList<JComponent> middle = new ArrayList<>();
        ArrayList<JComponent> end = new ArrayList<>();

        Font menuFont = FontManager.menuFont;
        Font menuItemsFont = FontManager.menuItemsFont;

        Menu settingsMenu = new Menu("settings", AuthSettings.NO_AUTH) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        IconManager.dynamicSettingsIcon.set(settingsMenu);
        authorizedComponents.add(settingsMenu);
        end.add(settingsMenu);

        Menu statisticsMenu = new Menu("statistics", AuthSettings.ANY_LOGIN) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        IconManager.dynamicStatisticsIcon.set(statisticsMenu);
        authorizedComponents.add(statisticsMenu);
        start.add(statisticsMenu);

        Menu serverStatusMenu = new Menu("server status", AuthSettings.NO_AUTH) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        authorizedComponents.add(serverStatusMenu);
        IconManager.dynamicServerIcon.set(serverStatusMenu);
        start.add(serverStatusMenu);

        Menu userSettings = new Menu("user settings", AuthSettings.USER) {{
            setFont(menuFont);
            setChildrenFont(menuItemsFont);
        }};
        authorizedComponents.add(userSettings);
        settingsMenu.add(userSettings);

        userSettings.add(new MenuItem("change password") {{
            addActionListener(l -> client.changePassword());
        }});

        statisticsMenu.setFont(menuFont);
        for (PreMadeRequest preMadeRequest : PreMadeRequest.statistics) {
            Menu currentMenu = statisticsMenu;
            PreMadeRequest[] variations = preMadeRequest.getRequestVariations();

            if (variations.length > 0) {
                Menu newMenu = new Menu(preMadeRequest.createBuilder().getName(), preMadeRequest.authSettings);
                newMenu.setFont(currentMenu.getFont());
                authorizedComponents.add(newMenu);
                currentMenu.add(newMenu);
                currentMenu = newMenu;
            }
            MenuItem parentItem = createRequestMenuItem(preMadeRequest, menuItemsFont);
            authorizedComponents.add(parentItem);
            currentMenu.add(parentItem);

            Menu finalCurrentMenu = currentMenu;
            Arrays.stream(variations).forEach(req -> {
                MenuItem requestMenuItem = createRequestMenuItem(req, menuItemsFont);
                authorizedComponents.add(requestMenuItem);
                finalCurrentMenu.add(requestMenuItem);
            });

        }

        SyncedJMenu connectedUsers = new ConnectedUsers();
        serverStatusMenu.add(connectedUsers.getJMenu());
        listsToRegister.add(connectedUsers);

        SyncedJMenu ongoingGames = new OngoingGames();
        serverStatusMenu.add(ongoingGames.getJMenu());
        listsToRegister.add(ongoingGames);

        settingsMenu.add(new MenuItem("about") {{
            addActionListener(e -> {
                showMessage("i really hope i remembered to change this", "About", MessageCard.MessageType.INFO);
            });
        }});

        start.forEach(menuBar::add);
        if (!middle.isEmpty()) {
            menuBar.add(Box.createHorizontalGlue());
            middle.forEach(menuBar::add);
        }
        menuBar.add(Box.createHorizontalGlue());
        end.forEach(menuBar::add);
    }

    private MenuItem createRequestMenuItem(PreMadeRequest preMadeRequest, Font menuItemsFont) {
        return new MenuItem(preMadeRequest.createBuilder().getName(), preMadeRequest.authSettings) {{
            addActionListener(l -> {
                client.request(preMadeRequest);
            });
            setFont(menuItemsFont);
        }};

    }

    private void winResized() {
        repaint();
        boardPnl.onResize();
    }

    public void showMessage(String message, String title, MessageCard.MessageType messageType) {
        showDialog(new MessageDialog(client.dialogProperties(), message, title, messageType));

    }

    public void resetStatusLbl() {
        resetStatusLblClr();
        setStatusLbl("");
    }

    public void resetBackground() {
        boardPnl.forEachBtnParallel(BoardButton::resetBackground);
    }

    private void layoutSetup() {
        //שורת התפריטים
//        win.setJMenuBar(menuBar);
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

    public void resetStatusLblClr() {
        setStatusLbl("");
    }

    public void setStatusLbl(String str) {
        setStatusLbl(str, statusLblNormalClr);
    }

    public void authChange(LoginInfo loginInfo) {
        for (AuthorizedComponent component : authorizedComponents) {
            component.setAuth(loginInfo);
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
        sidePanel.enableBtns(true);
        currentGameStr = playerColor.getName() + " vs " + otherPlayer;
        updateTitle();
    }

    public void resetAllBtns() {
        boardPnl.resetAllButtons(true);
    }

    public void setUsername(String username) {
        this.username = username;
        updateTitle();
    }

    private void updateTitle() {
        win.setTitle(CLIENT_WIN_TITLE + " " + username + " " + currentGameStr);
    }

    public void setGameTime(GameTime gameTime) {
        if (gameTime != null)
            sidePanel.sync(gameTime);
    }

    public void setBtnPiece(Location loc, Piece piece) {
        getBtn(loc).setPiece(piece);
    }

    public BoardButton getBtn(Location loc) {
        return boardPnl.getBtn(loc);
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

    public void boardButtonPressed(ViewLocation viewLoc) {
        client.boardButtonPressed(viewLoc);
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

    public void enableAllSquares(boolean bool) {
        for (BoardButton[] row : this) {
            for (BoardButton btn : row) {
                btn.setEnabled(bool);
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
        makeBasicMove(move.getIntermediateMove());
        makeBasicMove(move);
    }

    public void makeBasicMove(BasicMove basicMove) {
        if (basicMove == null)
            return;

        BoardButton prevBtn = getBtn(basicMove.getMovingFrom());
        BoardButton newBtn = getBtn(basicMove.getMovingTo());
        newBtn.setPiece(prevBtn);
        prevBtn.reset();
    }

    public void gameOver(String str) {
        enableAllSquares(false);
        setStatusLbl("Game Over " + str, statusLblHighlightClr);
        sidePanel.enableBtns(false);
    }

    public void setStatusLbl(String str, Color fg) {
        if (str == null || str.equals(""))
            str = " ";
        else
            str = StrUtils.format(str);
//        str = StrUtils.wrapInHtml(str);
        statusLbl.setForeground(fg);
        statusLbl.setText(str);
    }

    public void showDBResponse(DBResponse response, String respondingTo, String title) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));

        respondingTo = StrUtils.format(respondingTo);

        DBResponse currentRes = response;
        while (currentRes != null) {
            pnl.add(createSingleComp(currentRes));
            if (currentRes instanceof Graphable graphable)
                pnl.add(Graph.createGraph(graphable));
            currentRes = currentRes.getAddedRes();
        }

        assert response != null;

        Properties properties = client.dialogProperties(respondingTo, title + " | " + response.getStatus());

        showDialog(new SimpleDialog(properties, pnl));
    }

    private JComponent createSingleComp(DBResponse response) {
        String[][] rowData = response.getRows();
        String[] colsNames = StrUtils.format(response.getColumns());
        FitWidthTable table = new FitWidthTable(rowData, colsNames);
        table.getTableHeader().setFont(FontManager.dbResponseTableHeader);
        table.setFont(FontManager.dbResponseTable);
        table.setEnabled(false);
        table.fit();

        return new JScrollPane() {{
            setViewportView(table);

            SwingUtilities.invokeLater(() -> {
                Size size = new Size(getPreferredSize().width, table.getPreferredSize().height + 100);
                setMaximumSize(size);
                setPreferredSize(size);
            });
        }};
    }

    public <D extends Dialog> D showDialog(D dialog) {
        synchronized (displayedDialogs) {
            displayedDialogs.add(dialog);
            dialog.start(this::dialogClosed);
        }
        return dialog;
    }

    private void dialogClosed(Dialog dialog) {
        try {
            displayedDialogs.remove(dialog);
        } catch (ConcurrentModificationException e) {

        }
    }

    public void drawFocus() {
        // bring the window into front (DeIconified)
        win.setVisible(true);
        win.toFront();
        win.setState(JFrame.NORMAL);
    }

    public void dispose() {
        win.dispose();
        closeOpenDialogs();
    }

    public void closeOpenDialogs() {
        synchronized (displayedDialogs) {
            try {
                displayedDialogs.forEach(Dialog::closeNow);

            } catch (ConcurrentModificationException e) {
            }
        }
    }

    public void connectedToServer() {
        for (SyncableList list : listsToRegister) {
            client.getMessagesHandler().registerSyncableList(list);
        }
    }
}
