package ver9.view.Dialog.Selectables;

import ver9.SharedClasses.Sync.UserInfo;
import ver9.view.IconManager;

import javax.swing.*;

public class SelectableUserInfo implements Selectable {
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
}
