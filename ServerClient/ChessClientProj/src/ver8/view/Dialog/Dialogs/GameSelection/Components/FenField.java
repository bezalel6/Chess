package ver8.view.Dialog.Dialogs.GameSelection.Components;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.RegEx;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.DialogFields.DialogTextField;

public class FenField extends DialogTextField {
    private final GameSettings gameSettings;

    public FenField(Dialog parent, GameSettings gameSettings) {
        super("Enter Fen", parent, RegEx.Fen, true);
        this.gameSettings = gameSettings;
    }

    @Override
    protected void onInputUpdate() {
        gameSettings.setFen(getValue());
        super.onInputUpdate();
    }

    @Override
    public String errorDetails() {
        return "fen is not legal";
    }
}
