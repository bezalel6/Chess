package ver14.SharedClasses.DBActions;

import ver14.SharedClasses.Callbacks.ObjCallback;
import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.Arg.ArgType;
import ver14.SharedClasses.DBActions.Arg.Config;
import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.DBActions.DBResponse.DBResponse;
import ver14.SharedClasses.DBActions.DBResponse.Graphable.GraphElement;
import ver14.SharedClasses.DBActions.DBResponse.Graphable.GraphElementType;
import ver14.SharedClasses.DBActions.DBResponse.Graphable.GraphableDBResponse;
import ver14.SharedClasses.DBActions.DBResponse.StatusResponse;
import ver14.SharedClasses.DBActions.DBResponse.TableDBResponse;
import ver14.SharedClasses.DBActions.Statements.CustomStatement;
import ver14.SharedClasses.DBActions.Statements.SQLStatement;
import ver14.SharedClasses.DBActions.Statements.Selection;
import ver14.SharedClasses.DBActions.Statements.Update;
import ver14.SharedClasses.DBActions.Table.Col;
import ver14.SharedClasses.DBActions.Table.Math;
import ver14.SharedClasses.DBActions.Table.SwitchCase;
import ver14.SharedClasses.DBActions.Table.Table;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.SharedClasses.Utils.ArrUtils;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;


