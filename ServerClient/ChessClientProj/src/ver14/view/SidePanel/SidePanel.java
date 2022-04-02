package ver14.view.SidePanel;

import ver14.Client;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.Game.GameTime;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.ui.MyJButton;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SidePanel extends JPanel {
    static final Font font = FontManager.sidePanel;
    public final AskPlayer askPlayerPnl;
    public final MoveLog moveLog;
    final Client client;
    private final TimerPnl bottom, top;
    private final GameActions gameActions;
    private final Map<PlayerColor, TimerPnl> timersMap = new HashMap<>();
    private final Map<PlayerColor, String> usernames = new HashMap<>() {{
        for (PlayerColor clr : PlayerColor.PLAYER_COLORS) {
            put(clr, clr.getName());
        }
    }};
    private long timeControl;
    private PlayerColor currentlyRunningClr;
    private Timer currentTimer;
    private long currentRunMillis;

    public SidePanel(long millis, boolean isFlipped, Client client) {
        this.client = client;
        this.currentlyRunningClr = null;
        this.timeControl = millis;
        this.gameActions = new GameActions(this);

        setLayout(new GridBagLayout());

        bottom = new TimerPnl("White");
        top = new TimerPnl("Black");

        moveLog = new MoveLog();

        askPlayerPnl = new AskPlayer();
        setFlipped(isFlipped);

        enableBtns(false);
    }

    public void setFlipped(boolean isFlipped) {
        synchronized (timersMap) {
            PlayerColor base = isFlipped ? PlayerColor.BLACK : PlayerColor.WHITE;
            timersMap.put(base, bottom);
            timersMap.put(base.getOpponent(), top);
            namesSync();
        }
        addLayout();
    }

    public void enableBtns(boolean enable) {
        gameActions.enableBtns(enable);
    }

    private void namesSync() {
        for (PlayerColor clr : PlayerColor.PLAYER_COLORS) {
            timersMap.get(clr).setName(usernames.get(clr));
        }
    }

    private void addLayout() {
        removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        int wY, bY;
        int bottomY = 6;
//        if (isFlipped) {
//            wY = 0;
//            bY = bottomY;
//        } else {
        wY = bottomY;
        bY = 0;
//        }

        gbc.gridx = 0;
        gbc.gridy = bY;
        add(top, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(gameActions, gbc);

//        gbc.gridx = 2;
//        gbc.gridy = 1;
//        add(addTimeBtn, gbc);

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
        gbc.gridwidth = GridBagConstraints.NONE;
        add(askPlayerPnl, gbc);

    }

    MyJButton createBtn(String text, VoidCallback onClick) {
        return new MyJButton(text, font, onClick) {{
            setFocusable(false);
        }};

    }

    public void syncAndStartTimer(GameTime gameTime) {
        setBothPlayersClocks(gameTime);
    }

    public void setBothPlayersClocks(GameTime gameTime) {
        if (gameTime == null)
            return;
        timeControl = gameTime.getRunningTime(PlayerColor.WHITE).getTimeInMillis();
        for (PlayerColor player : PlayerColor.PLAYER_COLORS) {
            setTimerLabel(player, gameTime.getTimeLeft(player));
        }
    }

    public void setTimerLabel(PlayerColor player, long millis) {
        getTimerPnl(player).setTimer(millis);
    }

    protected TimerPnl getTimerPnl(PlayerColor clr) {
        synchronized (timersMap) {
            return timersMap.get(clr);
        }
    }

    public void initGame(PlayerColor myClr, String myUn, String oppUn, GameTime gameTime) {
        synchronized (usernames) {
            usernames.put(myClr, myUn);
            usernames.put(myClr.getOpponent(), oppUn);
        }
        namesSync();
        reset(gameTime);
        enableBtns(true);
    }

    public void reset(GameTime gameTime) {
        sync(gameTime);
        moveLog.reset();
        askPlayerPnl.showPnl(false);
    }

    public void sync(GameTime gameTime) {
        setBothPlayersClocks(gameTime);
    }

    public void startRunning(PlayerColor clr) {
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

    public void stopRunningTime() {
        if (currentTimer != null) {
            currentTimer.stop();
        }
        currentlyRunningClr = null;
    }

}
