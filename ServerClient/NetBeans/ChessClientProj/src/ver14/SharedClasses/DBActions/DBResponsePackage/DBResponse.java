package ver14.SharedClasses.DBActions.DBResponsePackage;

import ver14.SharedClasses.DBActions.DBRequest.DBRequest;

import java.io.Serializable;


/**
 * Db response - .
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class DBResponse implements Serializable {
    /**
     * The Status.
     */
    protected final Status status;
    /**
     * The Request.
     */
    protected final DBRequest request;
    /**
     * The Added res.
     */
    protected DBResponse addedRes = null;

    /**
     * Instantiates a new Db response.
     *
     * @param status  the status
     * @param request the request
     */
    protected DBResponse(Status status, DBRequest request) {
        this.status = status;
        this.request = request;
    }

    /**
     * Gets request.
     *
     * @return the request
     */
    public DBRequest getRequest() {
        return request;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Is success boolean.
     *
     * @return the boolean
     */
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }


    /**
     * Is any data boolean.
     *
     * @return the boolean
     */
    public abstract boolean isAnyData();

    /**
     * Gets added res.
     *
     * @return the added res
     */
    public DBResponse getAddedRes() {
        return addedRes;
    }

    /**
     * Sets added res.
     *
     * @param addedRes the added res
     */
    public void setAddedRes(DBResponse addedRes) {
        this.addedRes = addedRes;
    }

    /**
     * Clean db response.
     *
     * @return the db response
     */
    public abstract DBResponse clean();

    /**
     * Print.
     */
    public void print() {
        System.out.println(this);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "DBResponse{" +
                "status=" + status +
                ", addedRes=" + addedRes +
                '}';
    }

    /**
     * Status - .
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public enum Status {
        /**
         * Success status.
         */
        SUCCESS,
        /**
         * Error status.
         */
        ERROR;
    }
}
