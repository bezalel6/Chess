package ver14.SharedClasses.DBActions.Statements;

import ver14.SharedClasses.DBActions.Condition;
import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.Table.Col;
import ver14.SharedClasses.DBActions.Table.Table;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;


/**
 * Update - a sql update statement.
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
     * @param updating  the updating
     * @param condition the condition
     * @param newValues the new values
     */
    public Update(Table updating, Condition condition, NewValue... newValues) {
        super(DBRequest.Type.Update);
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

    /**
     * New value - a new value for a certain column.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class NewValue implements Serializable {
        /**
         * The Col.
         */
        public final Col col;
        /**
         * The Value.
         */
        public final Object value;

        /**
         * Instantiates a new New value.
         *
         * @param col   the col
         * @param value the value
         */
        public NewValue(Col col, Object value) {
            this.col = col;
            this.value = value;
        }

        /**
         * To string string.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return col.colName() + " = " + value;
        }
    }
}
