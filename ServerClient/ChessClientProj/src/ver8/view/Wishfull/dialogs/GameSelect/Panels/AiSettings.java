package ver8.view.Wishfull.dialogs.GameSelect.Panels;

import ver8.SharedClasses.GameSettings;
import ver8.view.Wishfull.dialogs.GameSelect.Buttons.SelectablesList;
import ver8.view.Wishfull.dialogs.GameSelect.Selectables.SelectableAiDifficulty;
import ver8.view.Wishfull.dialogs.GameSelect.Selectables.SelectableAiType;
import ver8.view.Wishfull.dialogs.GameSelect.GameCreatePnl;
import ver8.view.Wishfull.dialogs.GameSelect.GameSelect;
import ver8.view.Wishfull.dialogs.Navigation.BackOkInterface;
import ver8.view.Wishfull.dialogs.Navigation.Navable;
import ver8.view.Wishfull.dialogs.WinPnl;

import java.util.List;

public class AiSettings extends WinPnl implements Navable {
    public AiSettings(GameSelect gameSelect, GameCreatePnl newGameCreatePnl) {
        super("ai settings");
        GameSettings gameSettings = gameSelect.getGameSettings();
        initBackOk(new BackOkInterface() {
            @Override
            public void onBack() {
                newGameCreatePnl.aiOptionPnl.setCheckBox(false);
                gameSelect.navTo(newGameCreatePnl);
            }

            @Override
            public void onOk() {
                gameSelect.navTo(newGameCreatePnl);
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

    @Override
    public String getNavText() {
        return "Play vs Ai";
    }

    @Override
    public WinPnl navTo() {
        return this;
    }

//    @Override
//    public boolean verify() {
//        return false;
//    }
}
