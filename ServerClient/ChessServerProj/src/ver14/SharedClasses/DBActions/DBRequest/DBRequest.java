package ver14.SharedClasses.DBActions.DBRequest;
//import org.intellij.lang.annotations.Language;

import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.DBActions.Statements.CustomStatement;
import ver14.SharedClasses.DBActions.Statements.SQLStatement;

import java.io.Serializable;

/*
 * DBRequest
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * DBRequest -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * DBRequest -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class DBRequest implements Serializable {
    public final Type type;
    private final String request;
    private final RequestBuilder builder;
    private DBRequest subRequest = null;

    public DBRequest(SQLStatement sqlStatement) {
        this(sqlStatement.type, sqlStatement.getStatement(), null);
    }

    public DBRequest(Type type, String request, RequestBuilder builder) {
        this.type = type;
        this.request = request;
        this.builder = builder == null ? new RequestBuilder(new CustomStatement(type, request), "internal") : builder;
    }

    public DBRequest(SQLStatement sqlStatement, RequestBuilder builder) {
        this(sqlStatement.type, sqlStatement.getStatement(), builder);
    }

    public RequestBuilder getBuilder() {
        return builder;
    }

    public DBRequest getSubRequest() {
        return subRequest;
    }

    public void setSubRequest(DBRequest subRequest) {
        this.subRequest = subRequest;
    }

    @Override
    public String toString() {
        return getRequest();
    }

    public String getRequest() {
        return request;
    }

    public enum Type {
        Query, Update
    }

}
