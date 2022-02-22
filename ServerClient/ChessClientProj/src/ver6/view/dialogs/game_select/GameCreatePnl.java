package ver6.view.dialogs.game_select;

import ver6.SharedClasses.FontManager;
import ver6.SharedClasses.GameSettings;
import ver6.view.List.SelfContainedList;
import ver6.view.dialogs.game_select.OptionalFields.AiOptionPnl;
import ver6.view.dialogs.game_select.OptionalFields.FenOptionPnl;
import ver6.view.dialogs.game_select.Panels.AiSettings;
import ver6.view.dialogs.game_select.Panels.FenPnl;
import ver6.view.dialogs.game_select.buttons.SelectablesList;
import ver6.view.dialogs.game_select.selectable.SelectablePlayerColor;
import ver6.view.dialogs.game_select.selectable.SelectableTimeFormat;

public class GameCreatePnl extends SelfContainedList {
    public final AiOptionPnl aiOptionPnl;
    public final FenOptionPnl fenOptionPnl;
    private final AiSettings aiSettingsPnl;
    private final FenPnl fenPnl;
    private final GameSelect gameSelect;

    public GameCreatePnl(GameSelect gameSelect) {
        super(ListType.Buttons, null, "Create New Game", false, gameSelect);
        this.gameSelect = gameSelect;
        GameSettings gameSettings = gameSelect.getGameSettings();
        aiSettingsPnl = new AiSettings(gameSelect, this);
        aiOptionPnl = new AiOptionPnl(() -> gameSelect.switchTo(aiSettingsPnl));

        container.setFont(FontManager.loginProcess);
        container.addList(new SelectablesList(SelectablePlayerColor.values(),
                "color",
                SelectablePlayerColor.RANDOM,
                selected -> {
                    gameSettings.setPlayerToMove(((SelectablePlayerColor) selected).playerColor);
                },
                gameSelect));
        container.addList(new SelectablesList(SelectableTimeFormat.values(),
                "time format",
                null,
                selected -> {
                    gameSettings.setTimeFormat(((SelectableTimeFormat) selected).timeFormat);
                },
                gameSelect));

        fenPnl = new FenPnl(gameSelect, this);
        fenOptionPnl = (new FenOptionPnl(() -> gameSelect.switchTo(fenPnl)));
        container.add(fenOptionPnl);

//        container.listen

        doneAdding();
    }

    @Override
    public void doneAdding() {
        container.add(aiOptionPnl);
        super.doneAdding();
    }

    @Override
    public void onBack() {
        super.onBack();
    }

    @Override
    public void onOk() {
        GameSettings gameSettings = gameSelect.getGameSettings();

        if (!aiOptionPnl.isCheckBox()) {
            gameSettings.setAiParameters(null);
        }
        gameSettings.setGameType(GameSettings.GameType.CREATE_NEW);
        gameSelect.done();
    }
}
