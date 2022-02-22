package ver12.view.Dialog.Dialogs.GameSelection.Components;

import ver12.SharedClasses.GameSettings;
import ver12.SharedClasses.RegEx;
import ver12.view.Dialog.Components.Parent;
import ver12.view.Dialog.DialogFields.TextBasedFields.TextField;

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
