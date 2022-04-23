package ver14.SharedClasses.Login;

import org.intellij.lang.annotations.MagicConstant;

/*
 * AuthSettings
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * AuthSettings -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * AuthSettings -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

@MagicConstant(intValues = {AuthSettings.NEVER_AUTH, AuthSettings.GUEST, AuthSettings.USER, AuthSettings.ANY_LOGIN, AuthSettings.NO_AUTH})
public @interface AuthSettings {
    int GUEST = 1, USER = 2;
    int ANY_LOGIN = GUEST | USER;
    int NEVER_AUTH = 4;
    //לא ממש נצרך כרגע כי עד שיש איזהשהו לוגין כל הUI חסום
    int NO_AUTH = 5;
}
