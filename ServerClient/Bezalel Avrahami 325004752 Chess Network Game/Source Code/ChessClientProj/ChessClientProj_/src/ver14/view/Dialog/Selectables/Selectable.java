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
 * represents an object the client can select in some way (clicking, hovering...). a visual representation of an existing logical structure, like {@link ver14.SharedClasses.Game.PlayerColor}
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface Selectable {
    /**
     * Create a list of selectables from a list of {@link SyncedItems}. used to convert a list of logical items to a list of displayable objects.
     *
     * @param list       the list
     * @param canUseIcon the can use icon
     * @return the collection
     */
    static ArrayList<Selectable> createSelectables(SyncedItems<?> list, boolean canUseIcon) {
        ArrayList<Selectable> ret = new ArrayList<>();
        Size iconSize = canUseIcon ? SyncableListComponent.listItemSize : null;
        list.forEachItem(item -> {
            ret.add(switch (list.syncedListType) {
                case RESUMABLE_GAMES, JOINABLE_GAMES, ONGOING_GAMES -> new SelectableGame((GameInfo) item, iconSize);
                case CONNECTED_USERS -> new SelectableUserInfo((UserInfo) item);

                default -> throw new IllegalStateException("Unexpected value: " + list.syncedListType);
            });
        });
        return ret;
    }

    /**
     * Gets the icon of this selectable.
     *
     * @return null to show no icon, or the icon
     */
    ImageIcon getIcon();

    /**
     * Gets the text of this selectable.
     *
     * @return null / empty string to show no text, or the text
     */
    String getText();
}
