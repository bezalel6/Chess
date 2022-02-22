package ver6.view.dialogs;

import ver6.SharedClasses.SavedGames.GameInfo;
import ver6.SharedClasses.Sync.SyncedItems;
import ver6.SharedClasses.Sync.UserInfo;
import ver6.view.IconManager;
import ver6.view.SelectableUserInfo;
import ver6.view.dialogs.game_select.selectable.SelectableGame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public interface Selectable {
    static Collection<Selectable> createSelectables(SyncedItems list) {
        ArrayList<Selectable> ret = new ArrayList<>();
        IconManager.Size iconSize = WinPnl.listItemSize;
        list.forEachItem(item -> {
            ret.add(switch (list.syncedListType) {
                case RESUMABLE_GAMES, JOINABLE_GAMES -> new SelectableGame((GameInfo) item, iconSize);
                case CONNECTED_PLAYERS -> new SelectableUserInfo((UserInfo) item);
            });
        });
        return ret;
    }

    ImageIcon getIcon();

    String getText();
}
