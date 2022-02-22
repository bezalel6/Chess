package ver5.server.DB;

import org.apache.commons.lang.SerializationUtils;
import ver5.SharedClasses.GameSettings;
import ver5.SharedClasses.PlayerStatistics;
import ver5.SharedClasses.RegEx;
import ver5.SharedClasses.SavedGame;

import java.io.File;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DB - SQLמחלקת שירות לביצוע חיבור למסד נתונים מסוג אקסס וביצוע משפטי .
 * ---------------------------------------------------------------------------
 * by Ilan Peretz(ilanperets@gmail.com) 5/11/2021
 */
public class DB {
    public static final String APP_DB_FILE_PATH = "/assets/db.accdb";
    public static final String TIE_STR = "$tie";

    /**
     * בדיקה האם יוזר ששם המשתמש והסיסמה המתקבלים כפרמטירים קיים בטבלת היוזרים
     *
     * @param un שם המשתמש של היוזר
     * @param pw הסיסמה של היוזר
     * @return מחזיר אמת אם קיים יוזר כזה ושקר אחרת
     * @throws SQLException נזרקת שגיאה אם לא ניתן לבצע השאילה
     */
    public static boolean isUserExists(String un, String pw) throws SQLException {
        return select(Table.Users, new Condition(Table.Col.Username, un, "="), new Condition(Table.Col.Password, pw, "=")).isAnyData();
    }

    private static Results select(Table table, Condition... conditions) throws SQLException {
        StringBuilder conditionsStr = new StringBuilder();
        if (conditions.length != 0) {
            conditionsStr.append("WHERE ");
            for (int i = 0; i < conditions.length; i++) {
                Condition currentCondition = conditions[i];
                conditionsStr.append(currentCondition);

                if (i != conditions.length - 1) {
                    conditionsStr.append(" %s ".formatted(currentCondition.nextConditionRelation));
                }
            }
        }
        return runQuery(table, "SELECT * FROM %s %s".formatted(table.name(), conditionsStr.toString()));
    }

    /**
     * Run SQL Query Statement - SELECT הפעולה מריצה שאילתת
     *
     * @param sql        שאילתה
     * @param dbFilePath פרמטר אופציונאלי לנתיב מסד הנתונים עליו רוצים להפעיל השאילתה
     * @return מחזירה טבלת תוצאה
     * @throws SQLException נזרקת שגיאה אם לא ניתן להריץ את השאילתה
     */
    public static Results runQuery(Table table, String sql, String... dbFilePath) throws SQLException {
        Connection con = getConnection(dbFilePath);
        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        // execute SQL Query statement
        ResultSet resultSetTable = st.executeQuery(sql);

        return new Results(resultSetTable, table);
    }

    // =======================================================================
    // CUSTOM METHODS HERE . . .
    // =======================================================================

    /**
     * הפעולה מבצעת חיבור למסד הנתונים שהנתיב אליו מתקבל כפרמטר
     *
     * @param dbFileRelativePath הנתיב היחסי למסד הנתונים שאליו רוצים לקבל חיבור
     * @return מחזיר חיבור למסד הנתונים
     * @throws SQLException נזרקת שגיאה אם החיבור למסד הנתונים לא מצליח
     */
    public static Connection getConnection(String... dbFileRelativePath) throws SQLException {
        String dbPath = APP_DB_FILE_PATH;
        if (dbFileRelativePath.length != 0)
            dbPath = dbFileRelativePath[0];

        //#####################################################################
        dbPath = "src" + dbPath;
        //dbPath = dbPath.substring(dbPath.lastIndexOf("/")+1); //TurnON for JAR
        //#####################################################################

        // dbURL: Access DB Driver Name + dbPath
        String dbURL = "jdbc:ucanaccess://" + new File(dbPath).getAbsolutePath();

        // create the connection object to db
        Connection dbCon = DriverManager.getConnection(dbURL, "", "");

        return dbCon;
    }

    public static void delUser(String un, String pw) throws SQLException {
        runUpdate("DELETE FROM users WHERE 'un'='%s' AND 'pw'='%s'".formatted(un, pw));
    }

    /**
     * Run SQL Update Statement - INSERT, DELETE, UPDATE הפעולה מריצה משפט עדכון
     *
     * @param sql        עדכון
     * @param dbFilePath
     * @return הפעולה מחזירה מספר השורות שעברו עדכון
     * @throws SQLException נזרקת שגיאה אם לא ניתן להריץ משפט עדכון
     */
    public static int runUpdate(String sql, String... dbFilePath) throws SQLException {
        System.out.println(sql);

        Connection con = getConnection(dbFilePath);
        Statement st = con.createStatement();

        // execute SQL Update statement
        int numRowsUpdated = st.executeUpdate(sql);

        return numRowsUpdated;
    }

    public static boolean isUsernameExists(String un) throws SQLException {
        return select(Table.Users, new Condition(Table.Col.Username, un, "=")).isAnyData();
    }

