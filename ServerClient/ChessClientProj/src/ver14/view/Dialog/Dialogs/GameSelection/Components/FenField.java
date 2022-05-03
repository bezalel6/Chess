package ver14.view.Dialog.Dialogs.GameSelection.Components;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Utils.RegEx;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.DialogFields.TextBasedFields.TextField;

/**
 * a Fen field. represents a string representation of a string.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class FenField extends TextField {
    /**
     * The Game settings.
     */
    private final GameSettings gameSettings;

    /**
     * Instantiates a new Fen field.
     *
     * @param parent       the parent
     * @param gameSettings the game settings
     */
    public FenField(Parent parent, GameSettings gameSettings) {
        super("Enter Fen", parent, RegEx.Fen);
        this.gameSettings = gameSettings;
    }

    /**
     * Sets fen.
     *
     * @param fen the fen
     */
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
