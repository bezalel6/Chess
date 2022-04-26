package ver14.view;

import ver14.SharedClasses.Login.AuthSettings;
import ver14.SharedClasses.Login.LoginInfo;
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
        header.setIcon(IconManager.loadUserIcon(loginInfo.getProfilePic(), IconManager.PROFILE_PIC_BIG_SIZE));
        return AuthorizedComponent.super.setAuth(loginInfo);
    }

    @Override
    public int authRequirements() {
        return AuthSettings.ANY_LOGIN;
    }
}
