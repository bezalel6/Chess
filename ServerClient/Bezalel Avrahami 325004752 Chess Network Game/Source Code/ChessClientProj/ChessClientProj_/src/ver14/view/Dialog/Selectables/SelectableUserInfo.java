package ver14.view.Dialog.Selectables;

import ver14.SharedClasses.Sync.SyncableItem;
import ver14.SharedClasses.Sync.UserInfo;
import ver14.view.IconManager.IconManager;

import javax.swing.*;

/**
 * represents a Selectable user info.
 *
 * @param userInfo The User info.
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public record SelectableUserInfo(UserInfo userInfo) implements Selectable, SyncableItem {
    /**
     * Instantiates a new Selectable user info.
     *
     * @param userInfo the user info
     */
    public SelectableUserInfo {
    }

    /**
     * Gets user info.
     *
     * @return the user info
     */
    @Override
    public UserInfo userInfo() {
        return userInfo;
    }

    @Override
    public ImageIcon getIcon() {
        return IconManager.loadUserIcon(userInfo.profilePic);
    }

    @Override
    public String getText() {
        return userInfo.ID();
    }

    @Override
    public String ID() {
        return userInfo.ID();
    }
}
