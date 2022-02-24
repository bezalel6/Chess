package ver12.SharedClasses.DBActions.Statements;

import ver12.SharedClasses.DBActions.Condition;
import ver12.SharedClasses.DBActions.DBRequest.DBRequest;
import ver12.SharedClasses.DBActions.Table.Col;
import ver12.SharedClasses.DBActions.Table.Table;
import ver12.SharedClasses.Utils.StrUtils;

public class Update extends SQLStatement {
    private final Table updating;
    private final NewValue[] newValues;
    private final Condition condition;

    public Update(Table updating, Condition condition, NewValue... newValues) {
        super(DBRequest.Type.Update);
        assert newValues.length > 0;
        this.updating = updating;
        this.condition = condition;
        this.newValues = newValues;
    }

    @Override
    protected String createStatement() {
        return "UPDATE %s\nSET %s\nWHERE %s".formatted(updating, StrUtils.splitArr(newValues), condition);
    }

    public static class NewValue {
        public final Col col;
        public final Object value;

        public NewValue(Col col, Object value) {
            this.col = col;
            this.value = value;
        }

        @Override
        public String toString() {
            return col.colName() + " = " + value;
        }
    }
}
