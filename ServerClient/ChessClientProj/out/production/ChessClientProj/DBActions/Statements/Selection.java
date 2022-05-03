package ver14.SharedClasses.DBActions.Statements;

import org.intellij.lang.annotations.MagicConstant;
import ver14.SharedClasses.DBActions.Condition;
import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.Table.Col;
import ver14.SharedClasses.DBActions.Table.Table;
import ver14.SharedClasses.Utils.StrUtils;

import java.util.Arrays;


/**
 * Selection - a selection sql statement.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Selection extends SQLStatement {
    /**
     * The objects to Select.
     */
    private final Object[] select;
    /**
     * The Condition.
     */
    private final Condition condition;
    /**
     * The Selection prefix.
     */
    private String selectPrefix;
    /**
     * The Select from.
     */
    private String selectFrom;
    /**
     * The selection Post fix.
     */
    private String postFix;

    /**
     * Instantiates a new Selection.
     *
     * @param selectFrom the select from
     * @param select     the select
     */
    public Selection(Object selectFrom, Object[] select) {
        this(selectFrom, null, select);
    }

    /**
     * Instantiates a new Selection.
     *
     * @param selectFrom the select from
     * @param condition  the condition
     * @param select     the select
     */
    public Selection(Object selectFrom, Condition condition, Object[] select) {
        super(DBRequest.Type.Query);
        this.selectFrom = selectFrom.toString();
        this.select = Arrays.stream(select)
                .map(s -> s instanceof Col col ? col.as() : s.toString())
                .toList()
                .toArray();
        this.condition = condition;
        this.selectPrefix = "SELECT";
        this.postFix = "";
    }

    /**
     * Nest me selection.
     *
     * @param outerSelect the outer select
     * @return the selection
     */
    public Selection nestMe(Col... outerSelect) {
        return new Selection("(%s)".formatted(getStatement()), Arrays.stream(outerSelect).map(Col::nested).toArray());
    }

    /**
     * Create statement string.
     *
     * @return the string
     */
    @Override
    protected String createStatement() {
        String conditionsStr = condition == null ? "" : "WHERE " + condition;

        String selecting = select.length > 0 ? StrUtils.splitArr(select) : "*";

        String str = "%s %s FROM %s %s %s".formatted(selectPrefix, selecting, selectFrom, conditionsStr, postFix);
//        return str;
        return str.replaceAll(" {2}", " ");
    }


    /**
     * Top.
     *
     * @param top the top
     */
    public void top(Object top) {
        selectPrefix += " TOP " + top;
    }

    /**
     * Join.
     *
     * @param joinType  the join type
     * @param joinWith  the join with
     * @param condition the condition
     * @param groupBy   the group by
     */
    public void join(
            @Join String joinType,
            Table joinWith,
            Condition condition,
            Col... groupBy) {
        selectFrom += " %s %s ON (%s) GROUP BY %s ".formatted(joinType, joinWith, condition, StrUtils.splitArr(Arrays.stream(groupBy).map(Col::colName).toArray()));
    }

    /**
     * Order by.
     *
     * @param col   the col
     * @param order the order
     */
    public void orderBy(Col col, @Order String order) {
        postFix += " ORDER BY " + col.colName() + " " + order;
    }

    /**
     * Join - selection join.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    @MagicConstant(stringValues = {Join.LEFT})
    public @interface Join {
        /**
         * The constant LEFT.
         */
        String LEFT = "LEFT JOIN";
    }

    /**
     * Order - selection order by.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    @MagicConstant(stringValues = {Order.ASC, Order.DESC})
    public @interface Order {
        /**
         * The constant DESC.
         */
        String DESC = "DESC";
        /**
         * The constant ASC.
         */
        String ASC = "ASC";
    }
}
