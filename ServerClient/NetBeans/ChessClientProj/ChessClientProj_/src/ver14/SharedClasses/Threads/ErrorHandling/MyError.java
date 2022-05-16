package ver14.SharedClasses.Threads.ErrorHandling;

import ver14.SharedClasses.Utils.StrUtils;


/**
 * represents my implementation of an error.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MyError extends Error {

    /**
     * Instantiates a new error.
     */
    public MyError() {
    }

    /**
     * Instantiates a new error with a cause.
     *
     * @param cause the cause
     */
    public MyError(Throwable cause) {
        super(cause);
    }


    /**
     * Instantiates a new error with an error message.
     *
     * @param message the error message
     */
    public MyError(String message) {
        super(message);
    }

    /**
     * Instantiates a new error.
     *
     * @param message the error message
     * @param cause   the cause
     */
    public MyError(String message, Throwable cause) {
        super(message, cause);
    }


    /**
     * Gets a short description of this error.
     *
     * @return the handled str
     */
    public String getShortDesc() {
//        return toString();
        return StrUtils.isEmpty(this.getMessage()) ? getClass().getName() : this.getMessage() + (getCause() == null ? "" : "  " + getCause());
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

}
