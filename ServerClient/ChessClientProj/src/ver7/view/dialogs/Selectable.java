package ver7.view.dialogs;

import ver7.SharedClasses.SavedGames.GameInfo;
import ver7.SharedClasses.Sync.SyncedItems;
import ver7.SharedClasses.Sync.SyncedListType;
import ver7.SharedClasses.Sync.UserInfo;
import ver7.view.IconManager;
import ver7.view.SelectableUserInfo;
import ver7.view.dialogs.GameSelect.Selectables.SelectableGame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public interface Selectable {
    static Collection<Selectable> createSelectables(SyncedItems list) {
        ArrayList<Selectable> ret = new ArrayList<>();
        IconManager.Size iconSize = list.syncedListType == SyncedListType.ONGOING_GAMES ? null : WinPnl.listItemSize;
        list.forEachItem(item -> {
            ret.add(switch (list.syncedListType) {
                case RESUMABLE_GAMES, JOINABLE_GAMES, ONGOING_GAMES -> new SelectableGame((GameInfo) item, iconSize);
                case CONNECTED_PLAYERS -> new SelectableUserInfo((UserInfo) item);
                default -> throw new IllegalStateException("Unexpected value: " + list.syncedListType);
            });
        });
        return ret;
    }

    ImageIcon getIcon();

    String getText();
}
