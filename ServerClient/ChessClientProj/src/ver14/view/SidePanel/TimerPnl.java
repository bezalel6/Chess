package ver14.view.SidePanel;

import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.UI.MyLbl;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * Timer pnl - represents a panel with a timer label that will turn red as it gets to less than 10 seconds, and will play a sound if this client has less than 10 seconds.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
class TimerPnl extends JPanel {
    /**
     * The constant tenSecondsClr.
     */
    private static final Color tenSecondsClr = Color.RED;
    /**
     * The constant makeNoise.
     */
    private static final long makeNoise = TimeUnit.SECONDS.toMillis(10);
    /**
     * The Timer lbl.
     */
    private final MyLbl timerLbl, /**
     * The Name lbl.
     */
    nameLbl;
    /**
     * The Side panel.
     */
    private final SidePanel sidePanel;
    private final PlayerColor color;
    /**
     * The Played 10 s.
     */
    private boolean played10s = false;


    /**
     * Instantiates a new Timer pnl.
     *
     * @param sidePanel the side panel
     * @param color     the {@link PlayerColor} assigned to this timer panel
     */
    public TimerPnl(SidePanel sidePanel, PlayerColor color) {
        this.nameLbl = new MyLbl(color.getName());
        this.nameLbl.setOpaque(true);
//        this.nameLbl.setBackground(BoardPanel.getClr(color));
//        this.nameLbl.setForeground(BoardPanel.getClr(color.getOpponent()));
        this.sidePanel = sidePanel;
        this.color = color;
        this.timerLbl = new MyLbl();
        nameLbl.setFont(SidePanel.font);
        timerLbl.setFont(SidePanel.font);
        setLayout(new BorderLayout());
        add(nameLbl, BorderLayout.NORTH);
        add(timerLbl, BorderLayout.SOUTH);
    }

    public void setName(String name) {
        nameLbl.setText(name);
    }

    /**
     * Sets timer time.
     *
     * @param ms the time to set in milliseconds
     */
    public void setTimer(long ms) {
        timerLbl.setText(StrUtils.createTimeStr(ms));
        if (makeNoise >= ms) {
            if (!played10s) {
                played10s = true;
                if (sidePanel.client.isMyTurn())
                    sidePanel.client.soundManager.tenSeconds.play();
                timerLbl.setForeground(tenSecondsClr);
            }

        } else {
            played10s = false;
            timerLbl.setForeground(Color.black);
        }
    }

}
