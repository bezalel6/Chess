package ver7.view.dialogs.GameSelect;

import ver7.SharedClasses.FontManager;
import ver7.SharedClasses.GameSettings;
import ver7.view.List.SelfContainedList;
import ver7.view.dialogs.WinPnl;
import ver7.view.dialogs.GameSelect.OptionalFields.AiOptionPnl;
import ver7.view.dialogs.GameSelect.OptionalFields.FenOptionPnl;
import ver7.view.dialogs.GameSelect.Panels.AiSettings;
import ver7.view.dialogs.GameSelect.Panels.FenPnl;
import ver7.view.dialogs.GameSelect.Buttons.SelectablesList;
import ver7.view.dialogs.GameSelect.Selectables.SelectablePlayerColor;
import ver7.view.dialogs.GameSelect.Selectables.SelectableTimeFormat;

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
//        container.addNav(fenPnl);
        fenOptionPnl = (new FenOptionPnl(() -> gameSelect.switchTo((WinPnl) fenPnl)));
        container.add(fenOptionPnl);

        doneAdding();
    }

    @Override
    public void doneAdding() {
        container.add(aiOptionPnl);
        super.doneAdding();
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
