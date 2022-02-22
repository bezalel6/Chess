package ver7.server.DB;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class Results {
    public final String[] columns;
    public final Row[] rows;

    public Results(ResultSet rs, Table table) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData(); // to get columns names
        int colsNum = table.cols.length;
        columns = new String[colsNum];

        // Create Table Title (Columns Names)
        for (Table.Col col : table.cols) {
            columns[table.colIndex(col)] = col.getColName();
        }
//        for (int i = 1; i <= rsmd.getColumnCount(); i++)
//            columns[i - 1] = rsmd.getColumnName(i); //center(colWidth,rsmd.getColumnName(i));

        ArrayList<String[]> rowsList = new ArrayList<>();
        int row = 0;
        // Create All Table Rows
        while (rs.next()) {
            String[] strs = new String[colsNum];
//            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//                strs[i - 1] = rs.getString(i);
////                strs[i - 1] = StrUtils.dontCapWord(rs.getString(i));
//            }
            for (Table.Col col : table.cols) {
                strs[table.colIndex(col)] = rs.getString(col.getColName());
            }
            rowsList.add(strs);
            row++;
        }
        rows = new Row[row];
        for (int i = 0; i < rowsList.size(); i++) {
            rows[i] = new Row(rowsList.get(i));
        }
    }


    public int numOfRows() {
        return rows.length;
    }

    public Row getRow(int index) {
        return rows[index];
    }

    public Row getFirst() {
        return rows[0];
    }

    private int getColumnIndex(Table.Col column) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(column.getColName()))
                return i;
        }
        return -1;
    }

    public boolean isAnyData() {
        return rows.length > 0;
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
        for (Row row : rows) {
            bldr.append(row.toString()).append("\n");
        }

        return bldr.toString();
    }

    private int maxLength(int colIndex) {
        int max = 0;

        if (columns[colIndex].length() > max) {
            max = columns[colIndex].length();
        }
        for (Row row : rows) {
            String s = row.data[colIndex];
            if (s.length() > max) {
                max = s.length();
            }
        }
        return max;
    }

    public class Row {
        private final String[] data;

        Row(String[] data) {
            this.data = data;
        }

        public String getByCol(Table.Col col) {
            return data[getColumnIndex(col)];
        }

        public String[] getData() {
            return data;
        }

        @Override
        public String toString() {
            StringBuilder bldr = new StringBuilder();
            for (int i = 0, dataLength = data.length; i < dataLength; i++) {
                String s = data[i];
                String format = "%-" + maxLength(i) + "s";
                bldr.append(String.format(format, s)).append("\t");
            }
            return bldr.toString();
        }
    }
}


