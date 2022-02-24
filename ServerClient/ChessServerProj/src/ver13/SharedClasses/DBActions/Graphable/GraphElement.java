package ver13.SharedClasses.DBActions.Graphable;

public class GraphElement {
    protected final int num;
    protected final GraphElementType graphElementType;
    protected final String name;

    public GraphElement(int num, String name, GraphElementType graphElementType) {
        this.num = num;
        this.graphElementType = graphElementType;
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public GraphElementType getGraphElementType() {
        return graphElementType;
    }

    public String getName() {
        return name;
    }
}
