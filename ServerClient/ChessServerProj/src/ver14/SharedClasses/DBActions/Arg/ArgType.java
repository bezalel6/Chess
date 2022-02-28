package ver14.SharedClasses.DBActions.Arg;

public enum ArgType {
    Date, DateRange, Text, Number, Username(false), Password;
    final boolean isUserInput;

    ArgType() {
        this(true);
    }

    ArgType(boolean isUserInput) {
        this.isUserInput = isUserInput;
    }
}
