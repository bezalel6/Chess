package ver5.view.dialogs.game_select.Panels;

import ver5.SharedClasses.GameSettings;
import ver5.view.dialogs.Navigation.BackOkInterface;
import ver5.view.dialogs.WinPnl;
import ver5.view.dialogs.game_select.GameCreatePnl;
import ver5.view.dialogs.game_select.GameSelect;
import ver5.view.dialogs.game_select.buttons.SelectablesList;
import ver5.view.dialogs.game_select.selectable.SelectableAiDifficulty;
import ver5.view.dialogs.game_select.selectable.SelectableAiType;

public class AiSettings extends WinPnl {
    public AiSettings(GameSelect gameSelect, GameCreatePnl newGameCreatePnl) {
        super("ai settings");
        GameSettings gameSettings = gameSelect.getGameSettings();
        initBackOk(new BackOkInterface() {
            @Override
            public void onBack() {
                newGameCreatePnl.aiOptionPnl.setCheckBox(false);
                gameSelect.switchTo(newGameCreatePnl);
            }

            @Override
            public void onOk() {
                gameSelect.switchTo(newGameCreatePnl);
            }
        });

        add(new SelectablesList(SelectableAiType.selectableAiTypes(), "ai type", null, selected -> {
            gameSettings.getAiParameters().setAiType(((SelectableAiType) selected).aiType);
        }, gameSelect, false));
        add(new SelectablesList(SelectableAiDifficulty.values(), "ai difficulty", null, selected -> {
            gameSettings.getAiParameters().setMoveSearchTimeout(((SelectableAiDifficulty) selected).timeFormat);
        }, gameSelect, false));
        doneAdding();
    }
}
