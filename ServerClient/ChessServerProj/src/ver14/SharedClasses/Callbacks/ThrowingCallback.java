package ver14.SharedClasses.Callbacks;

/*
 * ThrowingCallback -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

/*
 * ThrowingCallback -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * ThrowingCallback
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

public interface ThrowingCallback<T> {


    void callback(T obj) throws Exception;

}
