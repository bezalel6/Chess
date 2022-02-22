package ver8.view.OldDialogs.GameSelect;

import ver8.SharedClasses.FontManager;
import ver8.SharedClasses.GameSettings;
import ver8.view.List.SelfContainedListOld;
import ver8.view.OldDialogs.GameSelect.OptionalFields.AiOptionPnl;
import ver8.view.OldDialogs.GameSelect.OptionalFields.FenOptionPnl;
import ver8.view.OldDialogs.GameSelect.Panels.AiSettings;
import ver8.view.OldDialogs.GameSelect.Panels.FenPnl;
import ver8.view.OldDialogs.GameSelect.buttons.SelectablesListOld;
import ver8.view.OldDialogs.GameSelect.selectable.SelectablePlayerColor;
import ver8.view.OldDialogs.GameSelect.selectable.SelectableTimeFormat;

public class GameCreatePnl extends SelfContainedListOld {
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
        container.addList(new SelectablesListOld(SelectablePlayerColor.values(),
                "color",
                SelectablePlayerColor.RANDOM,
                selected -> {
                    gameSettings.setPlayerToMove(((SelectablePlayerColor) selected).playerColor);
                },
                gameSelect));
        container.addList(new SelectablesListOld(SelectableTimeFormat.values(),
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

    @Override
    public boolean verify() {
        return false;
    }
}
