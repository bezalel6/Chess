package ver14.SharedClasses.DBActions.DBResponse.Graphable;

import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.DBResponse.DBResponse;

import java.util.Arrays;

/*
 * GraphableDBResponse
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * GraphableDBResponse -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * GraphableDBResponse -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public abstract class GraphableDBResponse extends DBResponse implements Graphable {

    protected GraphableDBResponse(Status status, DBRequest request) {
        super(status, request);
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + Arrays.toString(elements());
    }
}
