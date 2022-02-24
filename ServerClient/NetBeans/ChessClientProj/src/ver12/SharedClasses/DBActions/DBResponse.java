package ver12.SharedClasses.DBActions;

import java.io.Serializable;

public class DBResponse implements Serializable {
    private final int[] calcedLengths;
    //todo success/err
    protected String[] columns;
    protected String[][] rows;
    protected Status status;
    private DBResponse addedRes = null;

    public DBResponse(Status status) {
        this(new String[]{"status: " + status.toString()}, new String[0][], status);
    }

    public DBResponse(String[] columns, String[][] rows, Status status) {
        this.columns = columns;
        this.rows = rows;
        this.status = status;
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

    public DBResponse(String[] columns, String[][] rows) {
        this(columns, rows, Status.SUCCESS);
    }

    protected DBResponse() {
        this.calcedLengths = new int[0];
    }

    public DBResponse getAddedRes() {
        return addedRes;
    }

    public void setAddedRes(DBResponse addedRes) {
        this.addedRes = addedRes;
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

    public boolean isAnyData() {
        return rows.length > 0;
    }

    public DBResponse clean() {
        DBResponse clean = new DBResponse(columns, rows, status);
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

        return bldr.toString();
    }

    private int maxLength(int colIndex) {
        return calcedLengths[colIndex];
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

    public Status getStatus() {
        return status;
    }

    public String[] getColumns() {
        return columns;
    }

    public String[][] getRows() {
        return rows;
    }

    public void print() {
        System.out.println(this);
    }

    public enum Status {
        SUCCESS, ERROR;
    }
}
