package ver14.DB;

//import org.apache.commons.lang.SerializationUtils;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import ver14.SharedClasses.DBActions.Condition;
import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.DBRequest.Type;
import ver14.SharedClasses.DBActions.DBResponse.DBResponse;
import ver14.SharedClasses.DBActions.DBResponse.Status;
import ver14.SharedClasses.DBActions.DBResponse.StatusResponse;
import ver14.SharedClasses.DBActions.DBResponse.TableDBResponse;
import ver14.SharedClasses.DBActions.Relation;
import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.DBActions.Statements.CustomStatement;
import ver14.SharedClasses.DBActions.Statements.Selection;
import ver14.SharedClasses.DBActions.Table.Col;
import ver14.SharedClasses.DBActions.Table.Table;
import ver14.SharedClasses.Game.GameSetup.AISettings;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.SavedGames.ArchivedGameInfo;
import ver14.SharedClasses.Game.SavedGames.GameInfo;
import ver14.SharedClasses.Game.SavedGames.UnfinishedGame;
import ver14.SharedClasses.Misc.Environment;
import ver14.SharedClasses.Misc.IDsGenerator;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.SharedClasses.Utils.RegEx;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.File;
import java.io.Serializable;
import java.sql.*;
import java.util.Date;
import java.util.*;


