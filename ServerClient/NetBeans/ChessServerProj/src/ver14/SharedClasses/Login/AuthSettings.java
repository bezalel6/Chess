package ver14.SharedClasses.Login;

import org.intellij.lang.annotations.MagicConstant;


/**
 * Auth settings. represents all types of login authentication.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
@MagicConstant(valuesFromClass = AuthSettings.class)
public @interface AuthSettings {
    /**
     * logged in as GUEST.
     */
    int GUEST = 1;
    /**
     * logged in as USER.
     */
    int USER = 2;
    /**
     * logged in as guest / user.
     */
    int ANY_LOGIN = GUEST | USER;
    /**
     * will never authorize.
     */
    int NEVER_AUTH = 4;
    /**
     * no authentication is required (not used at the moment, because nothing is accessible without some sort of login).
     */
    int NO_AUTH = 5;
}
