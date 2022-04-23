package ver14.SharedClasses.DBActions.Table;


import ver14.SharedClasses.Utils.StrUtils;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Table
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * Table -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * Table -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public enum Table {
    Games(Col.GameID, Col.Player1, Col.Player2, Col.SavedGame, Col.Winner),
    UnfinishedGames(Col.GameID, Col.Player1, Col.Player2, Col.SavedGame, Col.PlayerToMove),
    Users(Col.Username, Col.Password);
    public final Col[] cols;

    Table(Col... cols) {
        this.cols = new ArrayList<>(Arrays.asList(cols)) {{
            add(Col.CreatedDateTime);
        }}.toArray(new Col[0]);
    }

    public String tableAndValues() {
        return StrUtils.clean(name() + escapeValues(cols, false, true));
    }

    public static String escapeValues(Object[] values, boolean quotes, boolean parentheses) {
        StringBuilder bldr = new StringBuilder();
        if (parentheses)
            bldr.append("(");
        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            String str = values[i].toString();
            if (quotes) {
                bldr.append("'");
            }
            bldr.append(StrUtils.clean(str));
            if (quotes) {
                bldr.append("'");
            }
            if (i != valuesLength - 1) {
                bldr.append(",");
            }
        }
        if (parentheses)
            bldr.append(')');
        return bldr.toString();
    }

}
