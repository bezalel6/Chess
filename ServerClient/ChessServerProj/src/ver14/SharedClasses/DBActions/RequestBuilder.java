package ver14.SharedClasses.DBActions;

import ver14.SharedClasses.Callbacks.ObjCallback;
import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.Arg.ArgType;
import ver14.SharedClasses.DBActions.Arg.Config;
import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.SharedClasses.DBActions.Statements.CustomStatement;
import ver14.SharedClasses.DBActions.Statements.SQLStatement;
import ver14.SharedClasses.DBActions.Statements.Selection;
import ver14.SharedClasses.DBActions.Statements.Update;
import ver14.SharedClasses.DBActions.Table.Col;
import ver14.SharedClasses.DBActions.Table.Math;
import ver14.SharedClasses.DBActions.Table.SwitchCase;
import ver14.SharedClasses.DBActions.Table.Table;
import ver14.SharedClasses.Utils.ArrUtils;

import java.util.Date;

public class RequestBuilder {
    public static final String TIE_STR = "----tie----";
    public final Arg[] args;
    private final SQLStatement statement;
    private final String name;
    private String postDescription;
    private String preDescription;
    private RequestBuilder subBuilder = null;

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

    public static RequestBuilder games() {
        Arg username = new Arg(ArgType.Username);
        Col opponent = Col.switchCase(
                "Opponent",
                SwitchCase.equals(Col.Player1, username.repInStr, Col.Player2),
                SwitchCase.defaultCase(Col.Player1)
        );

        Arg start = new Arg(ArgType.Date, new Config<>("starting date", new Date(0)));
        Arg end = new Arg(ArgType.Date, new Config<>("ending date", new Date(), "Now"));

        Condition condition = p1_Or_p2(username.repInStr);

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

    private static Condition p1_Or_p2(Object un) {
        Condition condition = Condition.equals(Col.Player1.of(Table.Games), un);
        condition.add(Condition.equals(Col.Player2.of(Table.Games), un), Condition.Relation.OR, true);
        return condition;
    }

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
        selection = selection.nestMe(ArrUtils.concat(cols, gamesPlayed, winLossTieRatio));
        return selection;
    }

    public void setSubBuilder(RequestBuilder subBuilder) {
        this.subBuilder = subBuilder;
    }

    private static Selection gamesStats(Object username) {
        return gamesStats(username, null);
    }

    /**
     * <a href="https://sciencing.com/calculate-win-loss-average-8167765.html">Win loss tie ratio formula</a>
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
        selection.join(Selection.Join.LEFT, Table.Games, p1_Or_p2(username), username);

        Col gamesPlayed = Col.sum("num of games played", countWins, countLosses, countTies);

        Col winLossTieRatio = new Col.CustomCol(countTies.label(), "Win-Loss-Tie Ratio");
        winLossTieRatio.math(Math.Mult, 0.5);
        winLossTieRatio.math(Math.Plus, countWins);
        winLossTieRatio.math(Math.Div, gamesPlayed.colName());

        Arg topNum = new Arg(ArgType.Number, new Config<>("Number Of Players", 0, "all players"));

        selection = selection.nestMe(ArrUtils.concat(cols, gamesPlayed, winLossTieRatio));
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

    public static RequestBuilder select() {
        return new RequestBuilder(new Selection(Table.Games, new Object[0]), "selecting something");
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
        for (int i = 0; i < args.length; i++) {
            Arg arg = args[i];
            String argVal = arg.createVal(argsVals[i]);
            statement.replace(arg.repInStr, argVal);
            postDescription = postDescription.replaceAll(arg.repInStr, argVal);
            preDescription = preDescription.replaceAll(arg.repInStr, argVal);
        }
        DBRequest ret = new DBRequest(statement);
        ret.setSubRequest(subBuilder == null ? null : subBuilder.build(argsVals));
        return ret;
    }
}
