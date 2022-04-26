package ver14.view.SidePanel;

import ver14.SharedClasses.UI.Buttons.MyJButton;

import javax.swing.*;
import java.awt.*;

/**
 * Game actions - .
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class GameActions extends JPanel {
    private final SidePanel sidePanel;
    private final MyJButton resignBtn;
    private final MyJButton offerDrawBtn;

    /**
     * Instantiates a new Game actions.
     *
     * @param sidePanel the side panel
     */
    public GameActions(SidePanel sidePanel) {
        this.sidePanel = sidePanel;
        setLayout(new GridLayout(0, 2));
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
//        offerDrawBtn.replaceWithCancel(() -> {
//            sidePanel.client.cancelQuestion(Question.QuestionType.DRAW_OFFER);
//            offerDrawBtn.resetState(true);
//        });
        offerDrawBtn.setEnabled(false);
        sidePanel.client.offerDrawBtnClicked();
    }

    /**
     * Enable btns.
     *
     * @param e the e
     */
    public void enableBtns(boolean e) {
        resignBtn.resetState(e);
        offerDrawBtn.resetState(e);
    }

    /**
     * Enable draw offer btn boolean.
     *
     * @param e the e
     * @return was the draw offer btn enabled before
     */
    public boolean enableDrawOfferBtn(boolean e) {
        boolean b = offerDrawBtn.isEnabled();
        offerDrawBtn.setEnabled(e);
        return b;
    }
}
