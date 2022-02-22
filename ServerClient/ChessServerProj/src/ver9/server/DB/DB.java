package ver9.server.DB;

import org.apache.commons.lang.SerializationUtils;
import org.intellij.lang.annotations.Language;
import ver9.SharedClasses.DBActions.DBRequest;
import ver9.SharedClasses.DBActions.DBResponse;
import ver9.SharedClasses.DBActions.RequestBuilder;
import ver9.SharedClasses.DBActions.Table;
import ver9.SharedClasses.RegEx;
import ver9.SharedClasses.SavedGames.ArchivedGameInfo;
import ver9.SharedClasses.SavedGames.UnfinishedGame;
import ver9.SharedClasses.Sync.SyncedItems;
import ver9.SharedClasses.Sync.SyncedListType;

import java.io.File;
import java.io.Serializable;
import java.sql.*;
import java.util.Date;
import java.util.*;

/**
 * DB - SQLמחלקת שירות לביצוע חיבור למסד נתונים מסוג אקסס וביצוע משפטי .
 * ---------------------------------------------------------------------------
 * by Ilan Peretz(ilanperets@gmail.com) 5/11/2021
 */
public class DB {
    public static final String APP_DB_FILE_PATH = "/assets/db.accdb";
    public static final String TIE_STR = RequestBuilder.TIE_STR;

    /**
     * בדיקה האם יוזר ששם המשתמש והסיסמה המתקבלים כפרמטירים קיים בטבלת היוזרים
     *
     * @param un שם המשתמש של היוזר
     * @param pw הסיסמה של היוזר
     * @return מחזיר אמת אם קיים יוזר כזה ושקר אחרת
     * @throws SQLException נזרקת שגיאה אם לא ניתן לבצע השאילה
     */
    public static boolean isUserExists(String un, String pw) {
        return select(Table.Users, new Condition(Table.Col.Username, un, "="), new Condition(Table.Col.Password, pw, "=")).isAnyData();
    }

    private static ServerDBResponse select(Table table, Condition... conditions) {
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

        return runQuery("SELECT * FROM %s %s".formatted(table.name(), conditionsStr.toString()));
    }

