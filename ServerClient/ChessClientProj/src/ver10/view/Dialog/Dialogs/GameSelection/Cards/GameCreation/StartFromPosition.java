package ver10.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver10.SharedClasses.GameSettings;
import ver10.view.Dialog.Cards.CardHeader;
import ver10.view.Dialog.Dialog;
import ver10.view.Dialog.Dialogs.GameSelection.Components.FenField;
import ver10.view.IconManager.GameIconsGenerator;
import ver10.view.IconManager.Size;

import javax.swing.*;

public class StartFromPosition extends GameCreationCard {
    private final JLabel iconLbl;

    public StartFromPosition(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Start From Position"), parentDialog, gameSettings);
        iconLbl = new JLabel();
        add(iconLbl);
        addDialogComponent(new FenField(this, gameSettings));
    }

    protected void changeState(boolean state) {
        if (!state)
            gameSettings.setFen(null);
        checkbox.setState(state);
        checkbox.setEnabled(state);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        Size size = new Size(getSize());
        if (!size.isValid()) {
            return;
        }
        size.multBy(0.7);
        size = Size.min(size);
        iconLbl.setIcon(GameIconsGenerator.generate(gameSettings.getFen(), gameSettings.getPlayerToMove(), size));
    }
}
