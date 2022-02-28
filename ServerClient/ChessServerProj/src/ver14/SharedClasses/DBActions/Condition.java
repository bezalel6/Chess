package ver14.SharedClasses.DBActions;

import org.intellij.lang.annotations.MagicConstant;
import ver14.SharedClasses.DBActions.Table.Col;
import ver14.SharedClasses.DBActions.Table.Math;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Arrays;

public class Condition implements Serializable {
    private final Object[] parms;
    private String str;

    public Condition(String str, Object... parms) {
        assert parms.length == StrUtils.countMatches(str, "%([sd])");
        this.parms = parms;
        this.str = str.formatted(Arrays.stream(parms).map(Condition::createValueStr).toArray());
        assert StrUtils.countMatches(this.str, "\\(") == StrUtils.countMatches(this.str, "\\)");
    }

    private static String createValueStr(Object value) {
        if (value instanceof Col col) {
            return col.colName();
        }
        return "'%s'".formatted(value);
    }

    public static Condition equals(Object col, Object value) {
        return new Condition("StrComp(%s, %s, 0)=0", col, value).noNulls();
    }

    public Condition noNulls() {
        Object[] colsParms = Arrays.stream(parms)
                .filter(parm -> parm instanceof Col)
                .toArray();
        if (colsParms.length == 0)
            return this;
        String notNull = " IS NOT NULL ";
        String str = StrUtils.splitArr(notNull + "AND ", colsParms) + notNull;
        Condition condition = new Condition(str);
        condition.add(this, Relation.AND);
        return condition;
    }

    public Condition add(Condition condition, Relation relation) {
        return add(condition, relation, true);
    }

    /**
     * @return <h1>THIS</h1>
     */
    public Condition add(Condition condition, Relation relation, boolean wrap) {
        str += " " + relation + " " + condition.str;
        if (wrap)
            wrap();
        return this;
    }

    public void wrap() {
        str = "(" + str + ")";
    }

    public static Condition math(Object col, @MagicConstant(stringValues = {">", ">=", "<", "<="}) String operation, Object value) {
        //is casting necessary?
        col = new Col(Math.asFloat(col));
        value = new Col(Math.asFloat(value));
        Object opObj = new Col(operation);
        return new Condition("%s %s %s", col, opObj, value) {{
            wrap();
        }};
    }

    public static Condition between(Object col, Object start, Object end) {

        col = new Col(col + "");
        start = new Col(start + "");
        end = new Col(end + "");

        return new Condition("%s BETWEEN %s AND %s", col, start, end) {{
            wrap();
        }};
    }

    public static Condition notEquals(Object col, Object value) {
        return new Condition("StrComp(%s, %s, 0)<>0", col, value).noNulls();
    }

    /**
     * wraps
     *
     * @param condition
     * @return
     */
    public Condition and(Condition condition) {
        return add(condition, Relation.AND, true);
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }

    public enum Relation {
        AND, OR
    }


}
