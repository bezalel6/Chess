package ver10.SharedClasses.DBActions.Arg;

public enum ArgType {
    Date, DateRange, Text, Number, Username(false);
    public final boolean isUserInput;

    ArgType() {
        this(true);
    }

    ArgType(boolean isUserInput) {
        this.isUserInput = isUserInput;
    }
}
