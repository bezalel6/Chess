package ver14.view.AuthorizedComponents;

import ver14.SharedClasses.Login.AuthSettings;
import ver14.SharedClasses.Login.LoginInfo;

import java.awt.*;

/**
 * Authorized component - represents an object whose state depends on the authorization type of the client.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface AuthorizedComponent {


    /**
     * Sets auth status.
     *
     * @param loginInfo the login info
     * @return the auth
     */
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

    /**
     * check if given auth status of the client satisfies this component's requirements.
     *
     * @param authing the authing
     * @return the boolean
     */
    default boolean auth(@AuthSettings int authing) {
        return (authRequirements() & authing) != 0;
    }

    /**
     * Enable comp.
     *
     * @param e the e
     */
    default void enableComp(boolean e) {
        if (this instanceof Component Component)
            Component.setEnabled(e);
    }

    /**
     * Auth requirements int.
     *
     * @return the int
     */
    @AuthSettings
    int authRequirements();
}