/**
 * DB - utility class for communicating with the database.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class DB {
    /**
     * The constant APP_DB_FILE_PATH.
     */
    public static final String APP_DB_FILE_PATH = "/assets/db.accdb";
    /**
     * The constant TIE_STR.
     */
    public static final String TIE_STR = RequestBuilder.TIE_STR;

    /**
     * Del user.
     *
     * @param un the un
     * @param pw the pw
     */
    public static void delUser(String un, String pw) {
        runUpdate("DELETE FROM users WHERE 'un'='%s' AND 'pw'='%s'".formatted(un, pw));
    }

    /**
     * Run update.
     *
     * @param sql the sql
     * @return the status response
     */
    public static synchronized StatusResponse runUpdate(String sql) {
        sql = StrUtils.clean(sql);
        return runUpdate(new DBRequest(new CustomStatement(Type.Update, sql)));
    }

    /**
     * Run a requested update on the db, and return the response for it.
     * the response can be a success, with the number of updated rows, or an error.
     *
     * @param request the request
     * @return the status response
     */
    public static synchronized StatusResponse runUpdate(DBRequest request) {
        Connection con = getConnection();
        Status status;
        int updatedRows = 0;
        try {
            Statement st = con.createStatement();
            updatedRows = st.executeUpdate(request.getRequest());
            status = Status.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            status = Status.ERROR;
        }
        return new StatusResponse(status, request, updatedRows);
    }

    /**
     * create a connection to the db.
     *
     * @return the created connection to the db.
     */
    public static Connection getConnection() {
        String dbPath = APP_DB_FILE_PATH;

        //#####################################################################

        if (Environment.IS_JAR) {
//            dbPath = dbPath.substring(dbPath.lastIndexOf("/") + 1); //TurnON for JAR
            dbPath = "./ServerAssets" + dbPath;
        } else {
            dbPath = "src" + dbPath;
        }

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

    /**
     * Is username exists boolean.
     *
     * @param un the un
     * @return the boolean
     */
    public static boolean isUsernameExists(String un) {
        return select(Table.Users, Condition.equals(Col.Username, un)).isAnyData();
    }

    /**
     * Select db response.
     *
     * @param selectFromTable the select from table
     * @param condition       the condition
     * @param select          the select
     * @return the db response
     */
    private static DBResponse select(Table selectFromTable, Condition condition, String... select) {
        return select(selectFromTable.name(), condition, select);
    }

    /**
     * Select db response.
     *
     * @param selectFrom the select from
     * @param condition  the condition
     * @param select     the cols to select. default is *
     * @return db response
     */
    private static DBResponse select(String selectFrom, Condition condition, String... select) {

        Selection selection = new Selection(selectFrom, condition, select);
        return runQuery(new DBRequest(selection));

    }

    /**
     * Run a query on the db, and return the db's response. the response is built by individual requests allowing for maximum flexibility.
     *
     * @param request the request
     * @return the db response
     */
    public static synchronized DBResponse runQuery(DBRequest request) {
        try {
            Connection con = getConnection();
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = st.executeQuery(request.getRequest());
            return request.getBuilder().createResponse(rs, request);
        } catch (SQLException e) {
//            e.printStackTrace();
            throw new MyError.DBErr(e);
        }

    }


    /**
     * Gets all user details.
     *
     * @return the all user details
     */
    public static ArrayList<UserDetails> getAllUserDetails() {
        DBRequest request = new DBRequest(new Selection(Table.Users, new Object[]{Col.Username, Col.Password}));

        TableDBResponse res = (TableDBResponse) request(request);

        ArrayList<UserDetails> ret = new ArrayList<>();
        assert res != null;
        for (String[] row : res.getRows()) {
            ret.add(new UserDetails(row[0], row[1]));
        }
        return ret;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        try {
            clearGames();
//            System.out.println(request(PreMadeRequest.Games.createBuilder().build("bezalel6", new Date(0), new Date())));
//            createRndGames(30);
//            System.out.println(request(new DBRequest(new Delete(Table.UnfinishedGames, null))));
//            addUser("testing", "123456");

//            System.out.println(request(PreMadeRequest.ChangeProfilePic.createBuilder().build("bezalel6", "https://stackoverflow.com/questions/4275525/regex-for-urls-without-http-https-ftp")));

//            clearGames();

//            System.out.println(runQuery("SELECT format(DATEADD('s', CLng(Games.SAVEDDATETIME), #01/01/1970 00:00:00 AM#),'short time') as a FROM Games where format(DATEADD('s', CLng(Games.SAVEDDATETIME), #01/01/1970 00:00:00 AM#),'short time') between '16:00' AND '17:00'"));
//            System.out.println(runQuery("SELECT WeekDay(DATEADD('s', CLng(Games.SAVEDDATETIME), #01/01/1970 00:00:00 AM#)) FROM Games"));
//            System.out.println(runQuery("SELECT DATEADD('s', CLng(Games.SavedDateTime),#01/01/1970 00:00:00 AM#) FROM Games"));
//            System.out.println(request(RequestBuilder.games().build("bezalel6", null, null)));
//            System.out.println(request(RequestBuilder.statsByTimeOfDay().build("bezalel5")));
//            for (int i = 0; i < 24; i++) {
//                System.out.println(String.format("%02d:00", i));
//            }
//            createRndGames(30);
//            addGames("bezalel6");
//            clearGames();
//            System.out.println(request(PreMadeRequest.TopPlayers.createBuilder().build(5)));
//            System.out.println(request(RequestBuilder.top().build(5)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear games.
     */
    public static void clearGames() {
        runUpdate("DELETE FROM " + Table.UnfinishedGames.name());
        runUpdate("DELETE FROM " + Table.Games.name());
    }

    /**
     * Add user.
     *
     * @param un the un
     * @param pw the pw
     */
    public static void addUser(String un, String pw) {
        insert(Table.Users, un, pw);
    }

    /**
     * Insert.
     *
     * @param table  the table
     * @param values the values
     */
    private static void insert(Table table, String... values) {
        insertAtDate(table, new Date(), values);
    }

    /**
     * Insert at date.
     *
     * @param table  the table
     * @param date   the date
     * @param values the values
     */
    private static void insertAtDate(Table table, Date date, String... values) {
        Object[] vals = new ArrayList<>(List.of(values)) {{
            add((date.getTime() / 1000) + "");
        }}.toArray();
        assert vals.length == table.cols.length;
        runUpdate("INSERT INTO %s\nVALUES %s".formatted(table.tableAndValues(), Table.escapeValues(vals, true, true)));
    }

    /**
     * apply a db request, and return the db's response. a request will be forwarded to either:
     * {@linkplain #runUpdate(DBRequest)} or {@linkplain #runQuery(DBRequest)}
     *
     * @param request the request
     * @return the db's response to the request
     */
    public static DBResponse request(DBRequest request) {
        DBResponse response;
//        System.out.println("requesting " + SqlFormatter.format(request.getRequest()).replaceAll("\\[ ", "[").replaceAll(" ]", "]"));
        System.out.println(request.getRequest());
        response = switch (request.type) {
            case Update -> runUpdate(request);
            case Query -> runQuery(request);
        };
        if (response == null)
            return null;
        if (request.getSubRequest() != null)
            response.setAddedRes(request(request.getSubRequest()));

        return response;
    }

    /**
     * check if the given username and password match to a saved user in the db.
     *
     * @param un the username
     * @param pw the password
     * @return true if such user exists, false otherwise.
     */
    public static boolean isUserExists(String un, String pw) {
        Condition condition = Condition.equals(Col.Username, un);
        condition.add(Condition.equals(Col.Password, pw), Relation.AND);
        return select(Table.Users, condition).isAnyData();
    }

    /**
     * Reset games tables.
     */
    private static void resetGamesTables() {
        runUpdate("delete from " + Table.Games.name());
        runUpdate("delete from " + Table.UnfinishedGames.name());
    }

    /**
     * fill the db with fake users and games
     */
    private static void addUsers() {
        for (int i = 0; i < 10; i++) {
            String un = "bezalel" + i;
            addUser(un, "openSesame");
//            addGames(un);
        }
        createRndGames(20);
    }

    /**
     * Gets all unfinished games of a player.
     *
     * @param username the player's username
     * @return a list of all the unfinished games of this player
     */
    public static SyncedItems<GameInfo> getUnfinishedGames(String username) {
        SyncedItems<GameInfo> gameInfos = new SyncedItems<>(SyncedListType.RESUMABLE_GAMES);
        if (RegEx.DontSaveGame.check(username))
            return gameInfos;
        Condition condition = Condition.equals(Col.Player1, username);
        condition.add(Condition.equals(Col.Player2, username), Relation.OR);

        TableDBResponse rs = (TableDBResponse) select(Table.UnfinishedGames, condition, Col.SavedGame.colName());
        String[][] rows = rs.getRows();
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            UnfinishedGame gameInfo = unstringify(rs.getCell(rowIndex, Col.SavedGame));
            gameInfos.add(gameInfo);
        }
        return gameInfos;
    }

    /**
     * Unstringify a serialized object saved as a string.
     *
     * @param <T> the type parameter
     * @param str the str
     * @return the t
     */
    private static <T extends Serializable> T unstringify(String str) {
        str = StrUtils.clean(str);
        str = str.replace("[", "").replace("]", "");
        String[] split = str.split(",");
        byte[] arr = new byte[split.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Byte.parseByte(split[i].trim());
        }
        try {
            return (T) SerializationUtils.deserialize(arr);
        } catch (SerializationException e) {
            throw new MyError.DBErr("Serialization Exception!", e);
        }
    }

    /**
     * Print all tables.
     */
    private static void printAllTables() {
        for (Table table : Table.values()) {
            printTable(table);
        }
    }

    /**
     * Print table.
     *
     * @param table the table
     */
    public static void printTable(Table table) {
        System.out.println(select(table, null));
    }

    /**
     * Stringify an object - convert an object to a string representation of its serialization values.
     *
     * @param obj the object to convert
     * @return the created string
     */
    private static String stringify(Serializable obj) {
        return Arrays.toString(SerializationUtils.serialize(obj));
    }

    /**
     * Add fake games.
     *
     * @param winner the winner of the fake games. the loser will always be the player 'bezalel7'
     */
    private static void addGames(String winner) {
        addGames(winner, "bezalel7");
    }

    /**
     * Add fake games.
     *
     * @param winner the winner of the games
     * @param loser  the loser of the games
     */
    private static void addGames(String winner, String loser) {
        for (int i = 0; i < 5; i++) {
            saveGameResult(new ArchivedGameInfo(new Random().nextInt() + "", winner, loser, null, winner, new Stack<>()));
        }
    }

    /**
     * Save a finished game result.
     *
     * @param gameInfo the game info
     */
    public static void saveGameResult(ArchivedGameInfo gameInfo) {
        if (isGameIdExists(gameInfo.gameId, Table.Games))
            deleteGame(Table.Games, gameInfo.gameId);
        insertAtDate(Table.Games, gameInfo.getCreatedAt(), gameInfo.gameId, gameInfo.creatorUsername, gameInfo.opponentUsername, stringify(gameInfo), gameInfo.getWinner());
    }

    /**
     * Save an un-finished game.
     *
     * @param gameInfo the game info
     */
    public static void saveUnFinishedGame(UnfinishedGame gameInfo) {
        if (isGameIdExists(gameInfo.gameId, Table.UnfinishedGames))
            deleteGame(Table.UnfinishedGames, gameInfo.gameId);
        insertAtDate(Table.UnfinishedGames, gameInfo.getCreatedAt(), gameInfo.gameId, gameInfo.creatorUsername, gameInfo.opponentUsername, stringify(gameInfo), gameInfo.gameSettings.getPlayerToMove().name());

    }

    /**
     * Is game id exists - checks if a game id exists in any of the game tables in the db.
     *
     * @param gameID the game id to check
     * @return true if a game with the given id exists, false otherwise.
     */
    public static boolean isGameIdExists(String gameID) {
        return isGameIdExists(gameID, Table.Games) || isGameIdExists(gameID, Table.UnfinishedGames);
    }

    /**
     * Is game id exists in a specific table.
     *
     * @param gameID     the game id
     * @param gamesTable the games table
     * @return true if a game with the given id exists in the given table, false otherwise.
     */
    public static boolean isGameIdExists(String gameID, Table gamesTable) {
        return select(gamesTable, Condition.equals(Col.GameID, gameID)).isAnyData();
    }

    /**
     * Load an unfinished game by its id.
     *
     * @param gameID the game's id
     * @return the unfinished game
     */
    public static UnfinishedGame loadUnfinishedGame(String gameID) {
        return unstringify(((TableDBResponse) select(Table.UnfinishedGames, Condition.equals(Col.GameID, gameID))).getCell(0, Col.SavedGame));
    }

    /**
     * Delete unfinished game.
     *
     * @param game the game
     */
    public static void deleteUnfinishedGame(UnfinishedGame game) {
        deleteGame(Table.UnfinishedGames, game.gameId);
    }

    /**
     * Delete game.
     *
     * @param table  the table
     * @param gameId the game id
     */
    public static void deleteGame(Table table, String gameId) {
        runUpdate("DELETE FROM %s WHERE GameID='%s'".formatted(table.name(), gameId));
    }

    /**
     * Create rnd games.
     *
     * @param numOfGames the num of games
     */
    public static void createRndGames(int numOfGames) {
        ArrayList<UserDetails> details = getAllUserDetails();
        IDsGenerator generator = new IDsGenerator();
        Random rnd = new Random();
        for (int i = 0; i < numOfGames; i++) {
            String un = details.get(rnd.nextInt(details.size())).username;
            boolean isVsAi = rnd.nextBoolean();
            GameSettings gameSettings = new GameSettings();
            String oppUn, winner;
            if (isVsAi) {
                oppUn = AISettings.AiType.MyAi.toString();
                gameSettings.initDefault1vAi();
            } else {
                int index;
                do {
                    index = rnd.nextInt(details.size());
                } while (un.equals(details.get(index).username));
                oppUn = details.get(index).username;
                gameSettings.initDefault1v1();

            }
            winner = switch (rnd.nextInt(3)) {
                case 0 -> oppUn;
                case 1 -> un;
                case 2 -> TIE_STR;
                default -> "";
            };
            ArchivedGameInfo gameInfo = new ArchivedGameInfo(generator.generate(), un, oppUn, gameSettings, winner, new Stack<>());

            long now = new Date().getTime();
            long start = new Date(0).getTime();
            long d = (Math.abs(rnd.nextLong()) % (now - start)) + start;
            gameInfo.setCreatedAt(new Date(d));

            saveGameResult(gameInfo);
        }


    }

    /**
     * Gets profile pic.
     *
     * @param username the username
     * @return the profile pic
     */
    public static String getProfilePic(String username) {
        TableDBResponse response = (TableDBResponse) select(Table.Users, Condition.equals(Col.Username, username), Col.ProfilePic.colName());
        return response.isAnyData() ? response.getFirstRow()[0] : null;
    }

    /**
     * a convenient representation of a user's credentials.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static record UserDetails(String username, String password) {

        /**
         * To string string.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return
                    "username='" + username + '\'' +
                            ", password='" + password + '\'';
        }
    }

}
