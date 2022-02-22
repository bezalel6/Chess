package ver6.server.DB;

public class Condition {
    public final String nextConditionRelation;
    private final Table.Col col;
    private final String value;
    private final String condition;
    private String preFix = "", postFix = "";


    //todo make a constructor for a few conditions and relations
    public Condition(Table.Col col, String value, String condition) {
        this(col, value, condition, "AND");
    }

    public Condition(Table.Col col, String value, String condition, String nextConditionRelation) {
        this.nextConditionRelation = nextConditionRelation;
        this.col = col;
        this.value = value;
        this.condition = condition;
    }

    public void setPreFix(String preFix) {
        this.preFix = preFix;
    }

    public void setPostFix(String postFix) {
        this.postFix = postFix;
    }

    @Override
    public String toString() {
        return preFix + " " + col + condition + "'" + value + "' " + postFix;
    }

    public enum Type {
        And, Like, Equals, NotEquals
    }

    public enum Relation {
        And, Or
    }
}
