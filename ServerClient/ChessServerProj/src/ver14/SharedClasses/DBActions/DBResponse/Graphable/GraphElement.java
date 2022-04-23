package ver14.SharedClasses.DBActions.DBResponse.Graphable;

import java.io.Serializable;

/*
 * GraphElement
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * GraphElement -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * GraphElement -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class GraphElement implements Serializable {
    protected final double num;
    protected final GraphElementType graphElementType;
    protected final String name;

    public GraphElement(double num, String name, GraphElementType graphElementType) {
        this.num = num;
        this.graphElementType = graphElementType;
        this.name = name;
    }

    public double getNum() {
        return num;
    }

    public GraphElementType getGraphElementType() {
        return graphElementType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ": " + num;
    }
}
