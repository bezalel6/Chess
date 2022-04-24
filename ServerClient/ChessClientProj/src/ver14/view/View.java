package ver14.view;

import com.formdev.flatlaf.FlatLightLaf;
import ver14.Client;
import ver14.SharedClasses.Callbacks.AnswerCallback;
import ver14.SharedClasses.DBActions.DBResponse.DBResponse;
import ver14.SharedClasses.DBActions.DBResponse.Graphable.GraphableDBResponse;
import ver14.SharedClasses.DBActions.DBResponse.StatusResponse;
import ver14.SharedClasses.DBActions.DBResponse.TableDBResponse;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.GameSetup.GameTime;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.Moves.BasicMove;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.UI.MyJFrame;
import ver14.SharedClasses.UI.MyLbl;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.Sound.SoundManager;
import ver14.view.AuthorizedComponents.AuthorizedComponent;
import ver14.view.Board.BoardButton;
import ver14.view.Board.BoardPanel;
import ver14.view.Board.ViewLocation;
import ver14.view.Dialog.Cards.MessageCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Dialogs.SimpleDialogs.MessageDialog;
import ver14.view.Dialog.Dialogs.SimpleDialogs.SimpleDialog;
import ver14.view.Dialog.Properties;
import ver14.view.Dialog.Scrollable;
import ver14.view.Dialog.*;
import ver14.view.Graph.Graph;
import ver14.view.IconManager.IconManager;
import ver14.view.IconManager.Size;
import ver14.view.MenuBar.MenuBar;
import ver14.view.SidePanel.SidePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.*;


public class View extends SoundManager implements Iterable<BoardButton[]> {
    public static final String CLIENT_WIN_TITLE = "Chess Client";
    private final static Dimension winSize;
    private final static Color statusLblNormalClr = Color.BLACK;
    private final static Color statusLblHighlightClr = Color.BLUE;
    private static final int ROWS = 8;
    private static final int COLS = 8;

    static {
        GraphicsEnvironment gbd = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Dimension d = gbd.getScreenDevices()[0].getDefaultConfiguration().getBounds().getSize();
        int div = 2;
        winSize = new Dimension((int) (d.width / 2.5), (int) (d.height / div));

        FlatLightLaf.setup();
    }

    public final Object boardLock = new Object();
    private final ArrayList<Dialog> displayedDialogs;
    private final Client client;
    private final ArrayList<AuthorizedComponent> authorizedComponents = new ArrayList<>();
    private final ArrayList<SyncableList> listsToRegister = new ArrayList<>();
    private BoardPanel boardPnl;
    private SidePanel sidePanel;
    private ver14.view.MenuBar.MenuBar menuBar;
    private JPanel topPnl, bottomPnl;
    private MyLbl statusLbl;
    private PlayerColor boardOrientation;
    private MyJFrame win;
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

    public void addListToRegister(SyncableList list) {
        listsToRegister.add(list);
    }

    private void setIcon(PlayerColor myClr) {
        win.setIconImage(IconManager.getPieceIcon(myClr, PieceType.KING).getImage());
    }

    public void createGui() {
        win = new MyJFrame() {
            {

                setLayout(new GridBagLayout());
                setForeground(Color.BLACK);
                setSize(winSize);
                setLocationRelativeTo(null);
                setOnExit((BooleanClosing) client::disconnectFromServer);
                setOnResize(View.this::winResized);

                getMyAdapter().addAction(client::makeRandomMove, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_R);
//                setCursor(Toolkit.getDefaultToolkit().createCustomCursor(IconManager.dynamicStatisticsIcon.getHover().getImage(), new Point(0, 0), "My Cursor"));
            }

        };
        setIcon(PlayerColor.WHITE);
        boardPnl = new BoardPanel(ROWS, COLS, this);

        topPnl = new JPanel();
        bottomPnl = new JPanel(new GridLayout(0, 1));
        menuBar = new MenuBar(authorizedComponents, client, this);

        statusLbl = new MyLbl();
        statusLbl.setFont(FontManager.statusLbl);

        resetStatusLbl();
        bottomPnl.add(statusLbl);

        boardPnl.createMat();
        resetBackground();
        layoutSetup();
        addWinListeners();

        SwingUtilities.invokeLater(() -> {
            statusLbl.setPreferredSize(new Size(win.getWidth() / 2, statusLbl.getPreferredSize().height));
        });

        win.setVisible(true);
    }

