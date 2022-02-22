package ver12.SharedClasses.DBActions.Table;

public enum Math {
    Plus {
        @Override
        protected void apply(Object value) {
            simpleOperation(strSource(), "+", strVal(value));
        }
    }, Mult {
        @Override
        protected void apply(Object value) {
            simpleOperation(formatNum(strSource()), "*", formatNum(strVal(value)));
        }
    }, Div {
        @Override
        protected void apply(Object value) {
            simpleOperation(formatNum(strSource()), "/", nullIf0(value));
            zeroIfNull();
        }
    };
    Col col;

    public static String nullIf0(Object val) {
        return "NULLIF(" + formatNum(strVal(val)) + ",0)";
    }

    public static String formatNum(Object num) {
        return formatNum(num, "DECIMAL(16,3)");
    }

    public static String strVal(Object val) {
        return str(val);
    }

    public static String formatNum(Object num, String format) {
        String prep = "CAST(%s AS %s)".formatted("%s", format);
        return prep.formatted(num);
    }

    public static String str(Object obj) {
        return obj instanceof Col col ? col.label() : obj.toString();
    }

    public static String asFloat(Object num) {
        return formatNum(num, "float");
    }

    protected void zeroIfNull() {
        col.setColName("IFNULL(%s,0)".formatted(col.colName()));
    }

    public Col execute(Col col, Object value) {
        return execute(col, value, true);

    }

    public Col execute(Col col, Object value, boolean changeSelf) {
        this.col = changeSelf ? col : new Col(col);
        apply(value);
        return col;
    }

    protected abstract void apply(Object value);

    String strSource() {
        return col.colName();
    }

    protected void simpleOperation(Object a, String operation, Object b) {
        col.setColName("%s %s %s".formatted(a, operation, b));
    }

}
