package ver10.SharedClasses.DBActions;

import ver10.SharedClasses.AuthSettings;
import ver10.SharedClasses.Callbacks.ObjCallback;
import ver10.SharedClasses.DBActions.Arg.Arg;
import ver10.SharedClasses.DBActions.Arg.ArgType;
import ver10.SharedClasses.DBActions.Arg.Config;
import ver10.SharedClasses.DBActions.Statements.CustomStatement;
import ver10.SharedClasses.DBActions.Statements.SQLStatement;
import ver10.SharedClasses.DBActions.Statements.Selection;
import ver10.SharedClasses.DBActions.Statements.Update;
import ver10.SharedClasses.DBActions.Table.Col;
import ver10.SharedClasses.DBActions.Table.Math;
import ver10.SharedClasses.DBActions.Table.SwitchCase;
import ver10.SharedClasses.DBActions.Table.Table;
import ver10.SharedClasses.Utils.ArrUtils;
import ver10.SharedClasses.Utils.StrUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RequestBuilder {
    public static final String TIE_STR = "----tie----";
    private final Arg[] args;
    private final SQLStatement statement;
    private final String name;
    private String postDescription;
    private String preDescription;

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

    public static RequestBuilder changePassword() {
        Arg username = new Arg(ArgType.Username);
        Arg pw = new Arg(ArgType.Password);
        pw.setUserInput(false);

        Update update = new Update(Table.Users, Condition.equals(Col.Username, username.repInStr), new Update.NewValue(Col.Password, pw.repInStr));

        return new RequestBuilder(update, "change password", "", username, pw);
    }

    public static RequestBuilder games() {
        Arg username = new Arg(ArgType.Username);
        Col switchCase = Col.switchCase(
                "Opponent",
                SwitchCase.equals(Col.Player1, username.repInStr, Col.Player2),
                SwitchCase.defaultCase(Col.Player1)
        );

        Arg start = new Arg(ArgType.Date, new Config<>("starting date", new Date(0)));
        Arg end = new Arg(ArgType.Date, new Config<>("ending date", new Date(), "Now"));

        Condition condition = p1ORp2(username.repInStr);

        Col date = Col.SavedDateTime.as().of(Table.Games).date();
        condition = condition.and(Condition.between(date.colName(), start.repInStr, end.repInStr));

        Selection games = new Selection(
                Table.Games,
                condition,
                switchCase,
                Col.Winner.as().of(Table.Games),
                date
        );
        String prefixedS = StrUtils.dateTimePrefix(start.repInStr);
        String prefixedE = StrUtils.dateTimePrefix(end.repInStr);
        games.orderBy(date, Selection.Order.DESC);
        return new RequestBuilder(games,
                "Games In Range",
                "All Games For %s between %s and %s".formatted(username.repInStr, prefixedS, prefixedE),
                "Get Your Games In Range",
                username,
                start,
                end);
    }

    private static Condition p1ORp2(Object un) {
        Condition condition = Condition.equals(Col.Player1.of(Table.Games), un);
        condition.add(Condition.equals(Col.Player2.of(Table.Games), un), Condition.Relation.OR, true);
        return condition;
    }

    /**
     * <a href="https://sciencing.com/calculate-win-loss-average-8167765.html">Win loss tie ratio formula</a>
     *
     * @return
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
        selection.join(Selection.Join.LEFT, Table.Games, p1ORp2(username), username);

        Col gamesPlayed = Col.sum("num of games played", countWins, countLosses, countTies);

        Col winLossTieRatio = new Col.CustomCol(countTies.label(), "Win-Loss-Tie Ratio");
        winLossTieRatio.math(Math.Mult, 0.5);
        winLossTieRatio.math(Math.Plus, countWins);
        winLossTieRatio.math(Math.Div, gamesPlayed.colName());

        Arg topNum = new Arg(ArgType.Number, new Config<>("Number Of Players", 0, "all players"));

        selection = selection.nestMe(ArrUtils.concat(cols, gamesPlayed, winLossTieRatio));
        selection.top(topNum.repInStr);
        selection.orderBy(winLossTieRatio, Selection.Order.DESC);

        return new RequestBuilder(selection,
                "Top",
                "Top %s Players".formatted(topNum.repInStr),
                "Get Top Players",
                topNum);
    }

//    public static RequestBuilder changePassword() {
//        Arg newPassword = new Arg(ArgType.Number);
//    }

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
//        assert this.args.length == argsVals.length;
        for (int i = 0; i < args.length; i++) {
            Arg arg = args[i];
            String argVal = arg.createVal(argsVals[i]);
            statement.replace(arg.repInStr, argVal);
            postDescription = postDescription.replaceAll(arg.repInStr, argVal);
        }
        return new DBRequest(statement);
    }


    public static class PreMadeRequest {
        public static final PreMadeRequest Top = new PreMadeRequest(RequestBuilder::top, AuthSettings.ANY_LOGIN);

        public static final PreMadeRequest TopFive = new PreMadeRequest(() -> {
            String s = top().build(5).getRequest();
            return new RequestBuilder(new CustomStatement(DBRequest.Type.Query, s), "Top Five", "Top Five Players");
        }, AuthSettings.ANY_LOGIN);

        public static final PreMadeRequest GamesInRange = new PreMadeRequest(RequestBuilder::games, AuthSettings.USER);

        public static final PreMadeRequest Games = new PreMadeRequest(() -> {
            RequestBuilder og = games();
            Arg un = og.args[0];
            String s = og.build(un.repInStr, new Date(0), new Date()).getRequest();
            return new RequestBuilder(new CustomStatement(DBRequest.Type.Query, s), "All Games", "Get All Games", un);
        }, AuthSettings.USER);

        public static final PreMadeRequest GamesFromLastWeek = new PreMadeRequest(() -> {
            RequestBuilder og = games();
            Arg un = og.args[0];
            String s = og.build(un.repInStr, new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)), new Date()).getRequest();
            return new RequestBuilder(new CustomStatement(DBRequest.Type.Query, s), "Games from last week", "Get Games from last week", un);
        }, AuthSettings.USER);

        public final static PreMadeRequest[] statistics = {Top, TopFive, GamesInRange, Games, GamesFromLastWeek};

        public final @AuthSettings
        int authSettings;
        private final ObjCallback<RequestBuilder> builderBuilder;


        PreMadeRequest(ObjCallback<RequestBuilder> builderBuilder, @AuthSettings int authSettings) {
            this.builderBuilder = builderBuilder;
            this.authSettings = authSettings;
        }

        public RequestBuilder createBuilder() {
            return builderBuilder.get();
        }

    }
}
