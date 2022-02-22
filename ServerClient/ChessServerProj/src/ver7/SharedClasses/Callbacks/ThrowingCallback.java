package ver7.SharedClasses.Callbacks;

public interface ThrowingCallback<T> {


    void callback(T obj) throws Exception;

}
