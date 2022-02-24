package ver13.SharedClasses.DBActions.DBRequest;
//import org.intellij.lang.annotations.Language;

import ver13.SharedClasses.DBActions.Statements.SQLStatement;

import java.io.Serializable;

public class DBRequest implements Serializable {
    public final Type type;
    private final String request;
    private DBRequest subRequest = null;
    ;

    public DBRequest(SQLStatement sqlStatement) {
        this.request = sqlStatement.getStatement();
        this.type = sqlStatement.type;
    }

    public String getRequest() {
        return request;
    }

    public DBRequest getSubRequest() {
        return subRequest;
    }

    public void setSubRequest(DBRequest subRequest) {
        this.subRequest = subRequest;
    }

    public enum Type {
        Query, Update
    }

}
