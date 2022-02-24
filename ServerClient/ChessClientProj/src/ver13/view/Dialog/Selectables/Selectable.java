package ver13.view.Dialog.Selectables;

import ver13.SharedClasses.SavedGames.GameInfo;
import ver13.SharedClasses.Sync.SyncedItems;
import ver13.SharedClasses.Sync.UserInfo;
import ver13.view.Dialog.WinPnl;
import ver13.view.IconManager.Size;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public interface Selectable {
    static Collection<Selectable> createSelectables(SyncedItems list, boolean canUseIcon) {
        ArrayList<Selectable> ret = new ArrayList<>();
        Size iconSize = canUseIcon ? WinPnl.listItemSize : null;
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
