package ver14.SharedClasses.DBActions.Statements;

import org.intellij.lang.annotations.Language;
import ver14.SharedClasses.DBActions.DBRequest.DBRequest;


/**
 * Custom statement - a custom sql statement.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class CustomStatement extends SQLStatement {
    /**
     * The Statement.
     */
    private @Language("SQL")
    String statement;

    /**
     * Instantiates a new Custom statement.
     *
     * @param type      the type
     * @param statement the statement
     */
    public CustomStatement(DBRequest.Type type, @Language("SQL") String statement) {
        super(type);
        this.statement = statement;
    }

    /**
     * Create statement string.
     *
     * @return the string
     */
    @Override
    protected String createStatement() {
        return statement;
    }
}