    private void addWinListeners() {
        win.addKeyListener(sidePanel.moveLog.createNavAdapter());
        boardPnl.getBoardOverlay().createClrs().forEach(win.getMyAdapter()::addHeldDown);
//        win.addKeyListener(boardPnl.getBoardOverlay().createKeyAdapter());
    }

    private void winResized() {
        boardPnl.onResize();
    }

    public void showMessage(String message, String title, MessageCard.MessageType messageType) {
        try {
            showDialog(new MessageDialog(client.dialogProperties(), message, title, messageType));
        } catch (IllegalStateException e) {//that font err
            System.out.println("the font err thing");
        }

    }

    public <D extends Dialog> D showDialog(D dialog) {
        if (dialog instanceof MessageDialog) {
            closeOpenDialogs();
        }
        displayedDialogs.add(dialog);
        drawFocus();
        dialog.start(this::dialogClosed);
        return dialog;
    }

    public void closeOpenDialogs() {
        try {
            displayedDialogs.forEach(d -> {
                if (!(d instanceof MessageDialog)) {
                    d.closeNow();
                }
            });
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
//            System.out.println(e);
        }
    }

    public void drawFocus() {
        // bring the window into front (DeIconified)
//        boolean fullscreen = false;
//        win.setState(JFrame.ICONIFIED);
//        win.setState(fullscreen ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);
        win.setAlwaysOnTop(true);
        win.toFront();
        win.setVisible(true);
        win.setAlwaysOnTop(false);
        win.setState(JFrame.NORMAL);
        win.requestFocus();
    }

