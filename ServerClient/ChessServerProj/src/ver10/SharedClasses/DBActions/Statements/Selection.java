package ver10.SharedClasses.DBActions.Statements;

import org.intellij.lang.annotations.MagicConstant;
import ver10.SharedClasses.DBActions.Condition;
import ver10.SharedClasses.DBActions.DBRequest;
import ver10.SharedClasses.DBActions.Table.Col;
import ver10.SharedClasses.DBActions.Table.Table;
import ver10.SharedClasses.Utils.StrUtils;

import java.util.Arrays;

public class Selection extends SQLStatement {
    private final Object[] select;
    private final Condition condition;
    private String selectPrefix;
    private String selectFrom;
    private String postFix;

    public Selection(Object selectFrom, Object[] select) {
        this(selectFrom, null, select);
    }

    public Selection(Object selectFrom, Condition condition, Object... select) {
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


    public Selection nestMe(Col... outerSelect) {
        return new Selection("(%s)".formatted(getStatement()), Arrays.stream(outerSelect).map(Col::nested).toArray());
    }

    @Override
    protected String createStatement() {
        String conditionsStr = condition == null ? "" : "WHERE " + condition;

        String selecting = select.length > 0 ? StrUtils.splitArr(select) : "*";

        String str = "%s %s FROM %s %s %s".formatted(selectPrefix, selecting, selectFrom, conditionsStr, postFix);
//        return str;
        return str.replaceAll(" {2}", " ");
    }


    public void top(Object top) {
        selectPrefix += " TOP " + top;
    }

    public void join(
            @Join String joinType,
            Table joinWith,
            Condition condition,
            Col... groupBy) {
        selectFrom += " %s %s ON (%s) GROUP BY %s ".formatted(joinType, joinWith, condition, StrUtils.splitArr(Arrays.stream(groupBy).map(Col::colName).toArray()));
    }

    public void orderBy(Col col, @Order String order) {
        postFix += " ORDER BY " + col.colName() + " " + order;
    }

    @MagicConstant(stringValues = {Join.LEFT})
    public @interface Join {
        String LEFT = "LEFT JOIN";
    }

    @MagicConstant(stringValues = {Order.ASC, Order.DESC})
    public @interface Order {
        String DESC = "DESC";
        String ASC = "ASC";
    }
}
