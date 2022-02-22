package ver10.SharedClasses.DBActions.Statements;

import org.intellij.lang.annotations.Language;
import ver10.SharedClasses.DBActions.DBRequest;

import java.io.Serializable;

public abstract class SQLStatement implements Serializable {

    public final DBRequest.Type type;
    private String statement = null;

    public SQLStatement(DBRequest.Type type) {
        this.type = type;
    }

    public void replace(String replacing, String replaceWith) {
        createIfNotCreated();
        statement = statement.replaceAll(replacing, replaceWith);
    }

    private String createIfNotCreated() {
        if (statement == null) {
            statement = createStatement();
        }
        return statement;
    }

    protected abstract @Language("SQL")
    String createStatement();

    public String getStatement() {
        return createIfNotCreated();
    }

    @Override
    public String toString() {
        return "SQLStatement{" +
                "type=" + type +
                ", statement='" + statement + '\'' +
                '}';
    }

}
