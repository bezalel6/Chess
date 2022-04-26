package ver14.SharedClasses.DBActions.Table;


/**
 * Math - allows for math actions on columns and some math-related utilities for columns.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum Math {
    /**
     * The Plus.
     */
    Plus {
        @Override
        protected void apply(Object value) {
            simpleOperation(strSource(), "+", strVal(value));
        }
    },
    /**
     * The Mult.
     */
    Mult {
        @Override
        protected void apply(Object value) {
            simpleOperation(formatNum(strSource()), "*", formatNum(strVal(value)));
        }
    },
    /**
     * The Div.
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
     * Null if 0 string.
     *
     * @param val the val
     * @return the string
     */
    public static String nullIf0(Object val) {
        return "NULLIF(" + formatNum(strVal(val)) + ",0)";
    }

    /**
     * Format num string.
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
     * Format num string.
     *
     * @param num    the num
     * @param format the format
     * @return the string
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
     * As float string.
     *
     * @param num the num
     * @return the string
     */
    public static String asFloat(Object num) {
        return formatNum(num, "float");
    }

    /**
     * Zero if null.
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
     * Execute col.
     *
     * @param col        the col
     * @param value      the value
     * @param changeSelf the change self
     * @return the col
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
