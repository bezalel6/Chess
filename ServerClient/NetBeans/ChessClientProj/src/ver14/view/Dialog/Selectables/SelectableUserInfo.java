package ver14.view.Dialog.Selectables;

import ver14.SharedClasses.Sync.SyncableItem;
import ver14.SharedClasses.Sync.UserInfo;
import ver14.view.IconManager.IconManager;

import javax.swing.*;

public class SelectableUserInfo implements Selectable, SyncableItem {
    private final UserInfo userInfo;

    public SelectableUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public ImageIcon getIcon() {
        return IconManager.userIcon;
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
