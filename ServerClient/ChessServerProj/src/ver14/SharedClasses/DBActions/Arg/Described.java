package ver14.SharedClasses.DBActions.Arg;

public record Described<T>(T obj, String description) {
    public Described(T obj) {
        this(obj, obj + "");
    }

    public static <T> Described<T> d(T obj, String description) {
        return new Described<>(obj, description);
    }
}
