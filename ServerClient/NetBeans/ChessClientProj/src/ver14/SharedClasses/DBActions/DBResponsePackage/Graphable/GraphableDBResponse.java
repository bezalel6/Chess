package ver14.SharedClasses.DBActions.DBResponsePackage.Graphable;

import ver14.SharedClasses.DBActions.DBRequest.DBRequest;
import ver14.SharedClasses.DBActions.DBResponsePackage.DBResponse;

import java.util.Arrays;


public abstract class GraphableDBResponse extends DBResponse implements Graphable {

    protected GraphableDBResponse(Status status, DBRequest request) {
        super(status, request);
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + Arrays.toString(elements());
    }
}
