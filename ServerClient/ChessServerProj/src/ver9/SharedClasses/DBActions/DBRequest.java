/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ver9.SharedClasses.DBActions;

import org.intellij.lang.annotations.Language;

import java.io.Serializable;

public class DBRequest implements Serializable {
    public final Type type;
    private final @Language("SQL")
    String request;

    public DBRequest(@Language("SQL") String request, Type type) {
        this.request = request;
        this.type = type;
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
        Select, Update, Insert
    }

}
