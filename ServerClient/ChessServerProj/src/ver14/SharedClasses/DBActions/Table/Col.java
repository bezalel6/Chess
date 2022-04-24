package ver14.SharedClasses.DBActions.Table;

import ver14.SharedClasses.DBActions.Condition;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Arrays;


public class Col implements Serializable {

    public static final Col GameID = new Col("GameID");
    public static final Col SavedGame = new Col("SavedGame");
    public static final Col Password = new Col("password");
    public static final Col Player1 = new Col("Player1");
    public static final Col Player2 = new Col("Player2");
    public static final Col Username = new Col("username");
    public static final Col Winner = new Col("Winner");
    public static final Col PlayerToMove = new Col("PlayerToMove");
    public static final Col CreatedDateTime = new Col("CreatedDateTime");
    public static final Col ProfilePic = new Col("ProfilePic");

    private final String alias;
    private String colName;
    private boolean isWrapped = false;

    public Col(Col col) {
        this(col.colName, col.alias);
        this.isWrapped = col.isWrapped;
    }

    public Col(String colName, String alias) {
        this.colName = colName;
        this.alias = StrUtils.format(alias);
    }

    public Col(String colName) {
        this(colName, "");
    }

    public static Col count(String as) {
        return count(as, "*");
    }

    public static Col count(String as, Object countWhat) {
        return new Col("COUNT(%s)".formatted(countWhat), as);
    }

    public static Col countIf(String as, Condition condition) {
        return new Col("SUM(IIf(\n\t%s,1,0\n))".formatted(condition), as);
    }

    public static CustomCol sum(String as, Col... colsToSum) {
        return new CustomCol(StrUtils.splitArr("+", Arrays.stream(colsToSum).map(Col::label).toArray()), as);
    }

    public String label() {
        return alias.equals("") ? colName : ("[%s]".formatted(alias));
    }

    public static Col switchCase(String as, SwitchCase... cases) {
        assert cases.length > 0;
        String f = "\n\t";
        Col col = new Col("SWITCH(%s\n)".formatted(f + StrUtils.splitArr("," + f, cases)), as);
        col.wrap();
        return col;
    }

    public void wrap() {
        this.isWrapped = true;
    }

    public Col time() {
//        format(DATEADD('s', CLng(Games.SAVEDDATETIME), #01/01/1970 00:00:00 AM#),'short time')
        return new Col("format(%s,'short time')".formatted(date().colName), alias) {{
            setWrapped(Col.this.isWrapped);
        }};
    }

    public Col date() {
        return new Col("DATEADD('s', CLng(%s),#01/01/1970 00:00:00 AM#)".formatted(colName), alias) {{
            setWrapped(Col.this.isWrapped);
        }};
    }

    public void setWrapped(boolean wrapped) {
        isWrapped = wrapped;
    }

    public Col as() {
        if (this.alias.equals(""))
            return as(colName);
        return as(alias);
    }

    public Col as(String alias) {
        return new Col(colName, alias);
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String colName() {
        return colName;
    }

    public Col of(Table table) {
        return of(table.toString());
    }

    public Col of(String ofWhom) {
        return new Col(ofWhom + "." + colName, alias);
    }

    public Col replace(String replacing, String replaceWith) {
        return new Col(colName.replaceAll(replacing, replaceWith));
    }

    public String nested() {
        return label();
    }

    public Col math(Math operation, Object value) {
        return math(operation, value, true);
    }

    public Col math(Math operation, Object value, boolean changeSelf) {
        return operation.execute(this, value, changeSelf);
    }

    @Override
    public String toString() {
        String str = colName;
        if (!alias.equals(""))
            str += " AS [%s]".formatted(alias);
        return isWrapped ? "(%s)".formatted(str) : str;
    }

    public static class CustomCol extends Col {

        public CustomCol(String colName) {
            super(colName);
        }

        public CustomCol(String colName, String alias) {
            super(colName, alias);
        }

        @Override
        public String nested() {
            return toString();
        }
    }
}