    /**
     * Run SQL Query Statement - SELECT הפעולה מריצה שאילתת
     *
     * @param sql        שאילתה
     * @param dbFilePath פרמטר אופציונאלי לנתיב מסד הנתונים עליו רוצים להפעיל השאילתה
     * @return מחזירה טבלת תוצאה
     * @throws SQLException נזרקת שגיאה אם לא ניתן להריץ את השאילתה
     */
    public static ServerDBResponse runQuery(String sql, String... dbFilePath) {

        Connection con = getConnection(dbFilePath);
        Statement st = null;
        try {
            st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSetTable = st.executeQuery(sql);
            return new ServerDBResponse(resultSetTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

        // execute SQL Query statement

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
    public static Connection getConnection(String... dbFileRelativePath) {
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

        try {
            return DriverManager.getConnection(dbURL, "", "");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    public static void delUser(String un, String pw) {
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
    public static int runUpdate(String sql, String... dbFilePath) {

        Connection con = getConnection(dbFilePath);
        try {
            Statement st = con.createStatement();
            int numRowsUpdated = st.executeUpdate(sql);
            return numRowsUpdated;

        } catch (Exception e) {
            throw new Error(e);
        }

        // execute SQL Update statement

    }

    public static boolean isUsernameExists(String un) {
        return select(Table.Users, new Condition(Table.Col.Username, un, "=")).isAnyData();
    }

    public static void main(String[] args) {
        try {
//            addUsers();
//            addGames("un-test0");
//            System.out.println(getTop(6));
            System.out.println(request(RequestBuilder.PreMadeRequest.LossesNum.builder.build(new String[]{"bezalel9"})));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DBResponse request(DBRequest request) {
        DBResponse response = switch (request.type) {
            case Insert -> {
                runUpdate(request.getRequest());
                yield new DBResponse(DBResponse.Status.SUCCESS);
            }
            case Select -> runQuery(request.getRequest()).createResponse();
            default -> throw new IllegalStateException("Unexpected value: " + request.type);
        };
        return response;
    }

    public static ServerDBResponse getTop(int maxRows) throws SQLException {
        @Language("SQL")
        String sql = """
                SELECT
                    Username AS [Username],
                    WinsNum AS [Num of Wins],
                    LossesNum AS [Num of Losses],
                    (CAST(WinsNum AS float) / CAST(LossesNum AS float)) AS [Win Lose Ratio]
                  FROM
                  (
                    SELECT TOP %d
                       un AS Username,
                      (%s) AS WinsNum,
                      (%s) AS LossesNum
                    FROM
                      Users
                  )
                    ORDER BY
                      [Win Lose Ratio] DESC
                                    """.formatted(maxRows, Statements.countWins, Statements.countLosses);
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        return new ServerDBResponse(resultSet);
    }

    /**
     * הדפסת טבלת תוצאה של שאילתה
     *
     * @param rs
     * @param colWidth
     * @throws SQLException
     */
    public static void printResultSet(ResultSet rs, int... colWidth) throws SQLException {
        int colw = 7;
        if (colWidth.length != 0)
            colw = colWidth[0];

        System.out.println(toStringResultSet(rs, colw));
    }

    /**
     * מחזירה מחרוזת המתארת את טבלת התוצאה של השאילתה המתקבלת כפרמטר
     *
     * @param rs       טבלת התוצאה של שאילתה
     * @param colWidth פרמטר אופציונאלי עבור רוחב עמודה רצוי
     * @return מחרזות לתיאור טבלת התוצאה
     * @throws SQLException נזרקת שגיאה אם לא ניתן לבצע המשימה
     */
    public static String toStringResultSet(ResultSet rs, int... colWidth) throws SQLException {
        int colw = 7;
        if (colWidth.length != 0)
            colw = colWidth[0];

        String str = " ";
        String format = "%-" + colw + "s";
        String line = new String(new char[colw - 2]).replace('\0', '-') + "  ";

        ResultSetMetaData rsmd = rs.getMetaData(); // to get columns names
        // Create Table Title (Columns Names)
        for (int i = 1; i <= rsmd.getColumnCount(); i++)
            str += String.format(format, rsmd.getColumnLabel(i)); //center(colWidth,rsmd.getColumnName(i));
        str += "\n ";
        for (int i = 1; i <= rsmd.getColumnCount(); i++)
            str += line;
        str += "\n ";

        // Create All Table Rows
        while (rs.next()) {
            //str += String.format("%2d.  ",rs.getRow());
            for (int i = 1; i <= rsmd.getColumnCount(); i++)
                str += String.format(format, rs.getString(i)); // center(colWidth,rs.getString(i)); //
            if (!rs.isLast())
                str += "\n ";
        }

        return str;
    }

    private static void addUsers() {
        for (int i = 0; i < 5; i++) {
            String un = "un-test" + i;
            addUser(un, "openSesame");
            addGames(un);
        }
    }

    public static void clearGames() {
        runUpdate("DELETE FROM " + Table.UnfinishedGames.name());
        runUpdate("DELETE FROM " + Table.Games.name());
    }

    public static SyncedItems getUnfinishedGames(String userName) {
        SyncedItems gameInfos = new SyncedItems(SyncedListType.RESUMABLE_GAMES);
        if (RegEx.DontSaveGame.check(userName))
            return gameInfos;
        ServerDBResponse rs = select(Table.UnfinishedGames,
                new Condition(Table.Col.Player1, userName, "=", "OR"),
                new Condition(Table.Col.Player2, userName, "=")
        );
        String[][] rows = rs.getRows();
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            UnfinishedGame gameInfo = unstringify(rs.getCell(rowIndex, Table.Col.SavedGame));
            gameInfos.add(gameInfo);
        }
        return gameInfos;
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

    private static void printAllTables() {
        for (Table table : Table.values()) {
            printTable(table);
        }
    }

    public static void printTable(Table table) {
        System.out.println(select(table));
    }

    public static List<String> getAllUsernames() {
//        return Arrays.stream(select(Table.Users).rows).map(row -> row.getByCol(Table.Col.Username)).collect(Collectors.toList());
        return null;
    }

    public static void addUser(String un, String pw) {
        insert(Table.Users, un, pw);
    }

    private static void insert(Table table, String... values) {
        Object[] vals = new ArrayList<>(List.of(values)) {{
            add(new Date().getTime() + "");
        }}.toArray();
        assert vals.length == table.cols.length;
        runUpdate("INSERT INTO %s\nVALUES %s".formatted(table.tableAndValues(), Table.escapeValues(vals, true, true)));
    }


    private static String stringify(Serializable obj) {
        return Arrays.toString(SerializationUtils.serialize(obj));
    }

    private static void addGames(String winner) {
        for (int i = 0; i < 5; i++) {
            saveGameResult(new ArchivedGameInfo(new Random().nextInt() + "", winner, "bezalel6", null, winner, new Stack<>()));
        }
    }

    public static void saveGameResult(ArchivedGameInfo gameInfo) {
        if (isGameIdExists(gameInfo.gameId, Table.Games))
            deleteGame(Table.Games, gameInfo.gameId);
        insert(Table.Games, gameInfo.gameId, gameInfo.creatorUsername, gameInfo.opponentUsername, stringify(gameInfo), gameInfo.getWinner());
    }

    public static void saveUnFinishedGame(UnfinishedGame gameInfo) {
        if (isGameIdExists(gameInfo.gameId, Table.UnfinishedGames))
            deleteGame(Table.UnfinishedGames, gameInfo.gameId);
        insert(Table.UnfinishedGames, gameInfo.gameId, gameInfo.creatorUsername, gameInfo.opponentUsername, stringify(gameInfo), gameInfo.gameSettings.getPlayerToMove().name());

    }

    public static boolean isGameIdExists(String gameID) {
        return isGameIdExists(gameID, Table.Games) || isGameIdExists(gameID, Table.UnfinishedGames);
    }

    public static boolean isGameIdExists(String gameID, Table gamesTable) {
        return select(gamesTable, new Condition(Table.Col.GameID, gameID, "=")).isAnyData();
    }

    public static UnfinishedGame loadUnfinishedGame(String gameID) {
        return unstringify(select(Table.UnfinishedGames, new Condition(Table.Col.GameID, gameID, "=")).getCell(0, Table.Col.SavedGame));
    }

    //todo PlayerOverview combination of wins,losses,ties
//    public static PlayerStatistics getPlayersStatistics(String username) {
//        if (RegEx.DontSaveGame.check(username))
//            return new PlayerStatistics(username, 0, 0, 0);
//        return new PlayerStatistics(username, getNumOfWins(username), getNumOfLosses(username), getNumOfTies(username));
//    }

    public static int getNumOfWins(String username) {
        DBResponse results = select(Table.Games, new Condition(Table.Col.Winner, username, "="));
        return results.numOfRows();
    }

    public static int getNumOfLosses(String username) {
        Condition p1 = new Condition(Table.Col.Player1, username, "=", "OR") {{
            setPreFix("(");
        }};
        Condition p2 = new Condition(Table.Col.Player2, username, "=", "AND") {{
            setPostFix(")");
        }};
        Condition tie = new Condition(Table.Col.Winner, TIE_STR, "<>");

        Condition notMe = new Condition(Table.Col.Winner, username, "<>");
        DBResponse results = select(Table.Games, p1, p2, notMe, tie);
        return results.numOfRows();
    }
//
//    public static TopFive getTopFive(){
////        DBResponse results = select(Table.Games,new Condition())
//    }

    public static int getNumOfTies(String username) {
        Condition p1 = new Condition(Table.Col.Player1, username, "=", "OR") {{
            setPreFix("(");
        }};
        Condition p2 = new Condition(Table.Col.Player2, username, "=") {{
            setPostFix(")");
        }};
        Condition tie = new Condition(Table.Col.Winner, TIE_STR, "=");
        return select(Table.Games, p1, p2, tie).numOfRows();
    }

    public static void deleteUnfinishedGame(UnfinishedGame game) {
        deleteGame(Table.UnfinishedGames, game.gameId);
    }

    public static void deleteGame(Table table, String gameId) {
        runUpdate("DELETE FROM %s WHERE GameID='%s'".formatted(table.name(), gameId));
    }

    private static class Statements {
        public final static @Language("SQl")
        String
                countWins = """
                SELECT
                   COUNT (*)
                 FROM
                   Games
                 WHERE
                   winner LIKE un
                   """,
                countLosses = """
                        SELECT
                              COUNT (*)
                            FROM
                              Games
                            WHERE
                              winner <> un
                              AND winner <> '%s'--not tie
                        """
                        .formatted(TIE_STR);

    }
}
