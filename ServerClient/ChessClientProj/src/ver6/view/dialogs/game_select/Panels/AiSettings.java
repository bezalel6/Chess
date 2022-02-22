package ver6.view.dialogs.game_select.Panels;

import ver6.SharedClasses.GameSettings;
import ver6.view.dialogs.Navigation.BackOkInterface;
import ver6.view.dialogs.WinPnl;
import ver6.view.dialogs.game_select.GameCreatePnl;
import ver6.view.dialogs.game_select.GameSelect;
import ver6.view.dialogs.game_select.buttons.SelectablesList;
import ver6.view.dialogs.game_select.selectable.SelectableAiDifficulty;
import ver6.view.dialogs.game_select.selectable.SelectableAiType;

import java.util.List;

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

        addList(new SelectablesList(SelectableAiType.selectableAiTypes(), "ai type", null, selected -> {
            gameSettings.getAiParameters().setAiType(((SelectableAiType) selected).aiType);
        }, gameSelect, false));
        addList(new SelectablesList(List.of(SelectableAiDifficulty.values()), "ai difficulty", null, selected -> {
            gameSettings.getAiParameters().setMoveSearchTimeout(((SelectableAiDifficulty) selected).timeFormat);
        }, gameSelect, false));
        doneAdding();
    }

//    @Override
//    public boolean verify() {
//        return false;
//    }
}
