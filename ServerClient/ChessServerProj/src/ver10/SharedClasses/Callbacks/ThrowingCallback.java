package ver10.SharedClasses.Callbacks;

public interface ThrowingCallback<T> {


    void callback(T obj) throws Exception;

}
