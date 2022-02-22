package ver8.view.Wishfull.dialogs;

import ver8.SharedClasses.SavedGames.GameInfo;
import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.SharedClasses.Sync.UserInfo;
import ver8.view.IconManager;
import ver8.view.SelectableUserInfo;
import ver8.view.Wishfull.dialogs.GameSelect.Selectables.SelectableGame;

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
