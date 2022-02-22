package ver7.SharedClasses.networking;

import java.io.IOException;
import java.sql.SQLException;

public class MyErrors extends Exception {


    public MyErrors(String message) {
        super(message);
    }

    public MyErrors(String message, Exception cause) {
        super(message, cause);

    }

    public static void throwError(Exception e) throws MyErrors {
        throw parse(e);
    }

    public static MyErrors parse(Exception _e) {
        if (_e instanceof IOException e)
            return new Disconnected(e);
        else if (_e instanceof SQLException e) {
            return new DBErr(e);
        }
        return new MyErrors(_e.getMessage(), _e);
    }

    public void throwRuntime() {
        throw new RuntimeException("wrapper", this);
    }

    public static class Disconnected extends MyErrors {

        private final static String str = "disconnected";

        public Disconnected() {
            super(str);
        }

        public Disconnected(IOException e) {
            super(str, e);
        }

    }

    public static class DBErr extends MyErrors {
        public DBErr(SQLException e) {
            super("db err", e);
        }

    }
}