/**
 * Request builder - creates builders capable of generating complete sql statements. after building with the required arguments .
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class RequestBuilder implements Serializable {
    /**
     * The constant TIE_STR.
     */
    public static final String TIE_STR = "----tie----";
    /**
     * The request Arguments.
     */
    public final Arg[] args;
    /**
     * The Statement.
     */
    protected final SQLStatement statement;
    /**
     * The Name.
     */
    protected final String name;
    /**
     * The Should sync.
     */
    private final ArrayList<SyncedListType> shouldSync = new ArrayList<>();
    /**
     * The Post description.
     */
    protected String postDescription;
    /**
     * The Pre description.
     */
    protected String preDescription;
    /**
     * The Sub builder.
     */
    protected RequestBuilder subBuilder = null;
    /**
     * The Built args vals.
     */
    private String[] builtArgsVals;

    /**
     * Instantiates a new Request builder.
     *
     * @param request   the request
     * @param variation the variation
     */
    public RequestBuilder(DBRequest request, PreMadeRequest.Variation variation) {
        this(new CustomStatement(request.type, request.getRequest()), variation.variationName, variation.variationArgs);
        RequestBuilder builder = request.getBuilder();
        postDescription = builder.postDescription;
        preDescription = builder.preDescription;
    }

    /**
     * Instantiates a new Request builder.
     *
     * @param statement the statement
     * @param name      the name
     * @param args      the args
     */
    public RequestBuilder(SQLStatement statement, String name, Arg... args) {
        this(statement, name, name, args);
    }


    /**
     * Instantiates a new Request builder.
     *
     * @param statement the statement
     * @param name      the name
     * @param desc      the desc
     * @param args      the args
     */
    public RequestBuilder(SQLStatement statement, String name, String desc, Arg... args) {
        this(statement, name, desc, desc, args);
    }

    /**
     * Instantiates a new Request builder.
     *
     * @param statement       the statement
     * @param name            the name
     * @param postDescription the post description
     * @param preDescription  the pre description
     * @param args            the args
     */
    public RequestBuilder(SQLStatement statement, String name, String postDescription, String preDescription, Arg... args) {
        this.statement = statement;
        this.name = name;
        this.postDescription = postDescription;
        this.preDescription = preDescription;
        this.args = args;
    }

    /**
     * Create variation request builder.
     *
     * @param og               the og
     * @param variationCreator the variation creator
     * @return the request builder
     */
    public static RequestBuilder createVariation(ObjCallback<RequestBuilder> og, PreMadeRequest.VariationCreator variationCreator) {
        RequestBuilder builder = og.get();
        PreMadeRequest.Variation variation = variationCreator.create(builder);
        DBRequest req = builder.build(variation.buildingArgs);
        RequestBuilder ret = new RequestBuilder(req, variation);
        RequestBuilder sub = builder.subBuilder;
        variation = variationCreator.create(sub);//todo check if this(recreating the variation for the sub) works/necessary
        if (sub != null)
            sub = new RequestBuilder(sub.build(variation.buildingArgs), variation);

        ret.setSubBuilder(sub);
        return ret;
    }

    /**
     * Change password request builder.
     *
     * @return the request builder
     */
    public static RequestBuilder changePassword() {
        Arg username = new Arg(ArgType.Username);
        Arg pw = new Arg(ArgType.Password);
        pw.setUserInput(false);

        Update update = new Update(Table.Users, Condition.equals(Col.Username, username.repInStr), new Update.NewValue(Col.Password, pw.repInStr));

        return new RequestBuilder(update, "change password", "", username, pw);
    }

    /**
     * Change profile pic request builder.
     *
     * @return the request builder
     */
    public static RequestBuilder changeProfilePic() {
        Arg username = new Arg(ArgType.Username);
        Arg url = new Arg(ArgType.PictureUrl, true, new Config<>("Enter link to the new profile picture"));

        Update update = new Update(Table.Users, Condition.equals(Col.Username, username), new Update.NewValue(Col.ProfilePic, url));

        return new RequestBuilder(update, "change profile picture", "", username, url) {{
            addShouldSync(SyncedListType.CONNECTED_USERS);
        }};
    }

    /**
     * Add should sync.
     *
     * @param listType the list type
     */
    protected void addShouldSync(SyncedListType listType) {
        shouldSync.add(listType);
    }

    /**
     * Delete all un finished games request builder.
     *
     * @return the request builder
     */
    public static RequestBuilder deleteAllUnFinishedGames() {
        Arg username = new Arg(ArgType.Username);
        Delete del = new Delete(Table.UnfinishedGames, p1_OR_p2(username.repInStr, Table.UnfinishedGames));

        return new RequestBuilder(del, "delete all unfinished games", "", username);
    }

    /**
     * P 1 or p 2 condition.
     *
     * @param un        the un
     * @param playersOf the players of
     * @return the condition
     */
    private static Condition p1_OR_p2(Object un, Table playersOf) {
        Condition condition = Condition.equals(Col.Player1.of(playersOf), un);
        condition.add(Condition.equals(Col.Player2.of(playersOf), un), Condition.Relation.OR, true);
        return condition;
    }

    /**
     * Games request builder.
     *
     * @return the request builder
     */
    public static RequestBuilder games() {
        Arg username = new Arg(ArgType.Username);
        Col opponent = Col.switchCase(
                "Opponent",
                SwitchCase.equals(Col.Player1, username.repInStr, Col.Player2),
                SwitchCase.defaultCase(Col.Player1)
        );

        Arg start = new Arg(ArgType.Date, new Config<>("starting date", new Date(0)));
        Arg end = new Arg(ArgType.Date, new Config<>("ending date", new Date(), "Now"));

        Condition condition = p1_OR_p2(username.repInStr);

        Col date = Col.CreatedDateTime.as().of(Table.Games).date();
        condition = condition.and(Condition.between(date.colName(), start.repInStr, end.repInStr));

        Selection games = new Selection(
                Table.Games,
                condition,
                new Object[]{
                        opponent,
                        Col.Winner.as().of(Table.Games),
                        date
                }
        );
        games.orderBy(date, Selection.Order.DESC);

        Selection selection = gamesStats(username.repInStr, condition);
        RequestBuilder sub = new RequestBuilder(selection, "Games Stats", username, start, end);

        return new RequestBuilder(games,
                "Games In Range",
                "All Games For %s in selected range".formatted(username.repInStr),
//                "All Games For %s between %s and %s".formatted(username.repInStr, start.repInStr, end.repInStr),
                "Get Your Games In Range",
                username,
                start,
                end) {{
            setSubBuilder(sub);
        }};
    }

    /**
     * P 1 or p 2 condition.
     *
     * @param un the un
     * @return the condition
     */
    private static Condition p1_OR_p2(Object un) {
        return p1_OR_p2(un, Table.Games);
    }

    /**
     * <a href="https://sciencing.com/calculate-win-loss-average-8167765.html">Win loss tie ratio formula</a>
     *
     * @param username  the username
     * @param condition the condition
     * @return selection
     */
    private static Selection gamesStats(Object username, Condition condition) {
        Col winner = Col.Winner.of(Table.Games);

        Col countWins = Col.countIf("Wins", Condition.equals(winner, username));
        Col countLosses = Col.countIf("Losses", Condition.notEquals(winner, username).and(Condition.notEquals(winner, TIE_STR)));
        Col countTies = Col.countIf("Ties", Condition.equals(winner, TIE_STR));

        Col[] cols = new Col[]{
                countWins,
                countLosses,
                countTies
        };

        Col gamesPlayed = Col.sum("total games played", countWins, countLosses, countTies);

        Col winLossTieRatio = new Col.CustomCol(countTies.label(), "Win-Loss-Tie Ratio");
        winLossTieRatio.math(Math.Mult, 0.5);
        winLossTieRatio.math(Math.Plus, countWins);
        winLossTieRatio.math(Math.Div, gamesPlayed.colName());
        Selection selection = new Selection(Table.Games, condition, cols);
//        selection = selection.nestMe(gamesPlayed, winLossTieRatio);
        selection = selection.nestMe(ArrUtils.concat(new Col[]{gamesPlayed, winLossTieRatio}, cols));
        return selection;
    }

    /**
     * Sets sub builder.
     *
     * @param subBuilder the sub builder
     */
    public void setSubBuilder(RequestBuilder subBuilder) {
        this.subBuilder = subBuilder;
    }

    /**
     * Stats by time of day request builder.
     *
     * @return the request builder
     */
    public static RequestBuilder statsByTimeOfDay() {
        Arg username = new Arg(ArgType.Username);
        int hour = 25;

        Condition condition = betweenHours(hour - 2, hour - 1);

        Selection stats = gamesStats(username.repInStr, condition);

        RequestBuilder builder = new RequestBuilder(stats,
                "Stats By Time Of Day",
                "%02d:00 - %02d:00".formatted(hour - 2, hour - 1),
                "",
                username
        );
        hour -= 2;
        RequestBuilder current = builder;
        for (; hour > 1; hour -= 2) {
            current.setSubBuilder(new RequestBuilder(gamesStats(username, betweenHours(hour - 2, hour - 1)), "", "%02d:00 - %02d:00".formatted(hour - 2, hour - 1), "", username));
            current = current.subBuilder;
        }
        return builder;
    }

    /**
     * Between hours condition.
     *
     * @param start the start
     * @param end   the end
     * @return the condition
     */
    private static Condition betweenHours(int start, int end) {
        return Condition.between(Col.CreatedDateTime.time(), "'%02d:00'".formatted(start), "'%02d:00'".formatted(end));
    }

    /**
     * Games stats selection.
     *
     * @param username the username
     * @return the selection
     */
    private static Selection gamesStats(Object username) {
        return gamesStats(username, null);
    }

    /**
     * Top request builder.
     *
     * @return the request builder
     */
    public static RequestBuilder top() {
        Col winner = Col.Winner.of(Table.Games);
        Col username = Col.Username.of(Table.Users);

        Col countWins = Col.countIf("Wins", Condition.equals(winner, username));
        Col countLosses = Col.countIf("Losses", Condition.notEquals(winner, username).and(Condition.notEquals(winner, TIE_STR)));
        Col countTies = Col.countIf("Ties", Condition.equals(winner, TIE_STR));

        Col[] cols = new Col[]{
                username.as("Username"),
                countWins,
                countLosses,
                countTies
        };

        Selection selection = new Selection(Table.Users, cols);
        selection.join(Selection.Join.LEFT, Table.Games, p1_OR_p2(username), username);

        Col gamesPlayed = Col.sum("num of games played", countWins, countLosses, countTies);

        Col winLossTieRatio = new Col.CustomCol(countTies.label(), "Win-Loss-Tie Ratio");
        winLossTieRatio.math(Math.Mult, 0.5);
        winLossTieRatio.math(Math.Plus, countWins);
        winLossTieRatio.math(Math.Div, gamesPlayed.colName());

        Arg topNum = new Arg(ArgType.Number, new Config<>("Number Of Players", 0, "all players"));

        selection = selection.nestMe(Col.Username, winLossTieRatio, gamesPlayed);
        selection.top(topNum.repInStr);
        selection.orderBy(winLossTieRatio, Selection.Order.DESC);

        Col numOfGames = Col.count("Total Games");
        Selection summery = new Selection(Table.Games, new Object[]{numOfGames});

        RequestBuilder builder = new RequestBuilder(selection,
                "Top Players",
                "Top %s Players".formatted(topNum.repInStr),
                "Get Top Players",
                topNum);
        builder.setSubBuilder(new RequestBuilder(summery, "", topNum));
        return builder;
    }

    /**
     * Gets should sync.
     *
     * @return the should sync
     */
    public ArrayList<SyncedListType> getShouldSync() {
        return shouldSync;
    }

    /**
     * Gets arg val.
     *
     * @param index the index
     * @return the arg val
     */
    public String getArgVal(int index) {
        if (args[index].escape)
            return builtArgsVals[index] != null ? builtArgsVals[index].replaceAll("^'(.*)'$", "$1") : null;
        return builtArgsVals[index];
    }

    /**
     * Create response db response.
     *
     * @param rs      the rs
     * @param request the request
     * @return the db response
     */
    public DBResponse createResponse(ResultSet rs, DBRequest request) {
        try {
            String[] columns;
            String[][] rows;

            ResultSetMetaData rsmd = rs.getMetaData();
            int colsNum = rsmd.getColumnCount();
            columns = new String[colsNum];

            // Create Table Title (Columns Names)
            for (int i = 1; i <= colsNum; i++) {
                columns[i - 1] = rsmd.getColumnLabel(i);

            }
            ArrayList<String[]> rowsList = new ArrayList<>();
            // Create All Table Rows
            while (rs.next()) {
                String[] strs = new String[colsNum];
                for (int i = 1; i <= colsNum; i++) {
                    String str = rs.getString(i);
//                    str = StrUtils.dontCapWord(str);
                    str = StrUtils.isEmpty(str) ? "0" : str;
                    strs[i - 1] = str;
                }
                rowsList.add(strs);
            }
            rows = rowsList.toArray(new String[0][]);
            return new TableDBResponse(columns, rows, request);
        } catch (SQLException e) {
            e.printStackTrace();
            return new StatusResponse(DBResponse.Status.ERROR, request, 0);
        }
    }

    /**
     * Gets pre description.
     *
     * @return the pre description
     */
    public String getPreDescription() {
        return preDescription;
    }

    /**
     * Gets post description.
     *
     * @return the post description
     */
    public String getPostDescription() {
        return postDescription;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get args arg [ ].
     *
     * @return the arg [ ]
     */
    public Arg[] getArgs() {
        return args;
    }

    /**
     * Build db request.
     *
     * @param argsVals the args vals
     * @return the db request
     */
    public DBRequest build(Object... argsVals) {
        assert this.args.length == argsVals.length;
        this.builtArgsVals = new String[argsVals.length];
        for (int i = 0; i < args.length; i++) {
            Arg arg = args[i];
            String argVal = arg.createVal(argsVals[i]);
            builtArgsVals[i] = argVal;
            statement.replace(arg.repInStr, argVal);
            postDescription = postDescription.replaceAll(arg.repInStr, argVal);
            preDescription = preDescription.replaceAll(arg.repInStr, argVal);
        }
        DBRequest ret = new DBRequest(statement, this);
        ret.setSubRequest(subBuilder == null ? null : subBuilder.build(argsVals));
        return ret;
    }


    /**
     * Graph-able selection - a selection that its response is graph-able.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class GraphableSelection extends RequestBuilder {

        /**
         * The Elements.
         */
        protected ArrayList<GraphElement> elements;

        /**
         * Instantiates a new Graphable selection.
         *
         * @param selection       the selection
         * @param name            the name
         * @param postDescription the post description
         * @param preDescription  the pre description
         * @param args            the args
         */
        public GraphableSelection(Selection selection, String name, String postDescription, String preDescription, Arg... args) {
            super(selection, name, postDescription, preDescription, args);
        }

        /**
         * To string string.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return super.toString();
        }

        /**
         * Create response graphable db response.
         *
         * @param rs      the rs
         * @param request the request
         * @return the graphable db response
         */
        @Override
        public GraphableDBResponse createResponse(ResultSet rs, DBRequest request) {
            try {
                GraphableSelection selection = this;
                while (rs.next() && selection != null) {
                    selection.setElements(rs);
                    selection = (GraphableSelection) subBuilder;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return new GraphableDBResponse(DBResponse.Status.SUCCESS, request) {
                @Override
                public boolean isAnyData() {
                    return true;
                }

                @Override
                public DBResponse clean() {
                    return this;
                }

                @Override
                public String header() {
                    return getPostDescription();
                }

                @Override
                public GraphElement[] elements() {
                    return elements.toArray(new GraphElement[0]);
                }
            };

//            return;
        }

        /**
         * Sets elements.
         *
         * @param rs the rs
         * @throws SQLException the sql exception
         */
        protected void setElements(ResultSet rs) throws SQLException {
            elements = new ArrayList<>();

            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {

                GraphElement element = new GraphElement(rs.getDouble(i), rsmd.getColumnLabel(i), GraphElementType.GREEN);

                elements.add(element);
            }
        }
    }
}
