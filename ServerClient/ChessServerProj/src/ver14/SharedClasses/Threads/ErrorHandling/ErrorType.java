package ver14.SharedClasses.Threads.ErrorHandling;

public enum ErrorType {
    UnKnown,
    Model(ContextType.Game),
    AppSocketWrite(ContextType.AppSocket),
    AppSocketRead(ContextType.AppSocket), Disconnected();
    public final ContextType[] contextTypes;

    ErrorType(ContextType... contextTypes) {
        this.contextTypes = contextTypes;
    }
}
