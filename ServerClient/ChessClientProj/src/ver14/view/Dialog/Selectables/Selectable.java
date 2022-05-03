package ver14.view.Dialog.Selectables;

import ver14.SharedClasses.Game.SavedGames.GameInfo;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.UserInfo;
import ver14.view.Dialog.Components.SyncableListComponent;
import ver14.view.IconManager.Size;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * represents a Selectable value. usually a representation of an existing logical structure..
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface Selectable {
    /**
     * Create selectables collection.
     *
     * @param list       the list
     * @param canUseIcon the can use icon
     * @return the collection
     */
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

    /**
     * Gets icon.
     *
     * @return the icon
     */
    ImageIcon getIcon();

    /**
     * Gets text.
     *
     * @return the text
     */
    String getText();
}
