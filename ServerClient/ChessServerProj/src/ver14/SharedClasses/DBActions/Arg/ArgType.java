package ver14.SharedClasses.DBActions.Arg;


//askilan creating generic class instances
public enum ArgType {
    Date, DateRange, Text, ServerAddress, Number, Username(false), Password, Url, PictureUrl;
    final boolean isUserInput;

    ArgType() {
        this(true);
    }

    ArgType(boolean isUserInput) {
        this.isUserInput = isUserInput;
    }
}
