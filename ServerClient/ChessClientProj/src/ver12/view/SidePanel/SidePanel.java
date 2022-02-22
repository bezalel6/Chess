package ver12.view.SidePanel;

import ver12.SharedClasses.FontManager;
import ver12.SharedClasses.GameTime;
import ver12.SharedClasses.PlayerColor;
import ver12.SharedClasses.Utils.StrUtils;
import ver12.SharedClasses.ui.MyJButton;
import ver12.SharedClasses.ui.MyLbl;
import ver12.Client;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicLong;

public class SidePanel extends JPanel {
    static final Font font = FontManager.sidePanel;
    public final AskPlayer askPlayerPnl;
    public final MoveLog moveLog;
    private final MyLbl[] timeLbls;
    private final JPanel white, black;
    private final MyJButton resignBtn;
    private final MyJButton offerDrawBtn;
    private long timeControl;
    private PlayerColor currentlyRunningClr;
    private Timer currentTimer;
    private long currentRunMillis;

    public SidePanel(long millis, boolean isFlipped, Client client) {
        this.currentlyRunningClr = null;
        this.timeControl = millis;
        String timeControlStr = StrUtils.createTimeStr(millis);
        timeLbls = new MyLbl[2];
        for (int i = 0; i < timeLbls.length; i++) {
            timeLbls[i] = new MyLbl(timeControlStr);
            timeLbls[i].setFont(font);
        }
        resignBtn = new MyJButton("Resign", font, client::resignBtnClicked);
        offerDrawBtn = new MyJButton("Offer Draw", font, client::offerDrawBtnClicked);

        setLayout(new GridBagLayout());

        white = createTimerPnl("White", timeLbls[PlayerColor.WHITE.ordinal()]);
        black = createTimerPnl("Black", timeLbls[PlayerColor.BLACK.ordinal()]);

        moveLog = new MoveLog();

        askPlayerPnl = new AskPlayer();

        addLayout(isFlipped);

        enableBtns(false);
    }

    public JPanel createTimerPnl(String str, MyLbl timerLbl) {
        return new JPanel() {{
            MyLbl lbl = new MyLbl(str);
            lbl.setFont(font);
            setLayout(new BorderLayout());
            add(lbl, BorderLayout.NORTH);
            add(timerLbl, BorderLayout.SOUTH);
        }};
    }

    private void addLayout(boolean isFlipped) {
        removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        int wY, bY;
        int bottomY = 6;
        if (isFlipped) {
            wY = 0;
            bY = bottomY;
        } else {
            wY = bottomY;
            bY = 0;
        }

        gbc.gridx = 0;
        gbc.gridy = bY;
        add(black, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(resignBtn, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(offerDrawBtn, gbc);

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
        add(white, gbc);

        gbc.gridx = 0;
        gbc.gridy = bottomY + 3;
        gbc.gridwidth = GridBagConstraints.NONE;
        add(askPlayerPnl, gbc);

    }

    public void enableBtns(boolean enable) {
        resignBtn.setEnabled(enable);
        offerDrawBtn.setEnabled(enable);
    }


    public void setFlipped(boolean isFlipped) {
        addLayout(isFlipped);
    }

    public void reset(GameTime gameTime) {
        sync(gameTime);
        moveLog.reset();
        askPlayerPnl.showPnl(false);
    }

    public void sync(GameTime gameTime) {
        setBothPlayersClocks(gameTime);
    }

    public void setBothPlayersClocks(GameTime gameTime) {
        System.out.println("setting game time:" + gameTime);
        if (gameTime == null)
            return;
        timeControl = gameTime.getRunningTime(PlayerColor.WHITE).getTimeInMillis();
        for (PlayerColor player : PlayerColor.PLAYER_COLORS) {
            setTimerLabel(player, gameTime.getTimeLeft(player));
        }
    }

    public void setTimerLabel(PlayerColor player, long millis) {
        String str = StrUtils.createTimeStr(millis);
        timeLbls[player.asInt()].setText(str);
    }

    public void syncAndStartTimer(GameTime gameTime) {
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
