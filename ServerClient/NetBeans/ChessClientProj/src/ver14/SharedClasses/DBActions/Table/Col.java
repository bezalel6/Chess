package ver14.SharedClasses.DBActions.Table;

import ver14.SharedClasses.DBActions.Condition;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Col - represents a column. either existing column in the db (the constant columns {@link #GameID},
 * {@link #SavedGame} ...) or created columns.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Col implements Serializable {

    /**
     * The constant GameID.
     */
    public static final Col GameID = new Col("GameID");
    /**
     * The constant SavedGame.
     */
    public static final Col SavedGame = new Col("SavedGame");
    /**
     * The constant Password.
     */
    public static final Col Password = new Col("password");
    /**
     * The constant Player1.
     */
    public static final Col Player1 = new Col("Player1");
    /**
     * The constant Player2.
     */
    public static final Col Player2 = new Col("Player2");
    /**
     * The constant Username.
     */
    public static final Col Username = new Col("username");
    /**
     * The constant Winner.
     */
    public static final Col Winner = new Col("Winner");
    /**
     * The constant PlayerToMove.
     */
    public static final Col PlayerToMove = new Col("PlayerToMove");
    /**
     * The constant CreatedDateTime.
     */
    public static final Col CreatedDateTime = new Col("CreatedDateTime");
    /**
     * The constant ProfilePic.
     */
    public static final Col ProfilePic = new Col("ProfilePic");

    /**
     * The column's Alias.
     */
    private final String alias;
    /**
     * The Col name.
     */
    private String colName;
    /**
     * Is wrapped in parentheses.
     */
    private boolean isWrapped = false;

    /**
     * Instantiates a new Col.
     *
     * @param col the col
     */
    public Col(Col col) {
        this(col.colName, col.alias);
        this.isWrapped = col.isWrapped;
    }

    /**
     * Instantiates a new Col.
     *
     * @param colName the col name
     * @param alias   the alias
     */
    public Col(String colName, String alias) {
        this.colName = colName;
        this.alias = StrUtils.format(alias);
    }

    /**
     * Instantiates a new Col.
     *
     * @param colName the col name
     */
    public Col(String colName) {
        this(colName, "");
    }

    /**
     * Count col.
     * counts every row (*)
     *
     * @param as the alias
     * @return the col
     */
    public static Col count(String as) {
        return count(as, "*");
    }

    /**
     * Count col.
     *
     * @param as        the alias
     * @param countWhat the count what
     * @return the col
     */
    public static Col count(String as, Object countWhat) {
        return new Col("COUNT(%s)".formatted(countWhat), as);
    }

    /**
     * Count if col.
     * only counts if the given condition is true
     *
     * @param as        the as
     * @param condition the condition
     * @return the col
     */
    public static Col countIf(String as, Condition condition) {
        return new Col("SUM(IIf(\n\t%s,1,0\n))".formatted(condition), as);
    }

    /**
     * Sum cols.
     *
     * @param as        the alias
     * @param colsToSum the cols to sum
     * @return the custom col
     */
    public static CustomCol sum(String as, Col... colsToSum) {
        return new CustomCol(StrUtils.splitArr("+", Arrays.stream(colsToSum).map(Col::label).toArray()), as);
    }

    /**
     * Label string.
     *
     * @return the string
     */
    public String label() {
        return alias.equals("") ? colName : ("[%s]".formatted(alias));
    }

    /**
     * Switch case col.
     *
     * @param as    the as
     * @param cases the cases
     * @return the col
     */
    public static Col switchCase(String as, SwitchCase... cases) {
        assert cases.length > 0;
        String f = "\n\t";
        Col col = new Col("SWITCH(%s\n)".formatted(f + StrUtils.splitArr("," + f, cases)), as);
        col.wrap();
        return col;
    }

    /**
     * Wrap.
     */
    public void wrap() {
        this.isWrapped = true;
    }

    /**
     * Time col.
     *
     * @return a new col representing time
     */
    public Col time() {
        return new Col("format(%s,'short time')".formatted(date().colName), alias) {{
            setWrapped(Col.this.isWrapped);
        }};
    }

    /**
     * Date col.
     *
     * @return a new col representing datetime
     */
    public Col date() {
        return new Col("DATEADD('s', CLng(%s),#01/01/1970 00:00:00 AM#)".formatted(colName), alias) {{
            setWrapped(Col.this.isWrapped);
        }};
    }

    /**
     * Sets wrapped.
     *
     * @param wrapped the wrapped
     */
    public void setWrapped(boolean wrapped) {
        isWrapped = wrapped;
    }

    /**
     * As own name. practically keeps the name's case.
     *
     * @return the col
     */
    public Col as() {
        if (this.alias.equals(""))
            return as(colName);
        return as(alias);
    }

    /**
     * creates a new column with the given alias as its alias
     *
     * @param alias the alias
     * @return the col
     */
    public Col as(String alias) {
        return new Col(colName, alias);
    }

    /**
     * Sets col name.
     *
     * @param colName the col name
     */
    public void setColName(String colName) {
        this.colName = colName;
    }

    /**
     * Col name string.
     *
     * @return the string
     */
    public String colName() {
        return colName;
    }

    /**
     * new col like this that belongs to the given table
     *
     * @param table the table
     * @return the col
     */
    public Col of(Table table) {
        return of(table.toString());
    }

    /**
     * new col like this that belongs to the given owner
     *
     * @param ofWhom the of whom
     * @return the col
     */
    public Col of(String ofWhom) {
        return new Col(ofWhom + "." + colName, alias);
    }

    /**
     * Replace col.
     *
     * @param replacing   the replacing
     * @param replaceWith the replace with
     * @return the col
     */
    public Col replace(String replacing, String replaceWith) {
        return new Col(colName.replaceAll(replacing, replaceWith));
    }

    /**
     * Nested string.
     *
     * @return the string
     */
    public String nested() {
        return label();
    }

    /**
     * Math col.
     *
     * @param operation the operation
     * @param value     the value
     * @return the col
     */
    public Col math(Math operation, Object value) {
        return math(operation, value, true);
    }

    /**
     * Math col.
     *
     * @param operation  the operation
     * @param value      the value
     * @param changeSelf the change self
     * @return the col
     */
    public Col math(Math operation, Object value, boolean changeSelf) {
        return operation.execute(this, value, changeSelf);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        String str = colName;
        if (!alias.equals(""))
            str += " AS [%s]".formatted(alias);
        return isWrapped ? "(%s)".formatted(str) : str;
    }

    /**
     * a Custom col.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class CustomCol extends Col {

        /**
         * Instantiates a new Custom col.
         *
         * @param colName the col name
         */
        public CustomCol(String colName) {
            super(colName);
        }

        /**
         * Instantiates a new Custom col.
         *
         * @param colName the col name
         * @param alias   the alias
         */
        public CustomCol(String colName, String alias) {
            super(colName, alias);
        }

        /**
         * Nested string.
         *
         * @return the string
         */
        @Override
        public String nested() {
            return toString();
        }
    }
}
