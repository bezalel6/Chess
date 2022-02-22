/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ver9.SharedClasses.DBActions;


import java.util.ArrayList;
import java.util.Arrays;

public enum Table {
    Games(Col.GameID, Col.Player1, Col.Player2, Col.SavedGame, Col.Winner),
    UnfinishedGames(Col.GameID, Col.Player1, Col.Player2, Col.SavedGame, Col.PlayerToMove),
    Users(Col.Username, Col.Password);
    public final Col[] cols;

    Table(Col... cols) {
        this.cols = new ArrayList<>(Arrays.asList(cols)) {{
            add(Col.SavedDateTime);
        }}.toArray(new Col[0]);
    }

    public int colIndex(Col col) {
        for (int i = 0; i < cols.length; i++) {
            if (cols[i] == col) {
                return i;
            }
        }
        return -1;
    }

    public String tableAndValues() {
        return name() + escapeValues(cols, false, true);
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
            bldr.append(str);
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

    public enum Col {
        GameID,
        SavedGame,
        Password("pw"),
        Player1,
        Player2,
        Username("un"),
        Winner,
        PlayerToMove,
        SavedDateTime;
        private final String colName;

        Col() {
            this.colName = name();
        }

        Col(String colName) {
            this.colName = colName;
        }

        public String getColName() {
            return colName;
        }

        @Override
        public String toString() {
            return colName;
        }
    }

}
