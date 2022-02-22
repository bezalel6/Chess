package ver9.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver9.SharedClasses.GameSettings;
import ver9.view.Dialog.Cards.CardHeader;
import ver9.view.Dialog.Dialog;
import ver9.view.Dialog.Dialogs.GameSelection.Components.FenField;
import ver9.view.GameIconsGenerator;
import ver9.view.IconManager;

import javax.swing.*;

public class StartFromPosition extends GameCreationCard {
    private final JLabel iconLbl;

    public StartFromPosition(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Start From Position"), parentDialog, gameSettings);
        iconLbl = new JLabel();
        add(iconLbl);
        addDialogComponent(new FenField(this, gameSettings));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        IconManager.Size size = new IconManager.Size(getSize());
        size.multBy(0.7);
        iconLbl.setIcon(GameIconsGenerator.generate(gameSettings.getFen(), gameSettings.getPlayerToMove(), size));
    }

    @Override
    public void onBack() {
        gameSettings.setFen(null);
        super.onBack();
    }

    @Override
    public void onOk() {
        super.onBack();
    }
}
