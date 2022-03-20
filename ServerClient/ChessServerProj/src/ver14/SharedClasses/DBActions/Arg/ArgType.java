package ver14.SharedClasses.DBActions.Arg;

public enum ArgType {
    Date, DateRange, Text, ServerAddress, Number, Username(false), Password;
    final boolean isUserInput;

    ArgType() {
        this(true);
    }

    ArgType(boolean isUserInput) {
        this.isUserInput = isUserInput;
    }
}
