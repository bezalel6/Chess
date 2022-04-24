package ver14.SharedClasses.DBActions.DBRequest;
//import org.intellij.lang.annotations.Language;

import ver14.SharedClasses.DBActions.RequestBuilder;
import ver14.SharedClasses.DBActions.Statements.CustomStatement;
import ver14.SharedClasses.DBActions.Statements.SQLStatement;

import java.io.Serializable;


/**
 * Db request - a database request.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class DBRequest implements Serializable {
    /**
     * The Type.
     */
    public final Type type;
    /**
     * The Request.
     */
    private final String request;
    /**
     * The Builder.
     */
    private final RequestBuilder builder;
    /**
     * The Sub request.
     */
    private DBRequest subRequest = null;

    /**
     * Instantiates a new Db request.
     *
     * @param sqlStatement the sql statement
     */
    public DBRequest(SQLStatement sqlStatement) {
        this(sqlStatement.type, sqlStatement.getStatement(), null);
    }

    /**
     * Instantiates a new Db request.
     *
     * @param type    the type
     * @param request the request
     * @param builder the builder
     */
    public DBRequest(Type type, String request, RequestBuilder builder) {
        this.type = type;
        this.request = request;
        this.builder = builder == null ? new RequestBuilder(new CustomStatement(type, request), "internal") : builder;
    }

    /**
     * Instantiates a new Db request.
     *
     * @param sqlStatement the sql statement
     * @param builder      the builder
     */
    public DBRequest(SQLStatement sqlStatement, RequestBuilder builder) {
        this(sqlStatement.type, sqlStatement.getStatement(), builder);
    }

    /**
     * Gets builder.
     *
     * @return the builder
     */
    public RequestBuilder getBuilder() {
        return builder;
    }

    /**
     * Gets sub request.
     *
     * @return the sub request
     */
    public DBRequest getSubRequest() {
        return subRequest;
    }

    /**
     * Sets sub request.
     *
     * @param subRequest the sub request
     */
    public void setSubRequest(DBRequest subRequest) {
        this.subRequest = subRequest;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return getRequest();
    }

    /**
     * Gets request.
     *
     * @return the request
     */
    public String getRequest() {
        return request;
    }

    /**
     * Type - .
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public enum Type {
        /**
         * Query type.
         */
        Query,
        /**
         * Update type.
         */
        Update
    }

}
