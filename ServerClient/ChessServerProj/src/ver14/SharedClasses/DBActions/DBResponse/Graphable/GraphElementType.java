package ver14.SharedClasses.DBActions.DBResponse.Graphable;

import java.util.Locale;

/*
 * GraphElementType
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * GraphElementType -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * GraphElementType -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public enum GraphElementType {
    GREEN, RED, YELLOW, NORMAL;

    public String iconName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
