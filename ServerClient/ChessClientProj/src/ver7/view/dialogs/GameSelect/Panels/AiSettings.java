package ver7.view.dialogs.GameSelect.Panels;

import ver7.SharedClasses.GameSettings;
import ver7.view.dialogs.Navigation.BackOkInterface;
import ver7.view.dialogs.WinPnl;
import ver7.view.dialogs.GameSelect.GameCreatePnl;
import ver7.view.dialogs.GameSelect.GameSelect;
import ver7.view.dialogs.GameSelect.Buttons.SelectablesList;
import ver7.view.dialogs.GameSelect.Selectables.SelectableAiDifficulty;
import ver7.view.dialogs.GameSelect.Selectables.SelectableAiType;

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
