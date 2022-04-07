package ver14.view.SidePanel;

import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.ui.MyLbl;

import javax.swing.*;
import java.awt.*;

class TimerPnl extends JPanel {
    private final MyLbl timerLbl, nameLbl;

    public TimerPnl() {
        this("");
    }

    public TimerPnl(String name) {
        this.nameLbl = new MyLbl(name);
        this.timerLbl = new MyLbl();
        nameLbl.setFont(SidePanel.font);
        setLayout(new BorderLayout());
        add(nameLbl, BorderLayout.NORTH);
        add(timerLbl, BorderLayout.SOUTH);
    }

    public void setName(String name) {
        nameLbl.setText(name);
    }

    public void setTimer(long ms) {
        timerLbl.setText(StrUtils.createTimeStr(ms));
    }

}
