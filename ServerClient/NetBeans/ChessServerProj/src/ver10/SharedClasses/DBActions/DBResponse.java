package ver10.SharedClasses.DBActions;

import java.io.Serializable;

public class DBResponse implements Serializable {
    //todo success/err
    protected String[] columns;
    protected String[][] rows;
    protected Status status;

    public DBResponse(Status status) {
        this(new String[]{"status: " + status.toString()}, new String[0][], status);
    }

    public DBResponse(String[] columns, String[][] rows, Status status) {
        this.columns = columns;
        this.rows = rows;
        this.status = status;
    }

    public DBResponse(String[] columns, String[][] rows) {
        this(columns, rows, Status.SUCCESS);
    }


    protected DBResponse() {

    }

    public DBResponse createResponse() {
        return new DBResponse(columns, rows, status);
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
        return new DBResponse(columns, rows, status);
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            String s = columns[i];
            String format = "%-" + maxLength(i) + "s";
            bldr.append(String.format(format, s)).append('\t');
        }
        bldr.append("\n").append("\n");
        for (String[] row : rows) {
            bldr.append(rowToString(row)).append("\n");
        }

        return bldr.toString();
    }

    private int maxLength(int colIndex) {
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

    public void print() {
        System.out.println(this);
    }

    public enum Status {
        SUCCESS, ERROR;
    }
}
