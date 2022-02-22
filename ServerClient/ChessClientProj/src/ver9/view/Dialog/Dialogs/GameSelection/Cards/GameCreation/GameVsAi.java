package ver9.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver9.SharedClasses.GameSettings;
import ver9.SharedClasses.GameSetup.AiParameters;
import ver9.view.Dialog.Cards.CardHeader;
import ver9.view.Dialog.Dialog;
import ver9.view.Dialog.Dialogs.GameSelection.Components.AiDifficulties;
import ver9.view.Dialog.Dialogs.GameSelection.Components.AiTypes;

public class GameVsAi extends GameCreationCard {
    private final AiParameters aiParameters;

    public GameVsAi(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Game vs Ai"), parentDialog, gameSettings);
        aiParameters = new AiParameters();
        addDialogComponent(new AiTypes(this, aiParameters));
        addDialogComponent(new AiDifficulties(this, aiParameters));
    }

    @Override
    public void onBack() {
        gameSettings.setAiParameters(null);
        super.onBack();
    }

    @Override
    public void onOk() {
        gameSettings.setAiParameters(aiParameters);
        super.onBack();
    }
}
