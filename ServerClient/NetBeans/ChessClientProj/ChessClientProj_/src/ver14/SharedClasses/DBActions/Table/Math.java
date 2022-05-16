package ver14.SharedClasses.DBActions.Table;


/**
 * Math - allows for math operations on columns and some math-related utilities for columns.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum Math {
    /**
     * add
     */
    Plus {
        @Override
        protected void apply(Object value) {
            simpleOperation(strSource(), "+", strVal(value));
        }
    },
    /**
     * multiply
     */
    Mult {
        @Override
        protected void apply(Object value) {
            simpleOperation(formatNum(strSource()), "*", formatNum(strVal(value)));
        }
    },
    /**
     * divide
     */
    Div {
        @Override
        protected void apply(Object value) {
            simpleOperation(formatNum(strSource()), "/", nullIf0(value));
            zeroIfNull();
        }
    };
    /**
     * The Col.
     */
    Col col;

    /**
     * to avoid dividing by 0, if the value is equal to 0, it will be replaced with null, and then (if setup correctly) will be handled.
     * one way of handling with nulls is by using {@link #zeroIfNull()}
     *
     * @param val the val
     * @return the string
     */
    public static String nullIf0(Object val) {
        return "NULLIF(" + formatNum(strVal(val)) + ",0)";
    }

    /**
     * Format a num with a default 3 decimal places.
     *
     * @param num the num
     * @return the string
     */
    public static String formatNum(Object num) {
        return formatNum(num, "DECIMAL(16,3)");
    }

    /**
     * Str val string.
     *
     * @param val the val
     * @return the string
     */
    public static String strVal(Object val) {
        return str(val);
    }

    /**
     * cast col to be in a number format.
     *
     * @param num    the num
     * @param format the format
     * @return the formatted string
     */
    public static String formatNum(Object num, String format) {
        String prep = "CAST(%s AS %s)".formatted("%s", format);
        return prep.formatted(num);
    }

    /**
     * Str string.
     *
     * @param obj the obj
     * @return the string
     */
    public static String str(Object obj) {
        return obj instanceof Col col ? col.label() : obj.toString();
    }

    /**
     * cast value as float.
     *
     * @param num the num
     * @return the formatted value
     */
    public static String asFloat(Object num) {
        return formatNum(num, "float");
    }

    /**
     * if the col's value is null, it will be replaced with a 0.
     */
    protected void zeroIfNull() {
        col.setColName("IFNULL(%s,0)".formatted(col.colName()));
    }

    /**
     * Execute col.
     *
     * @param col   the col
     * @param value the value
     * @return the col
     */
    public Col execute(Col col, Object value) {
        return execute(col, value, true);

    }

    /**
     * Execute this operation on the passed col, or on a copy of it.
     *
     * @param col        the col
     * @param value      the value for the right side of this operation
     * @param changeSelf {@code true} to change the column passed as a parameter.
     * @return the changed column
     */
    public Col execute(Col col, Object value, boolean changeSelf) {
        this.col = changeSelf ? col : new Col(col);
        apply(value);
        return col;
    }

    /**
     * Apply.
     *
     * @param value the value
     */
    protected abstract void apply(Object value);

    /**
     * Str source string.
     *
     * @return the string
     */
    String strSource() {
        return col.colName();
    }

    /**
     * Simple operation.
     *
     * @param a         the a
     * @param operation the operation
     * @param b         the b
     */
    protected void simpleOperation(Object a, String operation, Object b) {
        col.setColName("%s %s %s".formatted(a, operation, b));
    }

}
