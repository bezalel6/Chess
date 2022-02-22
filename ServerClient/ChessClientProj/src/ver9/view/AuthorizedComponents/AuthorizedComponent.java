/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ver9.view.AuthorizedComponents;

import ver9.SharedClasses.AuthSettings;
import ver9.SharedClasses.LoginInfo;

import java.awt.*;

public interface AuthorizedComponent {


    default boolean setAuth(LoginInfo loginInfo) {

        boolean e = false;
        if (auth(this, AuthSettings.NEVER_AUTH)) {
            e = false;
        } else if (loginInfo == null) {
            e = false;
        } else {
            if (auth(this, AuthSettings.GUEST)) {
                e = e || loginInfo.isGuest();
            }
            if (auth(this, AuthSettings.USER)) {
                e = e || loginInfo.getLoginType().asUser();
            }
        }
        enableComp(e);
        return e;
    }

    static boolean auth(AuthorizedComponent component, @AuthSettings int authing) {
        return (component.authSettings() & authing) != 0;
    }

    default void enableComp(boolean e) {
        if (this instanceof Component Component)
            Component.setEnabled(e);
    }

    @AuthSettings
    int authSettings();
}
