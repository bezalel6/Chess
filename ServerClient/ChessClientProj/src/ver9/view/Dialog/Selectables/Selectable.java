package ver9.view.Dialog.Selectables;

import ver9.SharedClasses.SavedGames.GameInfo;
import ver9.SharedClasses.Sync.SyncedItems;
import ver9.SharedClasses.Sync.UserInfo;
import ver9.view.Dialog.WinPnl;
import ver9.view.IconManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public interface Selectable {
    static Collection<Selectable> createSelectables(SyncedItems list) {
        ArrayList<Selectable> ret = new ArrayList<>();
        IconManager.Size iconSize = WinPnl.listItemSize;
        list.forEachItem(item -> {
            ret.add(switch (list.syncedListType) {
                case RESUMABLE_GAMES, JOINABLE_GAMES, ONGOING_GAMES -> new Game((GameInfo) item, iconSize);
                case CONNECTED_USERS -> new SelectableUserInfo((UserInfo) item);

                default -> throw new IllegalStateException("Unexpected value: " + list.syncedListType);
            });
        });
        return ret;
    }

    ImageIcon getIcon();

    String getText();
}