    //fixme
    private void dialogClosed(Dialog dialog) {
//        try {
//            displayedDialogs.remove(dialog);
//        } catch (ConcurrentModificationException e) {
//            e.printStackTrace();
//        }
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
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        win.add(sidePanel, gbc);

        //שורה תחתונה

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 3;
//        gbc.gridy =
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        gbc.fill = GridBagConstraints.HORIZONTAL;
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

    public void flipBoard() {
        setBoardOrientation(boardOrientation.getOpponent());
    }

    public boolean isBoardFlipped() {
        return boardOrientation != PlayerColor.WHITE;
    }

    public BoardPanel getBoardPnl() {
        return boardPnl;
    }

    public void initGame(GameTime gameTime, Board board, PlayerColor playerColor, String otherPlayer) {
        synchronized (boardLock) {
            setBoardOrientation(playerColor);
            resetStatusLbl();
            resetAllBtns();
            boardPnl.setBoardButtons(board);
            boardPnl.getBoardOverlay().clearAllArrows();
            sidePanel.initGame(playerColor, client.getUsername(), otherPlayer, gameTime);
            currentGameStr = playerColor.getName() + " vs " + otherPlayer + " " + playerColor.getOpponent();
            setIcon(playerColor);
            updateTitle();
        }

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

    public MyJFrame getWin() {
        return win;
    }

    public PlayerColor getBoardOrientation() {
        return boardOrientation;
    }

    public void setBoardOrientation(PlayerColor boardOrientation) {
        this.boardOrientation = boardOrientation;
        boardPnl.resetOrientation();
        sidePanel.setFlipped(isBoardFlipped());

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

    public void highlightPath(List<Move> movableSquares) {
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

    public void enableMyPieces(PlayerColor clr) {
        synchronized (boardLock) {
            for (var row : this) {
                for (var btn : row) {
                    btn.setEnabled(btn.getPiece() != null && btn.getPiece().isOnMyTeam(clr));
                }
            }
            sidePanel.moveLog.resetCurrentBoard();
        }
    }

//    public void enableSources(ArrayList<Move> movableSquares) {
//        synchronized (boardLock) {
//
//            enableAllSquares(false);
//            if (movableSquares != null) {
//                for (Move move : movableSquares) {
//                    Location movingFrom = move.getMovingFrom();
////                    getBtn(movingFrom).setAsCurrent();
//                    enableSquare(movingFrom, true);
//                }
//                sidePanel.moveLog.resetCurrentBoard();
//            }
//        }
//    }

    public void inCheck(Location kingLoc) {
        getBtn(kingLoc).setAsCheck();
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
        synchronized (boardLock) {
            enableAllSquares(false);
            sidePanel.moveLog.preAdding();
            makeMove(move);
            boardPnl.resizeIcons();
            sidePanel.moveLog.addMove(move);
        }
    }

    public void makeMove(Move move) {
        makeBasicMove(move.getIntermediateMove());
        makeBasicMove(move);
    }

    public void makeBasicMove(BasicMove basicMove) {
        if (basicMove == null)
            return;

        synchronized (boardLock) {
            BoardButton prevBtn = getBtn(basicMove.getMovingFrom());
            BoardButton newBtn = getBtn(basicMove.getMovingTo());
            newBtn.setPiece(prevBtn);
            prevBtn.reset();

        }
    }

    public void askQuestion(Question question, AnswerCallback callback) {
        sidePanel.askPlayerPnl.ask(question, callback);
        win.pack();
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public void gameOver(GameStatus gameOverStatus) {
        sidePanel.stopRunningTime();
        sidePanel.askPlayerPnl.removeQuestion(Question.QuestionType.DRAW_OFFER);

        enableAllSquares(false);
        setStatusLbl("Game Over " + gameOverStatus.getDetailedStr(client.getPlayerUsernames()), statusLblHighlightClr);
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
//        win.pack();
    }

    public void showDBResponse(DBResponse response, String respondingTo, String title) {
        WinPnl pnl = new WinPnl();
//        pnl.setInsets(new Insets(10, 10, 10, 10));
//        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));

        respondingTo = StrUtils.format(respondingTo);

        DBResponse currentRes = response;
        while (currentRes != null) {
            pnl.add(createSingleComp(currentRes));
            currentRes = currentRes.getAddedRes();
        }

        Properties properties = client.dialogProperties(respondingTo, title + " | " + response.getStatus());

        showDialog(new SimpleDialog(properties, pnl));
    }

    private JComponent createSingleComp(DBResponse _response) {
        if (_response instanceof TableDBResponse response) {
//            System.out.println(response);
            Header header = new Header(response.getRequest().getBuilder().getPostDescription());
            String[][] rowData = response.getRows();
            String[] colsNames = StrUtils.format(response.getColumns());
            FitWidthTable table = new FitWidthTable(rowData, colsNames);
            table.getTableHeader().setFont(FontManager.dbResponseTableHeader);
            table.setFont(FontManager.dbResponseTable);
            table.setEnabled(false);
            table.fit();

            JScrollPane scrollPane = new JScrollPane() {{
                setViewportView(table);
//                SwingUtilities.invokeLater(() -> {
//                    Size size = new Size(getPreferredSize().width, table.getPreferredSize().height + 100);
//                    setMaximumSize(size);
//                    setPreferredSize(size);
//                });
            }};
            int spacing = -25;
            Scrollable.applyDefaults(scrollPane, table.getComputedSize().padding(spacing));
            return new WinPnl(header) {{
//                add(scrollPane);
                add(scrollPane);

            }};
        } else if (_response instanceof GraphableDBResponse response) {
            return Graph.createGraph(response);
        } else if (_response instanceof StatusResponse response) {
            return MessageCard.createMsgPnl(response.getDetails(), response.getStatus() == DBResponse.Status.SUCCESS ? MessageCard.MessageType.INFO : MessageCard.MessageType.ERROR, new Size(400));
        }
        return null;
    }

    public void dispose() {
        win.dispose();
        closeOpenDialogs();
    }

    public void connectedToServer() {
        for (SyncableList list : listsToRegister) {
            client.getMessagesHandler().registerSyncableList(list);
        }
    }

    public void colorMove(Move move) {
        synchronized (boardLock) {
            getBtn(move.getMovingFrom()).movingFrom();
            getBtn(move.getMovingTo()).movingTo();
        }
    }
}
