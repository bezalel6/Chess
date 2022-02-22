package ver7.view;

import ver7.SharedClasses.Sync.UserInfo;
import ver7.view.dialogs.Selectable;

import javax.swing.*;

public class SelectableUserInfo implements Selectable {
    public final UserInfo userInfo;

    public SelectableUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getText() {
        return userInfo.ID();
    }
}
