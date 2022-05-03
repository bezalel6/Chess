package ver14.view.SidePanel;

import ver14.Client;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Game.GameSetup.GameTime;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.SharedClasses.UI.FontManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Side panel - represents the side panel of the main {@link ver14.view.View} window. containing the {@link TimerPnl}s, {@link MoveLog} and {@link GameActions}.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class SidePanel extends JPanel {
    /**
     * The Font.
     */
    static final Font font = FontManager.sidePanel;
    /**
     * The Ask player pnl.
     */
    public final AskPlayer askPlayerPnl;
    /**
     * The Move log.
     */
    public final MoveLog moveLog;
    /**
     * The Client.
     */
    final Client client;
    /**
     * The Bottom.
     */
    private final TimerPnl bottom, /**
     * The Top.
     */
    top;
    /**
     * The Game actions.
     */
    private final GameActions gameActions;
    /**
     * The Timers map.
     */
    private final Map<PlayerColor, TimerPnl> timersMap = new HashMap<>();
    /**
     * The Usernames.
     */
    private final Map<PlayerColor, String> usernames = new HashMap<>() {{
        for (PlayerColor clr : PlayerColor.PLAYER_COLORS) {
            put(clr, clr.getName());
        }
    }};
    /**
     * The Time control.
     */
    private long timeControl;
    /**
     * The Currently running clr.
     */
    private PlayerColor currentlyRunningClr;
    /**
     * The Current timer.
     */
    private Timer currentTimer;
    /**
     * The Current run millis.
     */
    private long currentRunMillis;

    /**
     * Instantiates a new Side panel.
     *
     * @param millis    the millis
     * @param isFlipped the is flipped
     * @param client    the client
     */
    public SidePanel(long millis, boolean isFlipped, Client client) {
        this.client = client;
        this.currentlyRunningClr = null;
        this.timeControl = millis;
        this.gameActions = new GameActions(this);

        setLayout(new GridBagLayout());

        bottom = new TimerPnl("White", this);
        top = new TimerPnl("Black", this);

        moveLog = new MoveLog();

        askPlayerPnl = new AskPlayer();
        setFlipped(isFlipped);

        enableBtns(false);
    }

    /**
     * Sets flipped.
     *
     * @param isFlipped is flipped
     */
    public void setFlipped(boolean isFlipped) {
        synchronized (timersMap) {
            PlayerColor base = isFlipped ? PlayerColor.BLACK : PlayerColor.WHITE;
            timersMap.put(base, bottom);
            timersMap.put(base.getOpponent(), top);
            namesSync();
        }
        addLayout();
    }

    /**
     * Enable btns.
     *
     * @param enable the enable
     */
    public void enableBtns(boolean enable) {
        gameActions.enableBtns(enable);
    }

    /**
     * Names sync.
     */
    private void namesSync() {
        for (PlayerColor clr : PlayerColor.PLAYER_COLORS) {
            timersMap.get(clr).setName(usernames.get(clr));
        }
    }

    /**
     * Add layout.
     */
    private void addLayout() {
        removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        int wY, bY;
        int bottomY = 6;
        wY = bottomY;
        bY = 0;

        gbc.gridx = 0;
        gbc.gridy = bY;
        add(top, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(gameActions, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 10;
        gbc.weighty = 5;
        add(moveLog, gbc);

        gbc.gridx = 0;
        gbc.gridy = wY;
        gbc.weighty = 0;
        gbc.gridheight = 2;
        add(bottom, gbc);

        gbc.gridx = 0;
        gbc.gridy = bottomY + 3;
        gbc.weighty = 4;
//        gbc.gridwidth = GridBagConstraints.NONE;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        add(askPlayerPnl, gbc);

    }

    /**
     * Create btn my j button.
     *
     * @param text    the text
     * @param onClick the on click
     * @return the my j button
     */
    MyJButton createBtn(String text, VoidCallback onClick) {
        return new MyJButton(text, font, onClick) {{
            setFocusable(false);
        }};


    }

    /**
     * Sync and start timer.
     *
     * @param gameTime the game time
     */
    public void syncAndStartTimer(GameTime gameTime) {
        setBothPlayersClocks(gameTime);
    }

    /**
     * Sets both players clocks.
     *
     * @param gameTime the game time
     */
    public void setBothPlayersClocks(GameTime gameTime) {
        if (gameTime == null)
            return;
        timeControl = gameTime.getRunningTime(PlayerColor.WHITE).getTimeInMillis();
        for (PlayerColor player : PlayerColor.PLAYER_COLORS) {
            setTimerLabel(player, gameTime.getTimeLeft(player));
        }
    }

    /**
     * Sets timer label.
     *
     * @param player the player
     * @param millis the millis
     */
    public void setTimerLabel(PlayerColor player, long millis) {
        getTimerPnl(player).setTimer(millis);
    }

    /**
     * Gets timer pnl.
     *
     * @param clr the clr
     * @return the timer pnl
     */
    protected TimerPnl getTimerPnl(PlayerColor clr) {
        synchronized (timersMap) {
            return timersMap.get(clr);
        }
    }

    /**
     * Init game.
     *
     * @param myClr    the color this client is playing as
     * @param myUn     this client's username
     * @param oppUn    the opponent's username
     * @param gameTime the game time
     */
    public void initGame(PlayerColor myClr, String myUn, String oppUn, GameTime gameTime) {
        synchronized (usernames) {
            usernames.put(myClr, myUn);
            usernames.put(myClr.getOpponent(), oppUn);
        }
        namesSync();
        reset(gameTime);
        enableBtns(true);
    }

    /**
     * Reset side panel.
     *
     * @param gameTime the game time
     */
    public void reset(GameTime gameTime) {
        sync(gameTime);
        moveLog.reset();
//        askPlayerPnl.showPnl(false);
    }

    /**
     * Sync timers.
     *
     * @param gameTime the game time
     */
    public void sync(GameTime gameTime) {
        setBothPlayersClocks(gameTime);
    }

    /**
     * Start running a player's clock.
     *
     * @param clr the clr
     */
    public void startRunning(PlayerColor clr) {
        stopRunningTime();
        currentRunMillis = timeControl;
        currentlyRunningClr = clr;
        int delay = 100;
        AtomicLong last = new AtomicLong(System.currentTimeMillis());
        currentTimer = new Timer(delay, e -> {
            long current = System.currentTimeMillis();
            if (currentRunMillis > 0)
                currentRunMillis -= current - last.get();

            last.set(current);

            setTimerLabel(clr, currentRunMillis);
        });
        currentTimer.start();
    }

    /**
     * Stop currently running time.
     */
    public void stopRunningTime() {
        if (currentTimer != null) {
            currentTimer.stop();
        }
        currentlyRunningClr = null;
    }

    /**
     * Gets game actions.
     *
     * @return the game actions
     */
    public GameActions getGameActions() {
        return gameActions;
    }

}
