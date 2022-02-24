package ver13.view.Dialog.Dialogs.GameSelection.Components;

import ver13.SharedClasses.GameSettings;
import ver13.SharedClasses.RegEx;
import ver13.view.Dialog.Components.Parent;
import ver13.view.Dialog.DialogFields.TextBasedFields.TextField;

public class FenField extends TextField {
    private final GameSettings gameSettings;

    public FenField(Parent parent, GameSettings gameSettings) {
        super("Enter Fen", parent, RegEx.Fen);
        this.gameSettings = gameSettings;
    }

    public void setFen(String fen) {
        setValue(fen);
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
