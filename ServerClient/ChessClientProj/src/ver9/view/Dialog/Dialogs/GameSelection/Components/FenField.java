package ver9.view.Dialog.Dialogs.GameSelection.Components;

import ver9.SharedClasses.GameSettings;
import ver9.SharedClasses.RegEx;
import ver9.view.Dialog.Components.Parent;
import ver9.view.Dialog.DialogFields.DialogTextField;

public class FenField extends DialogTextField {
    private final GameSettings gameSettings;

    public FenField(Parent parent, GameSettings gameSettings) {
        super("Enter Fen", parent, RegEx.Fen, true);
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
