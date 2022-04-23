package ver14.SharedClasses.DBActions.Statements;

import org.intellij.lang.annotations.Language;
import ver14.SharedClasses.DBActions.DBRequest.DBRequest;

/*
 * CustomStatement
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * CustomStatement -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * CustomStatement -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class CustomStatement extends SQLStatement {
    private @Language("SQL")
    String statement;

    public CustomStatement(DBRequest.Type type, @Language("SQL") String statement) {
        super(type);
        this.statement = statement;
    }

    @Override
    protected String createStatement() {
        return statement;
    }
}
