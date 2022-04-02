package ver14.DB;

//import org.apache.commons.lang.SerializationUtils;

import org.apache.commons.lang3.SerializationUtils;
import org.intellij.lang.annotations.Language;
import ver14.SharedClasses.DBActions.Condition;
import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.DBResponse;
import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.DBActions.Table.Col;
import ver14.SharedClasses.DBActions.Table.Table;
import ver14.SharedClasses.Game.SavedGames.ArchivedGameInfo;
import ver14.SharedClasses.Game.SavedGames.GameInfo;
import ver14.SharedClasses.Game.SavedGames.UnfinishedGame;
import ver14.SharedClasses.RegEx;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.SharedClasses.Threads.ErrorHandling.ErrorType;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.File;
import java.io.Serializable;
import java.sql.*;
import java.util.*;

/**
 * DB - SQLמחלקת שירות לביצוע חיבור למסד נתונים מסוג אקסס וביצוע משפטי .
 * ---------------------------------------------------------------------------
 * by Ilan Peretz(ilanperets@gmail.com) 5/11/2021
 */
public class DB {
    public static final String APP_DB_FILE_PATH = "/assets/db.accdb";
    public static final String TIE_STR = RequestBuilder.TIE_STR;

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
    public static ServerDBResponse runUpdate(@Language("SQL") String sql, String... dbFilePath) {
        Connection con = getConnection(dbFilePath);
        DBResponse.Status status;
        try {
            Statement st = con.createStatement();
            st.executeUpdate(sql);
            status = DBResponse.Status.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            status = DBResponse.Status.ERROR;
        }
        return new ServerDBResponse(status);
    }

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

    // =======================================================================
    // CUSTOM METHODS HERE . . .
    // =======================================================================

    public static boolean isUsernameExists(String un) {
        return select(Table.Users, Condition.equals(Col.Username, un)).isAnyData();
    }

    private static ServerDBResponse select(Table selectFromTable, Condition condition, String... select) {
        return select(selectFromTable.name(), condition, select);
    }

    /**
     * @param condition
     * @param select    the cols to select. default is *
     * @return
     */
    private static ServerDBResponse select(String selectFrom, Condition condition, String... select) {
        String conditionsStr = condition == null ? "" : "WHERE " + condition;

        StringBuilder selecting = new StringBuilder();
        if (select.length > 0) {
            for (int i = 0; i < select.length; i++) {
                String str = select[i];
                selecting.append(str);
                if (i < select.length - 1) {
                    selecting.append(", ");
                }
            }
        } else {
            selecting.append("*");
        }

        return runQuery("SELECT %s FROM %s %s".formatted(selecting.toString(), selectFrom, conditionsStr));
    }

    public static ServerDBResponse runQuery(@Language("SQL") String sql, String... dbFilePath) {

        try {
            Connection con = getConnection(dbFilePath);
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = StrUtils.clean(sql);
            sql = sql.replaceAll("  ", " ");
            ResultSet resultSetTable = st.executeQuery(sql);
            return new ServerDBResponse(resultSetTable);
        } catch (SQLException e) {
//            e.printStackTrace();
            throw new MyError(ErrorType.DB) {{
                initCause(e);
            }};
        }

    }

