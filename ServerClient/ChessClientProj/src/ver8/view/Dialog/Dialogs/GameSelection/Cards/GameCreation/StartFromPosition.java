package ver8.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver8.SharedClasses.GameSettings;
import ver8.view.Dialog.Cards.CardHeader;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Dialogs.GameSelection.Components.FenField;

import javax.swing.*;

public class StartFromPosition extends GameCreationCard {
    private final JLabel iconLbl;

    public StartFromPosition(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Start From Position"), parentDialog, gameSettings);
        iconLbl = new JLabel();
        add(iconLbl);
        addDialogComponent(new FenField(parentDialog, gameSettings));
    }

    @Override
    public void onInputUpdate() {
//        iconLbl.setIcon(GameIconsGenerator.generate(gameSettings.getFen(), gameSettings.getPlayerToMove(), getSize()));
        super.onInputUpdate();
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
