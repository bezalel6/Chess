package ver14.view.AuthorizedComponents;

import ver14.SharedClasses.Login.AuthSettings;
import ver14.SharedClasses.Login.LoginInfo;

import java.awt.*;

public interface AuthorizedComponent {


    default boolean setAuth(LoginInfo loginInfo) {

        boolean e = false;
        if (auth(AuthSettings.NO_AUTH)) {
            e = true;
        } else if (auth(AuthSettings.NEVER_AUTH)) {
            e = false;
        } else if (loginInfo == null) {
            e = false;
        } else {
            if (auth(AuthSettings.GUEST)) {
                e = e || loginInfo.isGuest();
            }
            if (auth(AuthSettings.USER)) {
                e = e || loginInfo.getLoginType().asUser();
            }
        }
        enableComp(e);
        return e;
    }

    default boolean auth(@AuthSettings int authing) {
        return (authSettings() & authing) != 0;
    }

    default void enableComp(boolean e) {
        if (this instanceof Component Component)
            Component.setEnabled(e);
    }

    @AuthSettings
    int authSettings();
}
