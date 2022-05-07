package ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.DialogFields.Slider.TimeFormatSlider;
import ver14.view.Dialog.Dialogs.GameSelection.Components.AiTypes;

/**
 * Game vs ai card.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class GameVsAi extends GameCreationCard {
    /**
     * The Ai parameters.
     */
    private final AiParameters aiParameters;

    /**
     * Instantiates a new Game vs ai.
     *
     * @param parentDialog the parent dialog
     * @param gameSettings the game settings
     */
    public GameVsAi(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Game vs Ai"), parentDialog, gameSettings);
        aiParameters = new AiParameters();
        addDialogComponent(new AiTypes(this, aiParameters));
        addDialogComponent(new TimeFormatSlider(this, aiParameters) {{
            setToMinValue();
        }});
    }

    protected void setEnabledState(boolean state) {
        gameSettings.setAiParameters(state ? aiParameters : null);
        checkbox.setState(state);
        checkbox.setVisible(state);
    }


}
