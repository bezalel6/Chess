package ver14.SharedClasses.DBActions.Statements;

import org.intellij.lang.annotations.Language;
import ver14.SharedClasses.DBActions.DBRequest.DBRequest;

import java.io.Serializable;


/**
 * Sql statement - represents an sql statement.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class SQLStatement implements Serializable {

    /**
     * The request Type.
     */
    public final DBRequest.Type type;
    /**
     * The Statement.
     */
    private String statement = null;

    /**
     * Instantiates a new Sql statement.
     *
     * @param type the type
     */
    public SQLStatement(DBRequest.Type type) {
        this.type = type;
    }

    /**
     * Replace.
     *
     * @param replacing   the replacing
     * @param replaceWith the replace with
     */
    public void replace(String replacing, String replaceWith) {
        this.statement = createIfNotCreated().replaceAll(replacing, replaceWith);
    }

    /**
     * Create if not created string.
     *
     * @return the string
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
     * @return the string
     */
    protected abstract @Language("SQL")
    String createStatement();

    /**
     * Gets statement.
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
