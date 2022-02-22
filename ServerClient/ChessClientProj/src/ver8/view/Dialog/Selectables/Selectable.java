package ver8.view.Dialog.Selectables;

import ver8.SharedClasses.SavedGames.GameInfo;
import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.Sync.UserInfo;
import ver8.view.IconManager;
import ver8.view.OldDialogs.GameSelect.selectable.SelectableGame;
import ver8.view.OldDialogs.WinPnl;
import ver8.view.SelectableUserInfo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public interface Selectable {
    static Collection<Selectable> createSelectables(SyncedItems list) {
        ArrayList<Selectable> ret = new ArrayList<>();
        IconManager.Size iconSize = WinPnl.listItemSize;
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
