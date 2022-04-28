package ver14.SharedClasses.Login;

import org.intellij.lang.annotations.MagicConstant;


@MagicConstant(valuesFromClass = AuthSettings.class)
public @interface AuthSettings {
    int GUEST = 1;
    int USER = 2;
    int ANY_LOGIN = GUEST | USER;
    int NEVER_AUTH = 4;
    int NO_AUTH = 5;
}
