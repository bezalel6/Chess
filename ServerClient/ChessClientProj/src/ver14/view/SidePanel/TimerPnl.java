package ver14.view.SidePanel;

import ver14.SharedClasses.UI.MyLbl;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

class TimerPnl extends JPanel {
    private static final Color tenSecondsClr = Color.RED;
    private final MyLbl timerLbl, nameLbl;
    private final SidePanel sidePanel;
    private boolean played10s = false;

    public TimerPnl(SidePanel sidePanel) {
        this("", sidePanel);
    }

    public TimerPnl(String name, SidePanel sidePanel) {
        this.nameLbl = new MyLbl(name);
        this.sidePanel = sidePanel;
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

    public void setTimer(long ms) {
        timerLbl.setText(StrUtils.createTimeStr(ms));
        if (TimeUnit.SECONDS.toMillis(10) >= ms) {
            if (!played10s) {
                played10s = true;
                sidePanel.client.soundManager.tenSeconds.play();
                timerLbl.setForeground(tenSecondsClr);
            }

        } else {
            played10s = false;
            timerLbl.setForeground(Color.black);
        }
    }

}
