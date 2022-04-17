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

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class RequestBuilder implements Serializable {
    public static final String TIE_STR = "----tie----";
    public final Arg[] args;
    protected final SQLStatement statement;
    protected final String name;
    protected String postDescription;
    protected String preDescription;
    protected RequestBuilder subBuilder = null;
    private String[] builtArgsVals;


    public RequestBuilder(DBRequest request, PreMadeRequest.Variation variation) {
        this(new CustomStatement(request.type, request.getRequest()), variation.variationName, variation.variationArgs);
    }

    public RequestBuilder(SQLStatement statement, String name, Arg... args) {
        this(statement, name, name, args);
    }

    public RequestBuilder(SQLStatement statement, String name, String desc, Arg... args) {
        this(statement, name, desc, desc, args);
    }

    public RequestBuilder(SQLStatement statement, String name, String postDescription, String preDescription, Arg... args) {
        this.statement = statement;
        this.name = name;
        this.postDescription = postDescription;
        this.preDescription = preDescription;
        this.args = args;
    }

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

    public static RequestBuilder changePassword() {
        Arg username = new Arg(ArgType.Username);
        Arg pw = new Arg(ArgType.Password);
        pw.setUserInput(false);

        Update update = new Update(Table.Users, Condition.equals(Col.Username, username.repInStr), new Update.NewValue(Col.Password, pw.repInStr));

        return new RequestBuilder(update, "change password", "", username, pw);
    }

    public static RequestBuilder changeProfilePic() {
        Arg username = new Arg(ArgType.Username);
        Arg url = new Arg(ArgType.PictureUrl, true, new Config<>("Enter link to the new profile picture"));

        Update update = new Update(Table.Users, Condition.equals(Col.Username, username), new Update.NewValue(Col.ProfilePic, url));

        return new RequestBuilder(update, "change profile picture", "", username, url);
    }

    public static RequestBuilder deleteAllUnFinishedGames() {
        Arg username = new Arg(ArgType.Username);
        Update.Delete del = new Update.Delete(Table.UnfinishedGames, p1_OR_p2(username.repInStr, Table.UnfinishedGames));

        return new RequestBuilder(del, "delete all unfinished games", "", username);
    }

    private static Condition p1_OR_p2(Object un, Table playersOf) {
        Condition condition = Condition.equals(Col.Player1.of(playersOf), un);
        condition.add(Condition.equals(Col.Player2.of(playersOf), un), Condition.Relation.OR, true);
        return condition;
    }

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

        Col date = Col.SavedDateTime.as().of(Table.Games).date();
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
        RequestBuilder sub = new RequestBuilder(selection, "sub", username, start, end);

        return new RequestBuilder(games,
                "Games",
                "All Games For %s between %s and %s".formatted(username.repInStr, start.repInStr, end.repInStr),
                "Get Your Games In Range",
                username,
                start,
                end) {{
            setSubBuilder(sub);
        }};
    }

    private static Condition p1_OR_p2(Object un) {
        return p1_OR_p2(un, Table.Games);
    }

    /**
     * <a href="https://sciencing.com/calculate-win-loss-average-8167765.html">Win loss tie ratio formula</a>
     *
     * @param username
     * @param condition
     * @return
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
//        selection.join(Selection.Join.LEFT, Table.Games, p1_Or_p2(username), username);

        Col gamesPlayed = Col.sum("total games played", countWins, countLosses, countTies);

        Col winLossTieRatio = new Col.CustomCol(countTies.label(), "Win-Loss-Tie Ratio");
        winLossTieRatio.math(Math.Mult, 0.5);
        winLossTieRatio.math(Math.Plus, countWins);
        winLossTieRatio.math(Math.Div, gamesPlayed.colName());
        Selection selection = new Selection(Table.Games, condition, cols);
        selection = selection.nestMe(gamesPlayed, winLossTieRatio);
//        selection = selection.nestMe(ArrUtils.concat(cols, gamesPlayed, winLossTieRatio));
        return selection;
    }

    public void setSubBuilder(RequestBuilder subBuilder) {
        this.subBuilder = subBuilder;
    }

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

    private static Condition betweenHours(int start, int end) {
        return Condition.between(Col.SavedDateTime.time(), "'%02d:00'".formatted(start), "'%02d:00'".formatted(end));
    }

    private static Selection gamesStats(Object username) {
        return gamesStats(username, null);
    }

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
        builder.setSubBuilder(new RequestBuilder(summery, "sub", topNum));
        return builder;
    }

    public String getArgVal(int index) {
        if (args[index].escape)
            return builtArgsVals[index] != null ? builtArgsVals[index].replaceAll("^'(.*)'$", "$1") : null;
        return builtArgsVals[index];
    }

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

    public String getPreDescription() {
        return preDescription;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public String getName() {
        return name;
    }

    public Arg[] getArgs() {
        return args;
    }

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

//    /**
//     * only relevant after requesting
//     *
//     * @param arg
//     * @return
//     */
//    public String getArgVal(Arg arg) {
//        return
//    }

    public static class GraphableSelection extends RequestBuilder {

        protected ArrayList<GraphElement> elements;

        public GraphableSelection(Selection selection, String name, String postDescription, String preDescription, Arg... args) {
            super(selection, name, postDescription, preDescription, args);
        }

        @Override
        public String toString() {
            return super.toString();
        }

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
