package ver14.view.SidePanel;

import ver14.SharedClasses.ui.MyJButton;

import javax.swing.*;
import java.awt.*;

class GameActions extends JPanel {
    private final SidePanel sidePanel;
    private final MyJButton resignBtn;
    private final MyJButton offerDrawBtn;

    public GameActions(SidePanel sidePanel) {
        this.sidePanel = sidePanel;
        setLayout(new GridLayout(1, 0));
        resignBtn = sidePanel.createBtn("Resign", this::resignBtnClicked);
        offerDrawBtn = sidePanel.createBtn("Offer Draw", this::offerDrawBtnCLicked);
        offerDrawBtn.setToolTipText("You can only offer draw once per game");
        add(offerDrawBtn);
        add(resignBtn);
    }

    private void resignBtnClicked() {
        resignBtn.setEnabled(false);
        sidePanel.client.resignBtnClicked();
    }

    private void offerDrawBtnCLicked() {
        offerDrawBtn.setEnabled(false);
        sidePanel.client.offerDrawBtnClicked();
    }

    public void enableBtns(boolean e) {
        resignBtn.setEnabled(e);
        offerDrawBtn.setEnabled(e);
    }
}