    public static void main(String[] args) {
        try {
//            addGames("bezalel6");
//            clearGames();
//            System.out.println(request(PreMadeRequest.TopPlayers.createBuilder().build(5)));
            System.out.println(request(RequestBuilder.top().build(5)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearGames() {
        runUpdate("DELETE FROM " + Table.UnfinishedGames.name());
        runUpdate("DELETE FROM " + Table.Games.name());
    }

    public static DBResponse request(DBRequest request) {
        ServerDBResponse response;
//        System.out.println("requesting " + SqlFormatter.format(request.getRequest()).replaceAll("\\[ ", "[").replaceAll(" ]", "]"));
        System.out.println(request.getRequest());
        response = switch (request.type) {
            case Update -> runUpdate(request.getRequest());
            case Query -> runQuery(request.getRequest());
        };
        if (response == null)
            return null;
        if (request.getSubRequest() != null)
            response.setAddedRes(request(request.getSubRequest()));

        return response.clean();
    }

    /**
     * בדיקה האם יוזר ששם המשתמש והסיסמה המתקבלים כפרמטירים קיים בטבלת היוזרים
     *
     * @param un שם המשתמש של היוזר
     * @param pw הסיסמה של היוזר
     * @return מחזיר אמת אם קיים יוזר כזה ושקר אחרת
     */
    public static boolean isUserExists(String un, String pw) {
        Condition condition = Condition.equals(Col.Username, un);
        condition.add(Condition.equals(Col.Password, pw), Condition.Relation.AND);
        return select(Table.Users, condition).isAnyData();
    }

    private static void resetGamesTables() {
        runUpdate("delete from " + Table.Games.name());
        runUpdate("delete from " + Table.UnfinishedGames.name());
    }


    private static void addUsers() {
        for (int i = 0; i < 5; i++) {
            String un = "un-test" + i;
            addUser(un, "openSesame");
            addGames(un);
        }
    }

    public static SyncedItems<GameInfo> getUnfinishedGames(String username) {
        SyncedItems<GameInfo> gameInfos = new SyncedItems<>(SyncedListType.RESUMABLE_GAMES);
        if (RegEx.DontSaveGame.check(username))
            return gameInfos;
        Condition condition = Condition.equals(Col.Player1, username);
        condition.add(Condition.equals(Col.Player2, username), Condition.Relation.OR);

        ServerDBResponse rs = select(Table.UnfinishedGames, condition, Col.SavedGame.colName());
        String[][] rows = rs.getRows();
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            UnfinishedGame gameInfo = unstringify(rs.getCell(rowIndex, Col.SavedGame));
            gameInfos.add(gameInfo);
        }
        return gameInfos;
    }

    private static <T extends Serializable> T unstringify(String str) {
        str = StrUtils.clean(str);
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
        System.out.println(select(table, null));
    }

    public static void addUser(String un, String pw) {
        insert(Table.Users, un, pw);
    }

    private static void insert(Table table, String... values) {
        Object[] vals = new ArrayList<>(List.of(values)) {{
            add((System.currentTimeMillis() / 1000) + "");
        }}.toArray();
        assert vals.length == table.cols.length;
        runUpdate("INSERT INTO %s\nVALUES %s".formatted(table.tableAndValues(), Table.escapeValues(vals, true, true)));
    }


    private static String stringify(Serializable obj) {
        return Arrays.toString(SerializationUtils.serialize(obj));
    }

    private static void addGames(String winner) {
        addGames(winner, "bezalel7");
    }

    private static void addGames(String winner, String loser) {
        for (int i = 0; i < 5; i++) {
            saveGameResult(new ArchivedGameInfo(new Random().nextInt() + "", winner, loser, null, winner, new Stack<>()));
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
        return select(gamesTable, Condition.equals(Col.GameID, gameID)).isAnyData();
    }

    public static UnfinishedGame loadUnfinishedGame(String gameID) {
        return unstringify(select(Table.UnfinishedGames, Condition.equals(Col.GameID, gameID)).getCell(0, Col.SavedGame));
    }

    //todo PlayerOverview combination of wins,losses,ties
//    public static PlayerStatistics getPlayersStatistics(String username) {
//        if (RegEx.DontSaveGame.check(username))
//            return new PlayerStatistics(username, 0, 0, 0);
//        return new PlayerStatistics(username, getNumOfWins(username), getNumOfLosses(username), getNumOfTies(username));
//    }


    public static void deleteUnfinishedGame(UnfinishedGame game) {
        deleteGame(Table.UnfinishedGames, game.gameId);
    }

    public static void deleteGame(Table table, String gameId) {
        runUpdate("DELETE FROM %s WHERE GameID='%s'".formatted(table.name(), gameId));
    }

}
