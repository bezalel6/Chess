package ver14.SharedClasses.DBActions.Arg;


/**
 * Arg type - .
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum ArgType {
    /**
     * Date arg type.
     */
    Date,
    /**
     * Date range arg type.
     */
    DateRange,
    /**
     * Text arg type.
     */
    Text,
    /**
     * Server address arg type.
     */
    ServerAddress,
    /**
     * Number arg type.
     */
    Number,
    /**
     * Username arg type.
     */
    Username(false),
    /**
     * Password arg type.
     */
    Password,
    /**
     * Url arg type.
     */
    Url,
    /**
     * Picture url arg type.
     */
    PictureUrl;
    /**
     * The Is user input.
     */
    final boolean isUserInput;

    ArgType() {
        this(true);
    }

    ArgType(boolean isUserInput) {
        this.isUserInput = isUserInput;
    }
}
