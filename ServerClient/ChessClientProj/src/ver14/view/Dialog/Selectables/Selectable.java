package ver14.view.Dialog.Selectables;

import ver14.SharedClasses.Game.SavedGames.GameInfo;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.UserInfo;
import ver14.view.Dialog.Components.SyncableListComponent;
import ver14.view.IconManager.Size;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public interface Selectable {
    static Collection<Selectable> createSelectables(SyncedItems<?> list, boolean canUseIcon) {
        ArrayList<Selectable> ret = new ArrayList<>();
        Size iconSize = canUseIcon ? SyncableListComponent.listItemSize : null;
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
