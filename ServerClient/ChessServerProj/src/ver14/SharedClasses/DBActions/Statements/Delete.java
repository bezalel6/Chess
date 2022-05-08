package ver14.SharedClasses.DBActions.Statements;

import ver14.SharedClasses.DBActions.Condition;
import ver14.SharedClasses.DBActions.DBRequest.Type;
import ver14.SharedClasses.DBActions.Table.Table;

/**
 * Delete - deletion statement.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Delete extends SQLStatement {

    /**
     * The Deleting from.
     */
    private final Table deletingFrom;
    /**
     * The Condition.
     */
    private final Condition condition;

    /**
     * Instantiates a new Delete.
     *
     * @param deletingFrom the deleting from
     * @param condition    the condition
     */
    public Delete(Table deletingFrom, Condition condition) {
        super(Type.Update);
        this.condition = condition;
        this.deletingFrom = deletingFrom;
    }

    /**
     * Create statement string.
     *
     * @return the string
     */
    @Override
    protected String createStatement() {
        return "DELETE FROM %s  ".formatted(deletingFrom) + (condition == null ? "" : ("WHERE " + condition));
    }
}
