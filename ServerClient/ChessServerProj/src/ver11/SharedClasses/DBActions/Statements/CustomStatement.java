package ver11.SharedClasses.DBActions.Statements;

import org.intellij.lang.annotations.Language;
import ver11.SharedClasses.DBActions.DBRequest;

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