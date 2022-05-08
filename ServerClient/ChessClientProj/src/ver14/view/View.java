package ver14.view;

import com.formdev.flatlaf.FlatLightLaf;
import ver14.Client;
import ver14.SharedClasses.Callbacks.AnswerCallback;
import ver14.SharedClasses.Callbacks.VoidCallback;
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
import ver14.view.Board.State;
import ver14.view.Board.ViewLocation;
import ver14.view.Dialog.Cards.MessageCard.MessageCard;
import ver14.view.Dialog.Cards.MessageCard.MessageType;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.*;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Scrollable;
import ver14.view.Dialog.Dialogs.OtherDialogs.MessageDialog;
import ver14.view.Graph.Graph;
import ver14.view.IconManager.IconManager;
import ver14.view.IconManager.Size;
import ver14.view.MenuBar.MenuBar;
import ver14.view.SidePanel.SidePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * View - represents the GUI manager for the client. the view handles things like:
 * showing and proper disposal of dialogs, the main game window, window resizes, and more..
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class View extends SoundManager implements Iterable<BoardButton[]> {
    /**
     * The constant CLIENT_WIN_TITLE.
     */
    public static final String CLIENT_WIN_TITLE = "Chess Client";
    /**
     * The constant winSize.
     */
    private final static Dimension winSize;
    /**
     * The constant statusLblNormalClr.
     */
    private final static Color statusLblNormalClr = Color.BLACK;
    /**
     * The constant statusLblHighlightClr.
     */
    private final static Color statusLblHighlightClr = Color.BLUE;
    /**
     * The constant ROWS.
     */
    private static final int ROWS = 8;
    /**
     * The constant COLS.
     */
    private static final int COLS = 8;

    static {
        GraphicsEnvironment gbd = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Dimension d = gbd.getScreenDevices()[0].getDefaultConfiguration().getBounds().getSize();
        int div = 2;
        winSize = new Dimension((int) (d.width / 2.5), (int) (d.height / div));

        FlatLightLaf.setup();
    }

    /**
     * The Board lock. used for synchronization of certain actions
     */
    private final Object boardLock = new Object();
    /**
     * The Client.
     */
    private final Client client;
    /**
     * The Authorized components. used for invoking an update every time the client's authorization status changes
     */
    private final ArrayList<AuthorizedComponent> authorizedComponents = new ArrayList<>();
    /**
     * The Lists to register. as synchronized lists with the server
     */
    private final ArrayList<SyncableList> listsToRegister = new ArrayList<>();

    /**
     * a list for storing all the Displayed dialogs.
     */
    private ArrayList<Dialog> displayedDialogs;
    /**
     * The Side panel.
     */
    private SidePanel sidePanel;
    /**
     * The Board panel.
     */
    private BoardPanel boardPnl;
    /**
     * The Menu bar.
     */
    private ver14.view.MenuBar.MenuBar menuBar;
    /**
     * The Top pnl.
     */
    private JPanel topPnl;
    /**
     * The Bottom pnl.
     */
    private JPanel
            bottomPnl;
    /**
     * The Status lbl.
     */
    private MyLbl statusLbl;
    /**
     * The Board orientation.
     */
    private PlayerColor boardOrientation;
    /**
     * The main window.
     */
    private MyJFrame win;
    /**
     * The player's Username.
     */
    private String username = "";
    /**
     * The Current game description.
     */
    private String currentGameStr = "";

    /**
     * Instantiates a new View.
     *
     * @param client the client
     */
    public View(Client client) {
        this.client = client;
        this.boardOrientation = PlayerColor.WHITE;
        createGui();
        enableAllSquares(false);
    }

    /**
     * Add list to register with the server once a connection has been made
     *
     * @param list the list
     */
    public void addListToRegister(SyncableList list) {
        listsToRegister.add(list);
    }

    /**
     * Add an authorized component to the {@link View#authorizedComponents} list, so its state will change as the authorization
     * status of the client is changed.
     *
     * @param authorizedComponent the authorized component
     */
    public void addAuthorizedComponent(AuthorizedComponent authorizedComponent) {
        authorizedComponents.add(authorizedComponent);
    }

    /**
     * Sets icon.
     *
     * @param myClr this player's color. used for loading the appropriate icon
     */
    private void setIcon(PlayerColor myClr) {
        win.setIconImage(IconManager.getPieceIcon(myClr, PieceType.KING).getImage());
    }

    /**
     * Create gui.
     */
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
        this.displayedDialogs = new ArrayList<>();

        sidePanel = new SidePanel(isBoardFlipped(), this, client);
        setIcon(PlayerColor.WHITE);
        boardPnl = new BoardPanel(ROWS, COLS, this);

        topPnl = new JPanel();
        bottomPnl = new JPanel(new GridLayout(0, 1));
        menuBar = new MenuBar(client, this);

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

        sidePanel.moveLog.setBoardPanel(boardPnl);
    }

    /**
     * Add the window listeners.
     */
    private void addWinListeners() {
        win.addKeyListener(sidePanel.moveLog.createNavAdapter());
        boardPnl.getBoardOverlay().createClrs().forEach(win.getMyAdapter()::addHeldDown);
//        win.addKeyListener(boardPnl.getBoardOverlay().createKeyAdapter());
    }

    /**
     * invoke a Window resized event.
     */
    private void winResized() {
        boardPnl.onResize();
    }

    /**
     * Show message.
     *
     * @param message     the message
     * @param title       the title
     * @param messageType the message type
     */
    public void showMessage(String message, String title, MessageType messageType) {
        try {
            showDialog(new MessageDialog(client.dialogProperties(), message, title, messageType));
        } catch (IllegalStateException e) {//that font err
            System.out.println("the font err thing");
        }

    }

    /**
     * Show dialog d.
     *
     * @param <D>    the type parameter
     * @param dialog the dialog to show
     * @return the d
     */
    public <D extends Dialog> D showDialog(D dialog) {
        if (dialog instanceof MessageDialog) {
            closeOpenDialogs();
        }
        displayedDialogs.add(dialog);
        drawFocus();
        dialog.start(this::dialogClosed);
        return dialog;
    }

    /**
     * Close open dialogs.
     * closes all open dialogs but message dialogs
     */
    public void closeOpenDialogs() {
        displayedDialogs.forEach(d -> {
            if (!(d instanceof MessageDialog)) {
                d.closeNow();
            }
        });
    }

    /**
     * Draw focus. to the window
     */
    public void drawFocus() {
        // bring the window into front (DeIconified)
        win.setAlwaysOnTop(true);
        win.toFront();
        win.setVisible(true);
        win.setAlwaysOnTop(false);
        win.setState(JFrame.NORMAL);
        win.requestFocus();
    }

    /**
     * Dialog closed.
     *
     * @param dialog the dialog
     */
    private void dialogClosed(Dialog dialog) {
//        try {
//            displayedDialogs.remove(dialog);
//        } catch (ConcurrentModificationException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Reset status lbl.
     */
    public void resetStatusLbl() {
        resetStatusLblClr();
        setStatusLbl("");
    }

    /**
     * Reset board background.
     */
    public void resetBackground() {
        boardPnl.forEachBtnParallel(BoardButton::resetBackground);
    }

    /**
     * Layout setup.
     */
    private void layoutSetup() {
//        win.setJMenuBar(menuBar);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        win.add(menuBar, gbc);
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

    /**
     * Repaint.
     */
    public void repaint() {
        win.repaint();
    }

    /**
     * Reset status lbl clr.
     */
    public void resetStatusLblClr() {
        setStatusLbl("");
    }

    /**
     * Sets status lbl.
     *
     * @param str the str
     */
    public void setStatusLbl(String str) {
        setStatusLbl(str, statusLblNormalClr);
    }

    /**
     * Auth change notify all the authorized components of a change in the authorization of the client
     *
     * @param loginInfo the new login info
     */
    public void authChange(LoginInfo loginInfo) {
        for (AuthorizedComponent component : authorizedComponents) {
            component.setAuth(loginInfo);
        }
    }

    /**
     * Flip board.
     */
    public void flipBoard() {
        setBoardOrientation(boardOrientation.getOpponent());
    }

    /**
     * Is the board flipped.
     *
     * @return true if the board's orientation is not white
     */
    public boolean isBoardFlipped() {
        return boardOrientation != PlayerColor.WHITE;
    }

    /**
     * Gets the board pnl.
     *
     * @return the board pnl
     */
    public BoardPanel getBoardPnl() {
        return boardPnl;
    }

    /**
     * Initialize the view for a new game.
     *
     * @param gameTime    the game time
     * @param board       the board
     * @param playerColor this client's player color
     * @param otherPlayer the other player's username
     */
    public void initGame(GameTime gameTime, Board board, PlayerColor playerColor, String otherPlayer) {
        syncAction(() -> {
            setBoardOrientation(playerColor);
            resetStatusLbl();
            resetAllBtns();
            boardPnl.setBoardButtons(board);
            boardPnl.getBoardOverlay().clearAllArrows();
            sidePanel.initGame(playerColor, client.getUsername(), otherPlayer, gameTime);
            currentGameStr = playerColor.getName() + " vs " + otherPlayer + " " + playerColor.getOpponent();
            setIcon(playerColor);
            updateTitle();
            winResized();
        });

    }

    /**
     * Reset all board buttons.
     */
    public void resetAllBtns() {
        boardPnl.resetAllButtons(true);
    }

    /**
     * Sets this player's username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
        updateTitle();
    }

    /**
     * Update title.
     */
    private void updateTitle() {
        win.setTitle(CLIENT_WIN_TITLE + " " + username + " " + currentGameStr);
    }

    /**
     * Sets game time.
     *
     * @param gameTime the game time
     */
    public void setGameTime(GameTime gameTime) {
        if (gameTime != null)
            sidePanel.sync(gameTime);
    }

    /**
     * Sets btn piece.
     *
     * @param loc   the loc
     * @param piece the piece
     */
    public void setBtnPiece(Location loc, Piece piece) {
        getBtn(loc).setPiece(piece);
    }

    /**
     * Gets btn.
     *
     * @param loc the loc
     * @return the btn
     */
    public BoardButton getBtn(Location loc) {
        return boardPnl.getBtn(loc);
    }

    /**
     * Gets win.
     *
     * @return the win
     */
    public MyJFrame getWin() {
        return win;
    }

    /**
     * Gets board orientation.
     *
     * @return the board orientation
     */
    public PlayerColor getBoardOrientation() {
        return boardOrientation;
    }

    /**
     * Sets board orientation.
     *
     * @param boardOrientation the board orientation
     */
    public void setBoardOrientation(PlayerColor boardOrientation) {
        this.boardOrientation = boardOrientation;
        boardPnl.resetOrientation();
        sidePanel.setFlipped(isBoardFlipped());

    }

    /**
     * Clear all drawings.
     */
    public void clearAllDrawings() {
        boardPnl.getBoardOverlay().clearAllArrows();
    }

    /**
     * Draw move.
     *
     * @param move the move
     */
    public void drawMove(Move move) {
        boardPnl.getBoardOverlay().drawMove(move);
    }

    /**
     * Board button pressed.
     *
     * @param viewLoc the view loc
     */
    public void boardButtonPressed(ViewLocation viewLoc) {
        client.boardButtonPressed(viewLoc);
    }

    /**
     * Highlight path.
     *
     * @param movableSquares the movable squares
     */
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

//            if (move.getMoveFlag().isCastling) {
//                Location rookLoc = Location.getLoc(movingTo.row, move.getMoveFlag().castlingSide.rookStartingCol);
//                getBtn(rookLoc)();
//            }
            enableSquare(movingTo, true);
        }
        boardPnl.repaint();
    }

    /**
     * Enable square.
     *
     * @param loc     the loc
     * @param enabled the enabled
     */
    public void enableSquare(Location loc, boolean enabled) {
        getBtn(loc).setEnabled(enabled);
    }

    /**
     * Enable path.
     *
     * @param movableSquares the movable squares
     */
    public void enablePath(ArrayList<Move> movableSquares) {
        enableAllSquares(false);
        if (movableSquares != null) {
            for (Move move : movableSquares) {
                Location movingTo = move.getMovingTo();
                enableSquare(movingTo, true);
            }
        }
    }

    /**
     * Enable all squares.
     *
     * @param bool should be enabled
     */
    public void enableAllSquares(boolean bool) {
        for (BoardButton[] row : this) {
            for (BoardButton btn : row) {
                btn.setEnabled(bool);
            }
        }
    }

    /**
     * Enable pieces of given color.
     * goes through the board and enables every button with a piece of the given color
     *
     * @param clr the clr
     */
    public void enablePieces(PlayerColor clr) {
        syncAction(() -> {
            for (var row : this) {
                for (var btn : row) {
                    btn.setEnabled(btn.getPiece() != null && btn.getPiece().isOnMyTeam(clr));
                }
            }
            sidePanel.moveLog.resetCurrentBoard();
        });
    }

    /**
     * Sync action .
     *
     * @param synchronizedAction the synchronized action
     */
    public void syncAction(VoidCallback synchronizedAction) {
        synchronized (boardLock) {
            synchronizedAction.callback();

        }
    }

    /**
     * sets a location In check.
     *
     * @param kingLoc the king loc
     */
    public void inCheck(Location kingLoc) {
        getBtn(kingLoc).setAsCheck();
    }

    /**
     * unselects all selected buttons.
     */
    public void resetSelectedButtons() {
        boardPnl.forEachBtnParallel(b -> b.removeState(State.SELECTED));
    }

    /**
     * set a button to be the current button the player clicked on
     *
     * @param loc the loc
     */
    public void setCurrentPiece(Location loc) {
        getBtn(loc).setAsCurrent();
    }

    /**
     * Draws an arrow.
     *
     * @param from the from
     * @param loc  the loc
     * @param clr  the clr
     */
    public void drawArrow(Location from, Location loc, Color clr) {
        boardPnl.getBoardOverlay().drawArrow(from, loc, clr);
    }

    @Override
    public Iterator<BoardButton[]> iterator() {
        return Arrays.stream(boardPnl.getBtnMat()).parallel().iterator();
    }

    /**
     * Updates the board by move.
     *
     * @param move the move
     */
    public void updateByMove(Move move) {
        syncAction(() -> {
            enableAllSquares(false);
//            sidePanel.moveLog.preAdding();
            makeMove(move);
//            boardPnl.resizeIcons();
            sidePanel.moveLog.addMove(move);
        });
    }

    /**
     * Make move.
     *
     * @param move the move
     */
    public void makeMove(Move move) {
        makeBasicMove(move.getIntermediateMove());
        makeBasicMove(move);
        boardPnl.newPosition();
    }

    /**
     * Make basic move.
     *
     * @param basicMove the basic move
     */
    public void makeBasicMove(BasicMove basicMove) {
        if (basicMove == null)
            return;

        syncAction(() -> {
            BoardButton prevBtn = getBtn(basicMove.getMovingFrom());
            BoardButton newBtn = getBtn(basicMove.getMovingTo());
            newBtn.setPiece(prevBtn);
            prevBtn.reset();

        });
    }

    /**
     * Ask the player a question.
     *
     * @param question the question
     * @param callback callback to when the player selects one of the answers
     */
    public void askQuestion(Question question, AnswerCallback callback) {
        sidePanel.askPlayerPnl.ask(question, callback);
        SwingUtilities.invokeLater(win::pack);
    }

    /**
     * Gets side panel.
     *
     * @return the side panel
     */
    public SidePanel getSidePanel() {
        return sidePanel;
    }

    /**
     * Game over.
     *
     * @param gameOverStatus the game over status
     */
    public void gameOver(GameStatus gameOverStatus) {
        sidePanel.stopRunningTime();
        sidePanel.askPlayerPnl.removeQuestion(Question.QuestionType.DRAW_OFFER);

        enableAllSquares(false);
        setStatusLbl("Game Over " + gameOverStatus.getDetailedStr(client.getPlayerUsernames()), statusLblHighlightClr);
        sidePanel.enableBtns(false);
    }

    /**
     * Sets status lbl.
     *
     * @param str the str
     * @param fg  the fg
     */
    public void setStatusLbl(String str, Color fg) {
        if (str == null || str.equals(""))
            str = " ";
        else
            str = StrUtils.format(str);
        statusLbl.setForeground(fg);
        statusLbl.setText(str);
    }

    /**
     * Show a db response from the server.
     *
     * @param response     the db response
     * @param respondingTo what is the server responding to
     * @param title        the title
     */
    public void showDBResponse(DBResponse response, String respondingTo, String title) {
        if (response == null)
            return;
        WinPnl pnl = new WinPnl();
        respondingTo = StrUtils.format(respondingTo);

        DBResponse currentRes = response;
        while (currentRes != null) {
            pnl.add(createDBResponseComponent(currentRes));
            currentRes = currentRes.getAddedRes();
        }

        DialogProperties dialogProperties = client.dialogProperties(respondingTo, title + " | " + response.getStatus());

        showDialog(new SimpleDialog(dialogProperties, pnl));
    }

    /**
     * Creates a component to display a database response
     *
     * @param _response the response
     * @return the j component
     */
    private JComponent createDBResponseComponent(DBResponse _response) {
        if (_response instanceof TableDBResponse response) {
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
            }};
            int spacing = -25;
            Scrollable.applyDefaults(scrollPane, table.getComputedSize().padding(spacing));
            return new WinPnl(header) {{
                add(scrollPane);

            }};
        } else if (_response instanceof GraphableDBResponse response) {
            return Graph.createGraph(response);
        } else if (_response instanceof StatusResponse response) {
            return MessageCard.createMsgPnl(response.getDetails(), response.getStatus() == DBResponse.Status.SUCCESS ? MessageType.INFO : MessageType.ERROR, new Size(400));
        }
        return null;
    }

    /**
     * Dispose. closes the main window and all open dialogs
     */
    public void dispose() {
        win.dispose();
        closeOpenDialogs();
    }

    /**
     * Connected to server. once the client connects to the server, all the synced lists need to registered, so they can be synced later
     */
    public void connectedToServer() {
        for (SyncableList list : listsToRegister) {
            client.getMessagesHandler().registerSyncableList(list);
        }
    }

    /**
     * Colors a move's source and destination, to clarify what was the last move played.
     *
     * @param move the move
     */
    public void colorMove(Move move) {
        syncAction(() -> {
            getBtn(move.getMovingFrom()).movingFrom();
            getBtn(move.getMovingTo()).movingTo();
        });
    }


}
