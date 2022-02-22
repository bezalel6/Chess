package ver5.view.dialogs.game_select;

import ver5.SharedClasses.FontManager;
import ver5.SharedClasses.GameSettings;
import ver5.view.dialogs.Navigation.BackOkInterface;
import ver5.view.dialogs.WinPnl;
import ver5.view.dialogs.game_select.OptionalFields.AiOptionPnl;
import ver5.view.dialogs.game_select.OptionalFields.FenOptionPnl;
import ver5.view.dialogs.game_select.Panels.AiSettings;
import ver5.view.dialogs.game_select.Panels.Fen;
import ver5.view.dialogs.game_select.buttons.SelectablesList;
import ver5.view.dialogs.game_select.selectable.SelectablePlayerColor;
import ver5.view.dialogs.game_select.selectable.SelectableTimeFormat;

public class GameCreatePnl extends WinPnl {
    public final AiOptionPnl aiOptionPnl;
    public final FenOptionPnl fenOptionPnl;
    private final AiSettings aiSettingsPnl;
    private final Fen fenPnl;

    public GameCreatePnl(GameSelect gameSelect) {
        super("select game settings");
        GameSettings gameSettings = gameSelect.getGameSettings();
        aiSettingsPnl = new AiSettings(gameSelect, this);
        aiOptionPnl = new AiOptionPnl(() -> gameSelect.switchTo(aiSettingsPnl));
        initBackOk(new BackOkInterface() {
            @Override
            public void onBack() {
                gameSelect.switchToStart();
            }

            @Override
            public void onOk() {
                if (!GameCreatePnl.this.aiOptionPnl.isCheckBox()) {
                    gameSettings.setAiParameters(null);
                }
                gameSettings.setGameType(GameSettings.GameType.CREATE_NEW);
                gameSelect.done();
            }
        });
        add(new SelectablesList(SelectablePlayerColor.values(), "color", SelectablePlayerColor.RANDOM, selected -> {
            gameSettings.setPlayerColor(((SelectablePlayerColor) selected).playerColor);
            setFont(FontManager.loginProcess);
        }, gameSelect));
        add(new SelectablesList(SelectableTimeFormat.values(), "time format", null, selected -> {
            gameSettings.setTimeFormat(((SelectableTimeFormat) selected).timeFormat);
        }, gameSelect));

        fenPnl = new Fen(gameSelect, this);
        fenOptionPnl = (new FenOptionPnl(() -> gameSelect.switchTo(fenPnl)));
        add(fenOptionPnl);

        doneAdding();
    }

    @Override
    public void doneAdding() {
        add(aiOptionPnl);
        super.doneAdding();
    }

}
