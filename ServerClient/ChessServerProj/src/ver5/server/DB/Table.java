package ver5.server.DB;

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
        return name() + DB.escapeValues(cols, false, true);
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