    public static void main(String[] args) {
        try {
            clearGames();
//            addGames();

            saveGame(new SavedGame("123456", "bezalel6", "opponent", GameSettings.EXAMPLE, null));

            for (SavedGame s : getUnfinishedGames("bezalel6")) {
                System.out.println(s);
            }

//            for (String un : getAllUsernames()) {
//                System.out.println(getPlayersStatistics(un));
//            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearGames() throws SQLException {
        runUpdate("DELETE FROM " + Table.UnfinishedGames.name());
        runUpdate("DELETE FROM " + Table.Games.name());
    }

    private static void printAllTables() throws SQLException {
        for (Table table : Table.values()) {
            printTable(table);
        }
    }

    public static void printTable(Table table) throws SQLException {
        System.out.println(select(table));
    }

    public static List<String> getAllUsernames() throws SQLException {
        return Arrays.stream(select(Table.Users).rows).map(row -> row.getByCol(Table.Col.Username)).collect(Collectors.toList());
    }

    public static void addUser(String un, String pw) throws SQLException {
        insert(Table.Users, un, pw);
    }

    private static void insert(Table table, String... values) throws SQLException {
        Object[] vals = new ArrayList<>(List.of(values)) {{
            add(new Date().getTime() + "");
        }}.toArray();
        assert vals.length == table.cols.length;
        runUpdate("INSERT INTO %s\nVALUES %s".formatted(table.tableAndValues(), escapeValues(vals, true, true)));
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

    private static <T extends Serializable> String stringify(T obj) {
        return Arrays.toString(SerializationUtils.serialize(obj));
    }

    private static void addGames() throws SQLException {
        for (int i = 0; i < 5; i++) {
            saveGame(new SavedGame("id" + i, "bezalel6", "bezalel7", GameSettings.EXAMPLE, "bezalel6"));
            saveGame(new SavedGame("id" + ((i + 1) * 4), "bezalel6", "bezalel7", GameSettings.EXAMPLE, TIE_STR));
            saveGame(new SavedGame("id" + i, "a", "opponentUsername", GameSettings.EXAMPLE, null));
        }
    }

    public static void saveGame(SavedGame savedGame) throws SQLException {
        saveGame(savedGame, true);
    }

    public static void saveGame(SavedGame savedGame, boolean overrideExisting) throws SQLException {
        Table table = savedGame.winner == null ? Table.UnfinishedGames : Table.Games;
        if (overrideExisting && isGameIdExists(savedGame.gameId, table))
            deleteGame(table, savedGame.gameId);
        if (RegEx.DontSaveGame.check(savedGame.creatorUsername) && RegEx.DontSaveGame.check(savedGame.opponentUsername)) {
            return;
        }
        if (table == Table.Games) {
            insert(table, savedGame.gameId, savedGame.creatorUsername, savedGame.opponentUsername, stringify(savedGame), savedGame.winner);

        } else {
            insert(table, savedGame.gameId, savedGame.creatorUsername, savedGame.opponentUsername, stringify(savedGame), savedGame.gameSettings.getPlayerColor().name());
        }
    }

    public static void deleteGame(Table table, String gameId) throws SQLException {
        runUpdate("DELETE FROM %s WHERE GameID='%s'".formatted(table.name(), gameId));
    }

    public static boolean isGameIdExists(String gameID) throws SQLException {
        return isGameIdExists(gameID, Table.Games) || isGameIdExists(gameID, Table.UnfinishedGames);
    }

    public static boolean isGameIdExists(String gameID, Table gamesTable) throws SQLException {
        return select(gamesTable, new Condition(Table.Col.GameID, gameID, "=")).isAnyData();
    }

    public static SavedGame[] getUnfinishedGames(String userName) throws SQLException {
        if (RegEx.DontSaveGame.check(userName))
            return new SavedGame[0];
        Results rs = select(Table.UnfinishedGames,
                new Condition(Table.Col.Player1, userName, "=", "OR"),
                new Condition(Table.Col.Player2, userName, "=")
        );
        ArrayList<SavedGame> savedGames = new ArrayList<>();
        for (Results.Row row : rs.rows) {
            SavedGame savedGame = unstringify(row.getByCol(Table.Col.SavedGame));
            savedGames.add(savedGame);
        }
        return savedGames.toArray(new SavedGame[0]);
    }

    public static SavedGame loadUnfinishedGameByGameId(String gameID) throws SQLException {
        return unstringify(select(Table.UnfinishedGames, new Condition(Table.Col.GameID, gameID, "=")).getFirst().getByCol(Table.Col.SavedGame));
    }

    private static <T extends Serializable> T unstringify(String str) {
        str = str.replace("[", "").replace("]", "");
        String[] split = str.split(",");
        byte[] arr = new byte[split.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Byte.parseByte(split[i].trim());
        }
        return (T) SerializationUtils.deserialize(arr);
    }

    public static PlayerStatistics getPlayersStatistics(String username) throws SQLException {
        return new PlayerStatistics(username, getNumOfWins(username), getNumOfLosses(username), getNumOfTies(username));
    }

    public static int getNumOfWins(String username) throws SQLException {
        Results results = select(Table.Games, new Condition(Table.Col.Winner, username, "="));
        return results.numOfRows();
    }

    public static int getNumOfLosses(String username) throws SQLException {
        Condition p1 = new Condition(Table.Col.Player1, username, "=", "OR") {{
            setPreFix("(");
        }};
        Condition p2 = new Condition(Table.Col.Player2, username, "=", "AND") {{
            setPostFix(")");
        }};
        Condition tie = new Condition(Table.Col.Winner, TIE_STR, "<>");

        Condition notMe = new Condition(Table.Col.Winner, username, "<>");
        Results results = select(Table.Games, p1, p2, notMe, tie);
        return results.numOfRows();
    }

    public static int getNumOfTies(String username) throws SQLException {
        Condition p1 = new Condition(Table.Col.Player1, username, "=", "OR") {{
            setPreFix("(");
        }};
        Condition p2 = new Condition(Table.Col.Player2, username, "=") {{
            setPostFix(")");
        }};
        Condition tie = new Condition(Table.Col.Winner, TIE_STR, "=");
        return select(Table.Games, p1, p2, tie).numOfRows();
    }
}
