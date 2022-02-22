/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ver9.SharedClasses.DBActions;

import org.intellij.lang.annotations.Language;
import ver9.SharedClasses.AuthSettings;

import java.util.Arrays;

public class RequestBuilder {
    public static final String TIE_STR = "~tie";

    /*
    currently, only used to confirm num of provided args when building.
    ordered by format order. todo change to be a smarter ordering system
    */
    private final Arg[] args;
    private final @Language("SQL")
    String request;
    private final DBRequest.Type type;
    private final String name;

    public RequestBuilder(@Language("SQL") String request, DBRequest.Type type, String name, Arg... args) {
        this.request = request;
        this.type = type;
        this.name = name;
        this.args = args;
    }

    public static RequestBuilder winNum() {
        @Language("SQL") String cmd = """
                SELECT
                   COUNT (*)
                 FROM
                   Games
                 WHERE
                   winner LIKE '%s'
                   """;
        return new RequestBuilder(cmd, DBRequest.Type.Select, "Num Of Wins", Arg.Username);
    }

    public static RequestBuilder lossesNum() {
        @Language("SQL") String cmd = """
                DECLARE @username AS NVARCHAR(100)='%s'
                                
                SELECT
                      COUNT (*)
                    FROM
                      Games
                    WHERE @username\040
                    IN q
                AND
                 winner <> @username
                 AND winner <>\040""" + TIE_STR + """
                """;
        cmd = cmd.replaceAll("q", Table.escapeValues(new Object[]{Table.Col.Player1, Table.Col.Player2}, false, true));
        return new RequestBuilder(cmd, DBRequest.Type.Select, "Num Of Losses", Arg.Username);
    }

    public String getName() {
        return name;
    }


    public Arg[] getArgs() {
        return args;
    }

    public DBRequest build(String[] argsStrs) {
        assert this.args.length == argsStrs.length;
        return new DBRequest(request.formatted((Object[]) argsStrs), type);
    }

    @Override
    public String toString() {
        return "RequestBuilder{" +
                "args=" + Arrays.toString(args) +
                ", request='" + request + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }

    public enum Arg {
        Username
    }

    public enum PreMadeRequest {
        WinNum(winNum(), AuthSettings.USER),
        LossesNum(lossesNum(), AuthSettings.ANY_LOGIN);
        public final RequestBuilder builder;
        public final @AuthSettings
        int authSettings;

        PreMadeRequest(RequestBuilder builder, @AuthSettings int authSettings) {
            this.builder = builder;
            this.authSettings = authSettings;
        }
    }
}
