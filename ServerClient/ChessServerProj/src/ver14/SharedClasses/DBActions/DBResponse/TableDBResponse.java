package ver14.SharedClasses.DBActions.DBResponse;

import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.Table.Col;

/*
 * TableDBResponse
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * TableDBResponse -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * TableDBResponse -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class TableDBResponse extends DBResponse {
    private final int[] calcedLengths;
    protected String[] columns;
    protected String[][] rows;

    public TableDBResponse(String[] columns, String[][] rows, DBRequest request) {
        this(columns, rows, Status.SUCCESS, request);
    }

    public TableDBResponse(String[] columns, String[][] rows, Status status, DBRequest request) {
        super(status, request);
        this.columns = columns;
        this.rows = rows;
        this.calcedLengths = new int[columns.length];
        for (int i = 0; i < columns.length; i++) {
            this.calcedLengths[i] = calcMaxLength(i);
        }
    }

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

    protected TableDBResponse() {
        super(Status.SUCCESS, null);
        this.calcedLengths = new int[0];
    }

    public int numOfRows() {
        return rows.length;
    }

    public String[] getFirstRow() {
        assert rows.length > 0;
        return rows[0];
    }

    public String getCell(int row, String col) {
        return rows[row][getColumnIndex(col)];
    }

    protected int getColumnIndex(String column) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(column))
                return i;
        }
        return -1;
    }

    public String getCell(int row, Col col) {
        return rows[row][getColumnIndex(col)];
    }

    private int getColumnIndex(Col col) {
        return getColumnIndex(col.colName());
    }

    public boolean isAnyData() {
        return rows.length > 0;
    }

    public TableDBResponse clean() {
        TableDBResponse clean = new TableDBResponse(columns, rows, status, request);
        clean.setAddedRes(this.addedRes == null ? null : this.addedRes.clean());
        return clean;
    }

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

    private int maxLength(int colIndex) {
        return calcedLengths.length == 0 ? 10 : calcedLengths[colIndex];
    }

    public String rowToString(String[] row) {
        StringBuilder bldr = new StringBuilder();
        for (int i = 0, dataLength = row.length; i < dataLength; i++) {
            String s = row[i];
            String format = "%-" + maxLength(i) + "s";
            bldr.append(String.format(format, s)).append("\t");
        }
        return bldr.toString();
    }


    public String[] getColumns() {
        return columns;
    }

    public String[][] getRows() {
        return rows;
    }

}
