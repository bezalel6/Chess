package ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation.StartFromPosition;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.UI.LinkLabel;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation.GameCreationCard;
import ver14.view.Dialog.Dialogs.GameSelection.Components.FenField;
import ver14.view.IconManager.GameIconsGenerator;
import ver14.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Start from position - start from a custom position.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class StartFromPosition extends GameCreationCard {
    /**
     * The Icon lbl.
     */
    private final JLabel iconLbl;


    /**
     * Instantiates a new Start from position.
     *
     * @param parentDialog the parent dialog
     * @param gameSettings the game settings
     */
    public StartFromPosition(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Start From Position"), parentDialog, gameSettings);
        iconLbl = new JLabel();
        add(iconLbl);
        FenField fenField = new FenField(this, gameSettings);
        addDialogComponent(fenField);
        JPanel pnl = new JPanel(new GridLayout(0, 4, 10, 10));
        Arrays.stream(Position.values()).forEach(position -> {
            pnl.add(new LinkLabel(position.name, () -> {
                fenField.setFen(position.fen);
            }));
        });
        add(pnl);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Size(500);
    }

    protected void setEnabledState(boolean state) {
        if (!state)
            gameSettings.setFen(null);
        checkbox.setState(state);
        checkbox.setVisible(state);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        iconLbl.setIcon(GameIconsGenerator.generate(gameSettings.getFen(), gameSettings.getPlayerToMove(), new Size(250)));
    }

}
