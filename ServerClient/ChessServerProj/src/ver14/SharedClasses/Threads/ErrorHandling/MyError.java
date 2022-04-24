package ver14.SharedClasses.Threads.ErrorHandling;

import ver14.SharedClasses.Utils.StrUtils;


/**
 * My error - .
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MyError extends Error {

    /**
     * Instantiates a new My error.
     */
    public MyError() {
    }

    /**
     * Instantiates a new My error.
     *
     * @param throwable the throwable
     */
    public MyError(Throwable throwable) {
        super(throwable);
    }


    /**
     * Instantiates a new My error.
     *
     * @param message the message
     */
    public MyError(String message) {
        super(message);
    }

    /**
     * Instantiates a new My error.
     *
     * @param message the message
     * @param cause   the cause
     */
    public MyError(String message, Throwable cause) {
        super(message, cause);
    }


    /**
     * Gets handled str.
     *
     * @return the handled str
     */
    public String getHandledStr() {
//        return toString();
        return this.getMessage() + (getCause() == null ? "" : "  " + getCause().getMessage());
    }


    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {

        return "MyError{" +
                "error=" + errToString(this) +
                ", source=" + errToString(getCause()) +
                '}';
    }

    /**
     * Err to string string.
     *
     * @param error the error
     * @return the string
     */
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

    /**
     * Super to string string.
     *
     * @return the string
     */
    private String superToString() {
        return super.toString();
    }

    /**
     * Db err - .
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class DBErr extends MyError {
        /**
         * Instantiates a new Db err.
         *
         * @param throwable the throwable
         */
        public DBErr(Throwable throwable) {
            super(throwable);
        }

        /**
         * Instantiates a new Db err.
         *
         * @param message the message
         * @param cause   the cause
         */
        public DBErr(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * a Disconnected error .
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class DisconnectedError extends MyError {
        /**
         * Instantiates a new Disconnected error.
         */
        public DisconnectedError() {
            this("");
        }

        /**
         * Instantiates a new Disconnected error.
         *
         * @param message the message
         */
        public DisconnectedError(String message) {
            super(message);
        }

        /**
         * Instantiates a new Disconnected error.
         *
         * @param throwable the throwable
         */
        public DisconnectedError(Throwable throwable) {
            super(throwable);
        }
    }

}
