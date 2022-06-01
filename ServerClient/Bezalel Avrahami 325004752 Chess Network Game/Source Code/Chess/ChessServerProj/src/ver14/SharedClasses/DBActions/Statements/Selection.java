package ver14.SharedClasses.DBActions.Statements;

import org.intellij.lang.annotations.MagicConstant;
import ver14.SharedClasses.DBActions.Condition;
import ver14.SharedClasses.DBActions.DBRequest.Type;
import ver14.SharedClasses.DBActions.Table.Col;
import ver14.SharedClasses.DBActions.Table.Table;
import ver14.SharedClasses.Utils.StrUtils;

import java.util.Arrays;


/**
 * represents a selection sql statement.
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
     * @param selectFrom select from
     * @param select     what to select
     */
    public Selection(Object selectFrom, Object[] select) {
        this(selectFrom, null, select);
    }

    /**
     * Instantiates a new Selection
     *
     * @param selectFrom where to select from
     * @param condition  the condition for selecting
     * @param select     select what
     */
    public Selection(Object selectFrom, Condition condition, Object[] select) {
        super(Type.Query);
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
     * create a {@link Selection} with this selection statement nested inside.
     *
     * @param outerSelect the columns to select from the now nested selection
     * @return the new nested selection
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
     * get a certain number of results from the top.
     *
     * @param top the number of results
     */
    public void top(Object top) {
        selectPrefix += " TOP " + top;
    }

    /**
     * Join this selection with another {@link Table}.
     *
     * @param joinType  the join type. left or right. a left join represents a join operation
     *                  where every record is selected from the original selection,
     *                  and any matching record from the {@code joinWith} table.
     *                  <p>
     *                  a right join represents a join operation
     *                  where every record is selected from the {@code joinWith} table
     *                  and any matching record from the original selection,
     * @param joinWith  the table to join with
     * @param condition the condition to join by
     * @param groupBy   the columns to group both tables by
     */
    public void join(
            @MagicConstant(stringValues = {"LEFT JOIN", "RIGHT JOIN"})
            String joinType,
            Table joinWith,
            Condition condition,
            Col... groupBy) {
        selectFrom += " %s %s ON (%s) GROUP BY %s ".formatted(joinType, joinWith, condition, StrUtils.splitArr(Arrays.stream(groupBy).map(Col::colName).toArray()));
    }

    /**
     * Order this selection in an ascending or descending order.
     *
     * @param col   the col to order by
     * @param order the order
     */
    public void orderBy(Col col, @MagicConstant(stringValues = {"ASC", "DESC"}) String order) {
        postFix += " ORDER BY " + col.colName() + " " + order;
    }


}
