package ver14.SharedClasses.DBActions.Statements.Update;

import ver14.SharedClasses.DBActions.Condition;
import ver14.SharedClasses.DBActions.DBRequest.Type;
import ver14.SharedClasses.DBActions.Statements.SQLStatement;
import ver14.SharedClasses.DBActions.Table.Table;
import ver14.SharedClasses.Utils.StrUtils;


/**
 * represents an update statement.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Update extends SQLStatement {
    /**
     * The Table Updating.
     */
    private final Table updating;
    /**
     * The New values.
     */
    private final NewValue[] newValues;
    /**
     * The Condition.
     */
    private final Condition condition;

    /**
     * Instantiates a new Update.
     *
     * @param updating  the {@link Table} to be updated
     * @param condition the condition
     * @param newValues the new values
     */
    public Update(Table updating, Condition condition, NewValue... newValues) {
        super(Type.Update);
        assert newValues.length > 0;
        this.updating = updating;
        this.condition = condition;
        this.newValues = newValues;
    }

    /**
     * Create statement string.
     *
     * @return the string
     */
    @Override
    protected String createStatement() {
        return "UPDATE %s\nSET %s\nWHERE %s".formatted(updating, StrUtils.splitArr(newValues), condition);
    }

}
