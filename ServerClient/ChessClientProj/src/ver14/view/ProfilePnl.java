package ver14.view;

import ver14.SharedClasses.AuthSettings;
import ver14.SharedClasses.LoginInfo;
import ver14.view.AuthorizedComponents.AuthorizedComponent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.WinPnl;
import ver14.view.IconManager.IconManager;

public class ProfilePnl extends WinPnl implements AuthorizedComponent {
    private LoginInfo loginInfo;

    public ProfilePnl() {
        super(new Header(""));
    }

    public boolean setAuth(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
        Header header = getHeader();
        header.setText(loginInfo.getUsername());
        header.setIcon(IconManager.loadUserIcon(loginInfo.getProfilePic()));
        return AuthorizedComponent.super.setAuth(loginInfo);
    }

    @Override
    public int authSettings() {
        return AuthSettings.ANY_LOGIN;
    }
}
