package ver14.SharedClasses.DBActions.Table;


import ver14.SharedClasses.Utils.StrUtils;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Table - represents a table in the db.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum Table {
    /**
     * Games table.
     */
    Games(Col.GameID, Col.Player1, Col.Player2, Col.SavedGame, Col.Winner),
    /**
     * Unfinished games table.
     */
    UnfinishedGames(Col.GameID, Col.Player1, Col.Player2, Col.SavedGame, Col.PlayerToMove),
    /**
     * Users table.
     */
    Users(Col.Username, Col.Password);
    /**
     * The Cols in this table
     */
    public final Col[] cols;

    /**
     * Instantiates a new Table.
     *
     * @param cols the cols
     */
    Table(Col... cols) {
        this.cols = new ArrayList<>(Arrays.asList(cols)) {{
            add(Col.CreatedDateTime);
        }}.toArray(new Col[0]);
    }

    /**
     * Table and values string.
     *
     * @return the string
     */
    public String tableAndValues() {
        return StrUtils.clean(name() + escapeValues(cols, false, true));
    }

    /**
     * Escape values string.
     *
     * @param values      the values
     * @param quotes      quotes
     * @param parentheses parentheses
     * @return the escaped string
     */
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
