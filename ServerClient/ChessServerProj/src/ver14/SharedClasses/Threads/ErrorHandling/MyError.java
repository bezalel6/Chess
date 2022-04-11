package ver14.SharedClasses.Threads.ErrorHandling;

import ver14.SharedClasses.networking.AppSocket;

import java.util.HashMap;
import java.util.Map;

public class MyError extends Error {

    public final ErrorType type;
    private final Map<ContextType, ErrorContext> context;

    public MyError(Throwable throwable) {
        super(throwable);

        this.type = ErrorType.UnKnown;
        this.context = new HashMap<>();
    }

    public MyError(ErrorType type) {
        this(type.name(), type);
    }

    public MyError(String message, ErrorType type) {
        super(message);
        this.type = type;
        context = new HashMap<>();
    }

    public static MyError AppSocket(boolean isRead, AppSocket appSocket, Throwable source) {
        return new MyError(isRead ? ErrorType.AppSocketRead : ErrorType.AppSocketWrite) {{
            addContext(appSocket);
            initCause(source);
        }};
    }

    public void addContext(ErrorContext context) {
        this.context.put(context.contextType(), context);
    }

    public String getHandledStr() {
//        return toString();
        return type + (getCause() == null ? "" : "  " + getCause().getMessage());
    }


    @Override
    public String toString() {

        return "MyError{" +
                "" + getStackTrace()[0] + "\n" +
//                "error=" + errToString(this) +
                "type=" + type +
                ", context=" + context +
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
        return errMsg.toString();
    }

    private String superToString() {
        return super.toString();
    }

    public ErrorContext getContext(ContextType contextType) {
        return context.get(contextType);
    }

    public static class DisconnectedError extends MyError {
        public DisconnectedError() {
            this("");
        }

        public DisconnectedError(String message) {
            super(message, ErrorType.Disconnected);
        }
    }

}
