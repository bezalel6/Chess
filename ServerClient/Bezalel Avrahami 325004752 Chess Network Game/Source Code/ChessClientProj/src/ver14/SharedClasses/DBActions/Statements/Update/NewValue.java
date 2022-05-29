package ver14.SharedClasses.DBActions.Statements.Update;

import ver14.SharedClasses.DBActions.Table.Col;

import java.io.Serializable;

/**
 * represents a new value for a certain column in a table in the db.
 *
 * @param col   The Column to update.
 * @param value The New Value.
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public record NewValue(Col col, Object value) implements Serializable {
   

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
