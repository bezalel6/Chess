package ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.GameSelection.Components.AiDifficulties;
import ver14.view.Dialog.Dialogs.GameSelection.Components.AiTypes;

public class GameVsAi extends GameCreationCard {
    private final AiParameters aiParameters;

    public GameVsAi(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Game vs Ai"), parentDialog, gameSettings);
        aiParameters = new AiParameters();
        addDialogComponent(new AiTypes(this, aiParameters));
        addDialogComponent(new AiDifficulties(this, aiParameters));
    }

    protected void changeState(boolean state) {
        gameSettings.setAiParameters(state ? aiParameters : null);
        checkbox.setState(state);
        checkbox.setEnabled(state);
    }


}
