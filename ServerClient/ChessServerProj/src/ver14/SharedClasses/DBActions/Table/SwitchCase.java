package ver14.SharedClasses.DBActions.Table;

import ver14.SharedClasses.DBActions.Condition;


/**
 * Switch case - represents a case that is meant to be used inside a switch case column.
 * if the {@link #condition} is true, the {@link #ifTrue} col will display in the switch case col
 *
 * @param condition The Condition.
 * @param ifTrue    The If true.
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public record SwitchCase(Condition condition, Col ifTrue) {
    /**
     * Instantiates a new Switch case.
     *
     * @param condition the condition
     * @param ifTrue    the column that will show if the condition is true
     */
    public SwitchCase {
    }

    /**
     * if the passed {@code col} is equal to {@code value}, the {@code ifTrue} column will display.
     *
     * @param col    the col
     * @param value  the value
     * @param ifTrue the if true
     * @return the created switch case
     */
    public static SwitchCase equals(Col col, String value, Col ifTrue) {
        return new SwitchCase(Condition.equals(col, value), ifTrue);
    }

    /**
     * if none of the previous cases matched, this column will show.
     *
     * @param ifTrue the if true
     * @return the switch case
     */
    public static SwitchCase defaultCase(Col ifTrue) {
        return new SwitchCase(new Condition("TRUE"), ifTrue);
    }

    /**
     * Condition.
     *
     * @return the condition
     */
    @Override
    public Condition condition() {
        return condition;
    }

    /**
     * If true col.
     *
     * @return the col
     */
    @Override
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
