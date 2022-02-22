package ver8.view.OldDialogs.GameSelect.Panels;

import ver8.SharedClasses.GameSettings;
import ver8.view.OldDialogs.GameSelect.GameCreatePnl;
import ver8.view.OldDialogs.GameSelect.GameSelect;
import ver8.view.OldDialogs.GameSelect.buttons.SelectablesListOld;
import ver8.view.OldDialogs.GameSelect.selectable.SelectableAiDifficulty;
import ver8.view.OldDialogs.GameSelect.selectable.SelectableAiType;
import ver8.view.OldDialogs.Navigation.BackOkInterface;
import ver8.view.OldDialogs.WinPnl;

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

        addList(new SelectablesListOld(SelectableAiType.selectableAiTypes(), "ai type", null, selected -> {
            gameSettings.getAiParameters().setAiType(((SelectableAiType) selected).aiType);
        }, gameSelect, false));
        addList(new SelectablesListOld(List.of(SelectableAiDifficulty.values()), "ai difficulty", null, selected -> {
            gameSettings.getAiParameters().setMoveSearchTimeout(((SelectableAiDifficulty) selected).timeFormat);
        }, gameSelect, false));
        doneAdding();
    }

//    @Override
//    public boolean verify() {
//        return false;
//    }
}
