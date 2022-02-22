package ver3.server_info;

import java.io.File;
import java.sql.*;

/**
 * DB - SQLמחלקת שירות לביצוע חיבור למסד נתונים מסוג אקסס וביצוע משפטי .
 * ---------------------------------------------------------------------------
 * by Ilan Peretz(ilanperets@gmail.com) 5/11/2021
 */
public class DB {
    public static final String APP_DB_FILE_PATH = "/assets/db.accdb";

    /**
     * הפעולה מבצעת חיבור למסד הנתונים שהנתיב אליו מתקבל כפרמטר
     *
     * @param dbFileRelativePath הנתיב היחסי למסד הנתונים שאליו רוצים לקבל חיבור
     * @return מחזיר חיבור למסד הנתונים
     * @throws SQLException נזרקת שגיאה אם החיבור למסד הנתונים לא מצליח
     */
    public static Connection getConnection(String... dbFileRelativePath) throws SQLException {
        String dbPath = APP_DB_FILE_PATH;
        if (dbFileRelativePath.length != 0)
            dbPath = dbFileRelativePath[0];

        //#####################################################################
        dbPath = "src" + dbPath;
        //dbPath = dbPath.substring(dbPath.lastIndexOf("/")+1); //TurnON for JAR
        //#####################################################################

        // dbURL: Access DB Driver Name + dbPath
        String dbURL = "jdbc:ucanaccess://" + new File(dbPath).getAbsolutePath();

        // create the connection object to db
        Connection dbCon = DriverManager.getConnection(dbURL, "", "");

        return dbCon;
    }

    /**
     * Run SQL Query Statement - SELECT הפעולה מריצה שאילתת
     *
     * @param sql        שאילתה
     * @param dbFilePath פרמטר אופציונאלי לנתיב מסד הנתונים עליו רוצים להפעיל השאילתה
     * @return מחזירה טבלת תוצאה
     * @throws SQLException נזרקת שגיאה אם לא ניתן להריץ את השאילתה
     */
    public static ResultSet runQuery(String sql, String... dbFilePath) throws SQLException {
        Connection con = getConnection(dbFilePath);
        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        // execute SQL Query statement
        ResultSet resultSetTable = st.executeQuery(sql);

        return resultSetTable;
    }

    /**
     * Run SQL Update Statement - INSERT, DELETE, UPDATE הפעולה מריצה משפט עדכון
     *
     * @param sql        עדכון
     * @param dbFilePath
     * @return הפעולה מחזירה מספר השורות שעברו עדכון
     * @throws SQLException נזרקת שגיאה אם לא ניתן להריץ משפט עדכון
     */
    public static int runUpdate(String sql, String... dbFilePath) throws SQLException {
        Connection con = getConnection(dbFilePath);
        Statement st = con.createStatement();

        // execute SQL Update statement
        int numRowsUpdated = st.executeUpdate(sql);

        return numRowsUpdated;
    }

    /**
     * הדפסת טבלת תוצאה של שאילתה
     *
     * @param rs
     * @param colWidth
     * @throws SQLException
     */
    public static void printResultSet(ResultSet rs, int... colWidth) throws SQLException {
        int colw = 7;
        if (colWidth.length != 0)
            colw = colWidth[0];

        System.out.println(toStringResultSet(rs, colw));
    }

    /**
     * מחזירה מחרוזת המתארת את טבלת התוצאה של השאילתה המתקבלת כפרמטר
     *
     * @param rs       טבלת התוצאה של שאילתה
     * @param colWidth פרמטר אופציונאלי עבור רוחב עמודה רצוי
     * @return מחרזות לתיאור טבלת התוצאה
     * @throws SQLException נזרקת שגיאה אם לא ניתן לבצע המשימה
     */
    public static String toStringResultSet(ResultSet rs, int... colWidth) throws SQLException {
        int colw = 7;
        if (colWidth.length != 0)
            colw = colWidth[0];

        String str = " ";
        String format = "%-" + colw + "s";
        String line = new String(new char[colw - 2]).replace('\0', '-') + "  ";

        ResultSetMetaData rsmd = rs.getMetaData(); // to get columns names
        // Create Table Title (Columns Names)
        for (int i = 1; i <= rsmd.getColumnCount(); i++)
            str += String.format(format, rsmd.getColumnName(i)); //center(colWidth,rsmd.getColumnName(i)); 
        str += "\n ";
        for (int i = 1; i <= rsmd.getColumnCount(); i++)
            str += line;
        str += "\n ";

        // Create All Table Rows 
        while (rs.next()) {
            //str += String.format("%2d.  ",rs.getRow());
            for (int i = 1; i <= rsmd.getColumnCount(); i++)
                str += String.format(format, rs.getString(i)); // center(colWidth,rs.getString(i)); //
            if (!rs.isLast())
                str += "\n ";
        }

        return str;
    }

    // =======================================================================
    // CUSTOM METHODS HERE . . .
    // =======================================================================

    /**
     * בדיקה האם יוזר ששם המשתמש והסיסמה המתקבלים כפרמטירים קיים בטבלת היוזרים
     *
     * @param un שם המשתמש של היוזר
     * @param pw הסיסמה של היוזר
     * @return מחזיר אמת אם קיים יוזר כזה ושקר אחרת
     * @throws SQLException נזרקת שגיאה אם לא ניתן לבצע השאילה
     */
    public static boolean isUserExists(String un, String pw) throws SQLException {
        String sql = "SELECT * FROM users WHERE un='" + un + "' AND pw='" + pw + "'";
        //System.out.println("isUserExists Query: " + sql);
        ResultSet rs = runQuery(sql);
        boolean isExists = rs.next();

        return isExists;
    }
}
