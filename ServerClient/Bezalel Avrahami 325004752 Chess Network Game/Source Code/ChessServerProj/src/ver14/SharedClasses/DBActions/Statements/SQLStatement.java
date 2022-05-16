package ver14.SharedClasses.DBActions.Statements;

import org.intellij.lang.annotations.Language;
import ver14.SharedClasses.DBActions.DBRequest.Type;

import java.io.Serializable;


/**
 * represents an sql statement. with a {@link Type}.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class SQLStatement implements Serializable {

    /**
     * The request Type.
     */
    public final Type type;
    /**
     * The Statement.
     */
    private String statement = null;

    /**
     * Instantiates a new Sql statement.
     *
     * @param type the sql statement type
     */
    public SQLStatement(Type type) {
        this.type = type;
    }

    /**
     * Replace string in the statement. used to replace argument's placeholders with actual values.
     *
     * @param replacing   the replacing
     * @param replaceWith the replace with
     */
    public void replace(String replacing, String replaceWith) {
        this.statement = createIfNotCreated().replaceAll(replacing, replaceWith);
    }

    /**
     * Create the statement if you haven't already.
     *
     * @return the statement
     */
    private String createIfNotCreated() {
        if (statement == null) {
            statement = createStatement();
        }
        return statement;
    }

    /**
     * Create statement string.
     *
     * @return the created sql string
     */
    protected abstract @Language("SQL")
    String createStatement();

    /**
     * Gets the statement.
     *
     * @return the statement
     */
    public String getStatement() {
        return createIfNotCreated();
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "SQLStatement{" +
                "type=" + type +
                ", statement='" + statement + '\'' +
                '}';
    }

}
