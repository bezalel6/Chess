package ver10.view.Dialog.Dialogs.GameSelection.Components;

import ver10.SharedClasses.GameSettings;
import ver10.SharedClasses.RegEx;
import ver10.view.Dialog.Components.Parent;
import ver10.view.Dialog.DialogFields.TextBasedFields.TextField;

public class FenField extends TextField {
    private final GameSettings gameSettings;

    public FenField(Parent parent, GameSettings gameSettings) {
        super("Enter Fen", parent, RegEx.Fen);
        this.gameSettings = gameSettings;
    }

    @Override
    protected void onUpdate() {
        gameSettings.setFen(getValue());
        super.onUpdate();
    }

    @Override
    public String errorDetails() {
        return "fen is not legal";
    }
}
