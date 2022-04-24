package ver14.SharedClasses.DBActions.Table;

import ver14.SharedClasses.DBActions.Condition;


public class SwitchCase {
    private final Condition condition;
    private final Col ifTrue;

    public SwitchCase(Condition condition, Col ifTrue) {
        this.condition = condition;
        this.ifTrue = ifTrue;
    }

    public static SwitchCase equals(Col col, String value, Col ifTrue) {
        return new SwitchCase(Condition.equals(col, value), ifTrue);
    }

    public static SwitchCase defaultCase(Col ifTrue) {
        return new SwitchCase(new Condition("TRUE"), ifTrue);
    }

    public Condition condition() {
        return condition;
    }

    public Col ifTrue() {
        return ifTrue;
    }


    @Override
    public String toString() {
        return condition + ", " + ifTrue;
    }

}
