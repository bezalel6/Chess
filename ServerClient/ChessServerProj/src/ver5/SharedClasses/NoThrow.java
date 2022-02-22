package ver5.SharedClasses;

public class NoThrow<T> {
    public final ThrowingCallback<T> throwingCallback;

    public NoThrow(ThrowingCallback<T> throwingCallback) {
        this.throwingCallback = throwingCallback;
    }

    public T get() {
        try {
            return throwingCallback.callback();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface ThrowingCallback<T> {
        T callback() throws Exception;
    }
}
