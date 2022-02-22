package ver12.SharedClasses.DBActions.Table;


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

}
