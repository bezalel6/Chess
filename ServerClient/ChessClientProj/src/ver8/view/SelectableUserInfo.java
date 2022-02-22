package ver8.view;

import ver8.SharedClasses.Sync.UserInfo;
import ver8.view.OldDialogs.Selectable;

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
