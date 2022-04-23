package ver14.SharedClasses.Threads.ErrorHandling;

/*
 * EnvManager -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

/*
 * EnvManager -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * EnvManager
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

public interface EnvManager {
    /**
     * notifies manager of a managed error
     *
     * @param err the error thrown
     */
    void handledErr(MyError err);

    /**
     * notifies manager of an un-handleable error. the manager must shut down everything
     *
     * @param err the error thrown
     */
    void criticalErr(MyError err);
}
