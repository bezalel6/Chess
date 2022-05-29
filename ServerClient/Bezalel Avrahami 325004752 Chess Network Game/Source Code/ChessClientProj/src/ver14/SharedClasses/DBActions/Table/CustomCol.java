package ver14.SharedClasses.DBActions.Table;

/**
 * represents a Custom column that will keep its name, even on nesting.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class CustomCol extends Col {

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
