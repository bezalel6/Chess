package ver14.SharedClasses.Threads.ErrorHandling;

import ver14.SharedClasses.networking.AppSocket;

import java.util.HashMap;
import java.util.Map;

public class MyError extends Error {
    public final ErrorType type;
    private final Map<ContextType, ErrorContext> context;
    private Throwable source;

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
            setSource(source);
        }};
    }

    public void addContext(ErrorContext context) {
        this.context.put(context.contextType(), context);
    }

    public void setSource(Throwable source) {
        this.source = source;
    }

    public Throwable source() {
        return source;
    }

    public ErrorContext getContext(ContextType contextType) {
        return context.get(contextType);
    }

    @Override
    public String toString() {
        return "MyError{" +
                "type=" + type +
                ", context=" + context +
                ", source=" + source +
                '}';
    }

    public enum ErrorType {
        UnKnown,
        Model(ContextType.Game),
        AppSocketWrite(ContextType.AppSocket),
        AppSocketRead(ContextType.AppSocket);
        public final ContextType[] contextTypes;

        ErrorType(ContextType... contextTypes) {
            this.contextTypes = contextTypes;
        }
    }

    public enum ContextType {
        GameSession, Game, Player, AppSocket;

    }
}
