package ver14.SharedClasses.DBActions.DBResponse;

import ver14.SharedClasses.DBActions.DBRequest.DBRequest;


/**
 * Status response - represents a db status response.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class StatusResponse extends DBResponse {
    /**
     * optional
     */
    private String details;
    /**
     * The Updated rows.
     */
    private int updatedRows;
//    private

    /**
     * Instantiates a new Status response.
     *
     * @param status      the status
     * @param request     the request
     * @param updatedRows the updated rows
     */
    public StatusResponse(Status status, DBRequest request, int updatedRows) {
        this(status, "Status: " + status.name() + " " + updatedRows + " Rows Updated", request, updatedRows);
    }

    /**
     * Instantiates a new Status response.
     *
     * @param status      the status
     * @param details     the details
     * @param request     the request
     * @param updatedRows the updated rows
     */
    public StatusResponse(Status status, String details, DBRequest request, int updatedRows) {
        super(status, request);
        this.details = details;
        this.updatedRows = updatedRows;
    }


    /**
     * Gets details.
     *
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Is any data boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isAnyData() {
        return true;
    }

    /**
     * Clean db response.
     *
     * @return the db response
     */
    @Override
    public DBResponse clean() {
        return new StatusResponse(status, request, updatedRows);
    }
}
