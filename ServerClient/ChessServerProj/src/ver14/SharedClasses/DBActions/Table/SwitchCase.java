package ver14.SharedClasses.DBActions.Table;

import ver14.SharedClasses.DBActions.Condition;


/**
 * Switch case - represents a case that is meant to be used inside a switch case col.
 * if the {@link #condition} is true, the {@link #ifTrue} col will display in the switch case col
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class SwitchCase {
    /**
     * The Condition.
     */
    private final Condition condition;
    /**
     * The If true.
     */
    private final Col ifTrue;

    /**
     * Instantiates a new Switch case.
     *
     * @param condition the condition
     * @param ifTrue    the if true
     */
    public SwitchCase(Condition condition, Col ifTrue) {
        this.condition = condition;
        this.ifTrue = ifTrue;
    }

    /**
     * Equals switch case.
     *
     * @param col    the col
     * @param value  the value
     * @param ifTrue the if true
     * @return the switch case
     */
    public static SwitchCase equals(Col col, String value, Col ifTrue) {
        return new SwitchCase(Condition.equals(col, value), ifTrue);
    }

    /**
     * Default case switch case.
     *
     * @param ifTrue the if true
     * @return the switch case
     */
    public static SwitchCase defaultCase(Col ifTrue) {
        return new SwitchCase(new Condition("TRUE"), ifTrue);
    }

    /**
     * Condition condition.
     *
     * @return the condition
     */
    public Condition condition() {
        return condition;
    }

    /**
     * If true col.
     *
     * @return the col
     */
    public Col ifTrue() {
        return ifTrue;
    }


    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return condition + ", " + ifTrue;
    }

}
