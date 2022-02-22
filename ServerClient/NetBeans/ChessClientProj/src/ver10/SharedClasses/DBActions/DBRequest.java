package ver10.SharedClasses.DBActions;

//import org.intellij.lang.annotations.Language;

import ver10.SharedClasses.DBActions.Statements.SQLStatement;

import java.io.Serializable;

public class DBRequest implements Serializable {
    public final Type type;
    private final String request;

    public DBRequest(SQLStatement sqlStatement) {
        this.request = sqlStatement.getStatement();
        this.type = sqlStatement.type;
    }

    public String getRequest() {
        return request;
    }

    @Override
    public String toString() {
        return "DBRequest{" +
                "type=" + type +
                ", request='" + request + '\'' +
                '}';
    }

    public enum Type {
        Query, Update
    }

}
