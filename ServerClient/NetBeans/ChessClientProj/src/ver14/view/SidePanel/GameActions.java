package ver14.view.SidePanel;

import ver14.SharedClasses.ui.MyJButton;

import javax.swing.*;
import java.awt.*;
import ver14.SharedClasses.Callbacks.VoidCallback;

class GameActions extends JPanel {
    private final SidePanel sidePanel;
    private final MyJButton resignBtn;
    private final MyJButton offerDrawBtn;

    public GameActions(SidePanel sidePanel) {
        this.sidePanel = sidePanel;
        setLayout(new GridLayout(1, 0));
        resignBtn = sidePanel.createBtn("Resign", sidePanel.client::resignBtnClicked);
        
        offerDrawBtn = sidePanel.createBtn("Offer Draw", new VoidCallback() {
            @Override
            public void callback() {
                offerDrawBtn.setEnabled(false);
                sidePanel.client.offerDrawBtnClicked();
            }
        });
        add(offerDrawBtn);
        add(resignBtn);
    }

    public void enableBtns(boolean e) {
        resignBtn.setEnabled(e);
        offerDrawBtn.setEnabled(e);
    }
}
