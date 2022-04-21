package ver14.DB;

//import org.apache.commons.lang.SerializationUtils;

import org.apache.commons.lang3.SerializationUtils;
import ver14.SharedClasses.DBActions.Condition;
import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.DBActions.DBResponse.DBResponse;
import ver14.SharedClasses.DBActions.DBResponse.StatusResponse;
import ver14.SharedClasses.DBActions.DBResponse.TableDBResponse;
import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.DBActions.Statements.CustomStatement;
import ver14.SharedClasses.DBActions.Statements.Selection;
import ver14.SharedClasses.DBActions.Table.Col;
import ver14.SharedClasses.DBActions.Table.Table;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.SavedGames.ArchivedGameInfo;
import ver14.SharedClasses.Game.SavedGames.GameInfo;
import ver14.SharedClasses.Game.SavedGames.UnfinishedGame;
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

    public static synchronized StatusResponse runUpdate(String sql) {
        sql = StrUtils.clean(sql);
        return runUpdate(new DBRequest(new CustomStatement(DBRequest.Type.Update, sql)));
    }

    /**
     * Run SQL Update Statement - INSERT, DELETE, UPDATE הפעולה מריצה משפט עדכון
     */
    public static synchronized StatusResponse runUpdate(DBRequest request) {
        Connection con = getConnection();
        DBResponse.Status status;
        int updatedRows = 0;
        try {
            Statement st = con.createStatement();
            updatedRows = st.executeUpdate(request.getRequest());
            status = DBResponse.Status.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            status = DBResponse.Status.ERROR;
        }
        return new StatusResponse(status, request, updatedRows);
    }

    /**
     * הפעולה מבצעת חיבור למסד הנתונים שהנתיב אליו מתקבל כפרמטר
     *
     * @param dbFileRelativePath הנתיב היחסי למסד הנתונים שאליו רוצים לקבל חיבור
     * @return מחזיר חיבור למסד הנתונים
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

    private static DBResponse select(Table selectFromTable, Condition condition, String... select) {
        return select(selectFromTable.name(), condition, select);
    }

    /**
     * @param condition
     * @param select    the cols to select. default is *
     * @return
     */
    private static DBResponse select(String selectFrom, Condition condition, String... select) {

        Selection selection = new Selection(selectFrom, condition, select);
        return runQuery(new DBRequest(selection));

    }

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

    public static void main(String[] args) {
        try {

            System.out.println(request(PreMadeRequest.TopPlayers.createBuilder().build(5)));
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

    public static void addUser(String un, String pw) {
        insert(Table.Users, un, pw);
    }

    private static void insert(Table table, String... values) {
        insertAtDate(table, new Date(), values);
    }

    private static void insertAtDate(Table table, Date date, String... values) {
        Object[] vals = new ArrayList<>(List.of(values)) {{
            add((date.getTime() / 1000) + "");
        }}.toArray();
        assert vals.length == table.cols.length;
        runUpdate("INSERT INTO %s\nVALUES %s".formatted(table.tableAndValues(), Table.escapeValues(vals, true, true)));
    }

    public static void clearGames() {
        runUpdate("DELETE FROM " + Table.UnfinishedGames.name());
        runUpdate("DELETE FROM " + Table.Games.name());
    }

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
        for (int i = 0; i < 10; i++) {
            String un = "bezalel" + i;
            addUser(un, "openSesame");
//            addGames(un);
        }
        createRndGames(20);
    }

    public static SyncedItems<GameInfo> getUnfinishedGames(String username) {
        SyncedItems<GameInfo> gameInfos = new SyncedItems<>(SyncedListType.RESUMABLE_GAMES);
        if (RegEx.DontSaveGame.check(username))
            return gameInfos;
        Condition condition = Condition.equals(Col.Player1, username);
        condition.add(Condition.equals(Col.Player2, username), Condition.Relation.OR);

        TableDBResponse rs = (TableDBResponse) select(Table.UnfinishedGames, condition, Col.SavedGame.colName());
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
        insertAtDate(Table.Games, gameInfo.getCreatedAt(), gameInfo.gameId, gameInfo.creatorUsername, gameInfo.opponentUsername, stringify(gameInfo), gameInfo.getWinner());
    }

    public static void saveUnFinishedGame(UnfinishedGame gameInfo) {
        if (isGameIdExists(gameInfo.gameId, Table.UnfinishedGames))
            deleteGame(Table.UnfinishedGames, gameInfo.gameId);
        insertAtDate(Table.UnfinishedGames, gameInfo.getCreatedAt(), gameInfo.gameId, gameInfo.creatorUsername, gameInfo.opponentUsername, stringify(gameInfo), gameInfo.gameSettings.getPlayerToMove().name());

    }

    public static boolean isGameIdExists(String gameID) {
        return isGameIdExists(gameID, Table.Games) || isGameIdExists(gameID, Table.UnfinishedGames);
    }

    public static boolean isGameIdExists(String gameID, Table gamesTable) {
        return select(gamesTable, Condition.equals(Col.GameID, gameID)).isAnyData();
    }

    public static UnfinishedGame loadUnfinishedGame(String gameID) {
        return unstringify(((TableDBResponse) select(Table.UnfinishedGames, Condition.equals(Col.GameID, gameID))).getCell(0, Col.SavedGame));
    }

    public static void deleteUnfinishedGame(UnfinishedGame game) {
        deleteGame(Table.UnfinishedGames, game.gameId);
    }

    public static void deleteGame(Table table, String gameId) {
        runUpdate("DELETE FROM %s WHERE GameID='%s'".formatted(table.name(), gameId));
    }

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
                oppUn = AiParameters.AiType.MyAi.toString();
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

    public static String getProfilePic(String username) {
        TableDBResponse response = (TableDBResponse) select(Table.Users, Condition.equals(Col.Username, username), Col.ProfilePic.colName());
        return response.isAnyData() ? response.getFirstRow()[0] : null;
    }

    public static record UserDetails(String username, String password) {

        @Override
        public String toString() {
            return
                    "username='" + username + '\'' +
                            ", password='" + password + '\'';
        }
    }

}
