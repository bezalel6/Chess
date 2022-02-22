package ver8.view.Wishfull.dialogs.GameSelect;

import ver8.SharedClasses.FontManager;
import ver8.SharedClasses.GameSettings;
import ver8.view.Wishfull.List.SelfContainedList;
import ver8.view.Wishfull.dialogs.DialogComponent;
import ver8.view.Wishfull.dialogs.GameSelect.Buttons.SelectablesList;
import ver8.view.Wishfull.dialogs.GameSelect.OptionalFields.AiOptionPnl;
import ver8.view.Wishfull.dialogs.GameSelect.OptionalFields.FenOptionPnl;
import ver8.view.Wishfull.dialogs.GameSelect.Panels.AiSettings;
import ver8.view.Wishfull.dialogs.GameSelect.Panels.FenPnl;
import ver8.view.Wishfull.dialogs.GameSelect.Selectables.SelectablePlayerColor;
import ver8.view.Wishfull.dialogs.GameSelect.Selectables.SelectableTimeFormat;
import ver8.view.Wishfull.dialogs.WinPnl;

public class GameCreatePnl extends SelfContainedList implements DialogComponent {
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
        aiOptionPnl = new AiOptionPnl(() -> gameSelect.navTo(aiSettingsPnl));

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
        container.addNav(fenPnl);
        fenOptionPnl = (new FenOptionPnl(() -> gameSelect.navTo((fenPnl))));
        container.add(fenOptionPnl);

        doneAdding();
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
    public Parent parent() {
        return parent;
    }

    @Override
    public WinPnl navTo() {
        return container;
    }

    @Override
    public void doneAdding() {
        //todo ↓
//        container.add(aiOptionPnl);
        super.doneAdding();
    }
}
