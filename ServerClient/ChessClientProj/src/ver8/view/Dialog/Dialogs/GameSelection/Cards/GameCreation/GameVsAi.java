package ver8.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.GameSetup.AiParameters;
import ver8.view.Dialog.Cards.CardHeader;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Dialogs.GameSelection.Components.AiDifficulties;
import ver8.view.Dialog.Dialogs.GameSelection.Components.AiTypes;

public class GameVsAi extends GameCreationCard {
    private final AiParameters aiParameters;

    public GameVsAi(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Game vs Ai"), parentDialog, gameSettings);
        aiParameters = new AiParameters();
        addDialogComponent(new AiTypes(parentDialog, aiParameters));
        addDialogComponent(new AiDifficulties(parentDialog, aiParameters));
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
