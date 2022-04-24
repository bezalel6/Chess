package ver14.SharedClasses.DBActions;

import org.intellij.lang.annotations.MagicConstant;
import ver14.SharedClasses.DBActions.Table.Col;
import ver14.SharedClasses.DBActions.Table.Math;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Condition - represents a condition.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Condition implements Serializable {
    /**
     * The condition parameters.
     */
    private final Object[] parms;
    /**
     * The Str.
     */
    private String str;

    /**
     * Instantiates a new Condition.
     *
     * @param str   the str
     * @param parms the parms
     */
    public Condition(String str, Object... parms) {
        assert parms.length == StrUtils.countMatches(str, "%([sd])");
        this.parms = parms;
        this.str = str.formatted(Arrays.stream(parms).map(Condition::createValueStr).toArray());
        assert StrUtils.countMatches(this.str, "\\(") == StrUtils.countMatches(this.str, "\\)");
    }

    /**
     * Create value str string.
     *
     * @param value the value
     * @return the string
     */
    private static String createValueStr(Object value) {
        if (value instanceof Col col) {
            return col.colName();
        }
        return "'%s'".formatted(value);
    }

    /**
     * Equals condition.
     *
     * @param col   the col
     * @param value the value
     * @return the condition
     */
    public static Condition equals(Object col, Object value) {
        return new Condition("StrComp(%s, %s, 0)=0", col, value).noNulls();
    }

    /**
     * No nulls condition.
     *
     * @return the condition
     */
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

    /**
     * Add condition.
     *
     * @param condition the condition
     * @param relation  the relation
     * @return the condition
     */
    public Condition add(Condition condition, Relation relation) {
        return add(condition, relation, true);
    }

    /**
     * Add condition.
     *
     * @param condition the condition
     * @param relation  the relation
     * @param wrap      the wrap
     * @return THIS condition
     */
    public Condition add(Condition condition, Relation relation, boolean wrap) {
        str += " " + relation + " " + condition.str;
        if (wrap)
            wrap();
        return this;
    }

    /**
     * Wrap.
     */
    public void wrap() {
        str = "(" + str + ")";
    }

    /**
     * Math condition.
     *
     * @param col       the col
     * @param operation the operation
     * @param value     the value
     * @return the condition
     */
    public static Condition math(Object col, @MagicConstant(stringValues = {">", ">=", "<", "<="}) String operation, Object value) {
        //is casting necessary?
        col = new Col(Math.asFloat(col));
        value = new Col(Math.asFloat(value));
        Object opObj = new Col(operation);
        return new Condition("%s %s %s", col, opObj, value) {{
            wrap();
        }};
    }

    /**
     * Between condition.
     *
     * @param col   the col
     * @param start the start
     * @param end   the end
     * @return the condition
     */
    public static Condition between(Object col, Object start, Object end) {

        col = new Col(col + "");
        start = new Col(start + "");
        end = new Col(end + "");

        return new Condition("%s BETWEEN %s AND %s", col, start, end) {{
            wrap();
        }};
    }

    /**
     * Not equals condition.
     *
     * @param col   the col
     * @param value the value
     * @return the condition
     */
    public static Condition notEquals(Object col, Object value) {
        return new Condition("StrComp(%s, %s, 0)<>0", col, value).noNulls();
    }

    /**
     * wraps
     *
     * @param condition the condition
     * @return condition
     */
    public Condition and(Condition condition) {
        return add(condition, Relation.AND, true);
    }

    /**
     * Gets str.
     *
     * @return the str
     */
    public String getStr() {
        return str;
    }

    /**
     * Sets str.
     *
     * @param str the str
     */
    public void setStr(String str) {
        this.str = str;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return str;
    }

    /**
     * Relation - relations between conditions.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public enum Relation {
        /**
         * And relation.
         */
        AND,
        /**
         * Or relation.
         */
        OR
    }


}
