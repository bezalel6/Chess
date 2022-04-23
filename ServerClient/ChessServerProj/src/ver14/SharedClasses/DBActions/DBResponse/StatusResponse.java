package ver14.SharedClasses.DBActions.DBResponse;

import ver14.SharedClasses.DBActions.DBRequest.DBRequest;

/*
 * StatusResponse
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * StatusResponse -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * StatusResponse -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class StatusResponse extends DBResponse {
    /**
     * optional
     */
    private String details;
    private int updatedRows;
//    private

    public StatusResponse(Status status, DBRequest request, int updatedRows) {
        this(status, "Status: " + status.name() + " " + updatedRows + " Rows Updated", request, updatedRows);
    }

    public StatusResponse(Status status, String details, DBRequest request, int updatedRows) {
        super(status, request);
        this.details = details;
        this.updatedRows = updatedRows;
    }


    public String getDetails() {
        return details;
    }

    @Override
    public boolean isAnyData() {
        return true;
    }

    @Override
    public DBResponse clean() {
        return new StatusResponse(status, request, updatedRows);
    }
}
