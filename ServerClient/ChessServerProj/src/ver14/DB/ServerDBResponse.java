package ver14.DB;

import ver14.SharedClasses.DBActions.DBResponse;
import ver14.SharedClasses.DBActions.Table.Col;
import ver14.SharedClasses.RegEx;
import ver14.SharedClasses.Utils.StrUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerDBResponse extends DBResponse {
    public ServerDBResponse(Status status) {
        super(status);
    }

    public ServerDBResponse(ResultSet rs) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int colsNum = rsmd.getColumnCount();
            columns = new String[colsNum];

            ArrayList<Integer> dateTimeCols = new ArrayList<>();
            // Create Table Title (Columns Names)
            for (int i = 1; i <= colsNum; i++) {
                columns[i - 1] = rsmd.getColumnLabel(i);
                //fixme nested col name isnt set to the og name
                if (RegEx.isSavedDate(rsmd.getColumnName(i))) {
//                    dateTimeCols.add(i);
                }

            }
            ArrayList<String[]> rowsList = new ArrayList<>();
            // Create All Table Rows
            while (rs.next()) {
                String[] strs = new String[colsNum];
                for (int i = 1; i <= colsNum; i++) {
                    String str = rs.getString(i);
                    if (dateTimeCols.contains(i)) {
                        str = StrUtils.formatDate(str);
                    }
                    str = StrUtils.dontCapWord(str);
                    strs[i - 1] = str;
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

    public String getCell(int row, Col col) {
        return rows[row][getColumnIndex(col)];
    }

    private int getColumnIndex(Col col) {
        return getColumnIndex(col.colName());
    }

}
