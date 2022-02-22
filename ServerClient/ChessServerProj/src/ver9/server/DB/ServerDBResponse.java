package ver9.server.DB;

import ver9.SharedClasses.DBActions.DBResponse;
import ver9.SharedClasses.DBActions.Table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerDBResponse extends DBResponse {
    public ServerDBResponse(ResultSet rs) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int colsNum = rsmd.getColumnCount();
            columns = new String[colsNum];

            // Create Table Title (Columns Names)
            for (int i = 1; i <= colsNum; i++) {
                columns[i - 1] = rsmd.getColumnLabel(i);
            }
            ArrayList<String[]> rowsList = new ArrayList<>();
            // Create All Table Rows
            while (rs.next()) {
                String[] strs = new String[colsNum];
                for (int i = 1; i <= colsNum; i++) {
                    strs[i - 1] = rs.getString(i);

                }
                rowsList.add(strs);
            }
            rows = rowsList.toArray(new String[0][]);
            status = Status.SUCCESS;
        } catch (SQLException e) {
            e.printStackTrace();
            status = Status.ERROR;
        }
    }

    public String getCell(int row, Table.Col col) {
        return rows[row][getColumnIndex(col)];
    }

    private int getColumnIndex(Table.Col column) {
        return getColumnIndex(column.getColName());
    }

}
