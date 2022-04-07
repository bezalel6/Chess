package ver14.SharedClasses.DBActions.DBResponse;

import ver14.SharedClasses.DBActions.DBRequest.DBRequest;

import java.io.Serializable;

public abstract class DBResponse implements Serializable {
    protected final Status status;
    protected final DBRequest request;
    protected DBResponse addedRes = null;

    protected DBResponse(Status status, DBRequest request) {
        this.status = status;
        this.request = request;
    }

    public DBRequest getRequest() {
        return request;
    }

    public Status getStatus() {
        return status;
    }

    public abstract boolean isAnyData();

    public DBResponse getAddedRes() {
        return addedRes;
    }

    public void setAddedRes(DBResponse addedRes) {
        this.addedRes = addedRes;
    }

    public abstract DBResponse clean();

    public void print() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "DBResponse{" +
                "status=" + status +
                ", addedRes=" + addedRes +
                '}';
    }

    public enum Status {
        SUCCESS, ERROR;
    }
}
