package ver14.SharedClasses.Threads.ErrorHandling;

import ver14.SharedClasses.Utils.StrUtils;

public class MyError extends Error {

    public MyError() {
    }

    public MyError(Throwable throwable) {
        super(throwable);
    }


    public MyError(String message) {
        super(message);
    }


    public String getHandledStr() {
//        return toString();
        return this.getMessage() + (getCause() == null ? "" : "  " + getCause().getMessage());
    }


    @Override
    public String toString() {

        return "MyError{" +
                "error=" + errToString(this) +
                ", source=" + errToString(getCause()) +
                '}';
    }

    public static String errToString(Throwable error) {
        if (error == null) {
            return "";
        }
        String toStr;
        if (error instanceof MyError myError)
            toStr = myError.superToString();
        else toStr = error + "";
        StringBuilder errMsg = new StringBuilder(">> " + toStr + "\n");
        for (StackTraceElement element : error.getStackTrace())
            errMsg.append(">>> ").append(element).append("\n");
        return StrUtils.dontCapFull(errMsg.toString());
    }

    private String superToString() {
        return super.toString();
    }

    public static class DBErr extends MyError {
        public DBErr(Throwable throwable) {
            super(throwable);
        }
    }

    public static class DisconnectedError extends MyError {
        public DisconnectedError() {
            this("");
        }

        public DisconnectedError(String message) {
            super(message);
        }

        public DisconnectedError(Throwable throwable) {
            super(throwable);
        }
    }

}
