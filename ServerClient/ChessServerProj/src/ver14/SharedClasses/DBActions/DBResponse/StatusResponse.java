package ver14.SharedClasses.DBActions.DBResponse;

import ver14.SharedClasses.DBActions.DBRequest.DBRequest;

public class StatusResponse extends DBResponse {
    /**
     * optional
     */
    private String details;

    public StatusResponse(Status status, DBRequest request) {
        this(status, "Status: " + status.name(), request);
    }

    public StatusResponse(Status status, String details, DBRequest request) {
        super(status, request);
        this.details = details;
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
        return new StatusResponse(status, request);
    }
}
