package ver14.SharedClasses.DBActions.DBResponse;

import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.Table.Col;


/**
 * represents a db response with the requested data structured in a table.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class TableDBResponse extends DBResponse {
    /**
     * The Calced lengths.
     */
    private final int[] calcedLengths;
    /**
     * The Columns.
     */
    protected String[] columns;
    /**
     * The Rows.
     */
    protected String[][] rows;

    /**
     * Instantiates a new Table db response.
     *
     * @param columns the columns
     * @param rows    the rows
     * @param request the request
     */
    public TableDBResponse(String[] columns, String[][] rows, DBRequest request) {
        this(columns, rows, Status.SUCCESS, request);
    }

    /**
     * Instantiates a new Table db response.
     *
     * @param columns the columns
     * @param rows    the rows
     * @param status  the status
     * @param request the request
     */
    public TableDBResponse(String[] columns, String[][] rows, Status status, DBRequest request) {
        super(status, request);
        this.columns = columns;
        this.rows = rows;
        this.calcedLengths = new int[columns.length];
        for (int i = 0; i < columns.length; i++) {
            this.calcedLengths[i] = calcMaxLength(i);
        }
    }

    /**
     * Calc max length int.
     *
     * @param colIndex the col index
     * @return the int
     */
    private int calcMaxLength(int colIndex) {
        int max = 0;

        if (columns[colIndex].length() > max) {
            max = columns[colIndex].length();
        }
        for (String[] row : rows) {
            String s = row[colIndex];
            if (s != null && s.length() > max) {
                max = s.length();
            }
        }
        return max;
    }

    /**
     * Instantiates a new Table db response.
     */
    protected TableDBResponse() {
        super(Status.SUCCESS, null);
        this.calcedLengths = new int[0];
    }

    /**
     * Num of rows int.
     *
     * @return the int
     */
    public int numOfRows() {
        return rows.length;
    }

    /**
     * Get first row string [ ].
     *
     * @return the string [ ]
     */
    public String[] getFirstRow() {
        assert rows.length > 0;
        return rows[0];
    }

    /**
     * Gets cell.
     *
     * @param row the row
     * @param col the col
     * @return the cell
     */
    public String getCell(int row, String col) {
        return rows[row][getColumnIndex(col)];
    }

    /**
     * Gets column index.
     *
     * @param column the column
     * @return the column index
     */
    protected int getColumnIndex(String column) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(column))
                return i;
        }
        return -1;
    }

    /**
     * Gets cell.
     *
     * @param row the row
     * @param col the col
     * @return the cell
     */
    public String getCell(int row, Col col) {
        return rows[row][getColumnIndex(col)];
    }

    /**
     * Gets column index.
     *
     * @param col the col
     * @return the column index
     */
    private int getColumnIndex(Col col) {
        return getColumnIndex(col.colName());
    }

    /**
     * Is any data boolean.
     *
     * @return the boolean
     */
    public boolean isAnyData() {
        return rows.length > 0;
    }

    /**
     * Clean table db response.
     *
     * @return the table db response
     */
    public TableDBResponse clean() {
        TableDBResponse clean = new TableDBResponse(columns, rows, status, request);
        clean.setAddedRes(this.addedRes == null ? null : this.addedRes.clean());
        return clean;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        int totalW = 0;
        for (int i = 0; i < columns.length; i++) {
            String s = columns[i];
            int w = maxLength(i);
            totalW += w;
            String format = "%-" + w + "s";
            bldr.append(String.format(format, s)).append('\t');
        }
        bldr.append("\n").append("-".repeat(totalW)).append("\n");
        for (String[] row : rows) {
            bldr.append(rowToString(row)).append("\n");
        }

        if (addedRes != null) {
            bldr.append("--------------").append("\n").append(addedRes);
        }

        return bldr.toString();
    }

    /**
     * Max length int.
     *
     * @param colIndex the col index
     * @return the int
     */
    private int maxLength(int colIndex) {
        return calcedLengths.length == 0 ? 10 : calcedLengths[colIndex];
    }

    /**
     * Row to string string.
     *
     * @param row the row
     * @return the string
     */
    public String rowToString(String[] row) {
        StringBuilder bldr = new StringBuilder();
        for (int i = 0, dataLength = row.length; i < dataLength; i++) {
            String s = row[i];
            String format = "%-" + maxLength(i) + "s";
            bldr.append(String.format(format, s)).append("\t");
        }
        return bldr.toString();
    }


    /**
     * Get columns string [ ].
     *
     * @return the string [ ]
     */
    public String[] getColumns() {
        return columns;
    }

    /**
     * Get rows string [ ] [ ].
     *
     * @return the string [ ] [ ]
     */
    public String[][] getRows() {
        return rows;
    }

}
