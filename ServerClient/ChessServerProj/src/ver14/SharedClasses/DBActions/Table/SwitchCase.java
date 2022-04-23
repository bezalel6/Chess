package ver14.SharedClasses.DBActions.Table;

import ver14.SharedClasses.DBActions.Condition;

/*
 * SwitchCase
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * SwitchCase -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * SwitchCase -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

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
