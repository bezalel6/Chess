package ver14.SharedClasses.DBActions.Arg;

import ver14.SharedClasses.Callbacks.ObjCallback;
import ver14.SharedClasses.Misc.IDsGenerator;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/*
 * Arg
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * Arg -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * Arg -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

/**
 * Arg - .
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Arg implements Serializable {
    private static final IDsGenerator ids;

    static {
        ids = new IDsGenerator();
    }

    /**
     * The Rep in str.
     */
    public final String repInStr;
    /**
     * The Escape.
     */
    public final boolean escape;
    /**
     * The Arg type.
     */
    public final ArgType argType;
    /**
     * The Config.
     */
    public final Config<?> config;
    private boolean isUserInput;

    /**
     * Instantiates a new Arg.
     *
     * @param argType the arg type
     */
    public Arg(ArgType argType) {
        this(argType, null);
    }

    /**
     * Instantiates a new Arg.
     *
     * @param argType the arg type
     * @param config  the config
     */
    public Arg(ArgType argType, Config<?> config) {
        this(argType, false, config);
    }

    /**
     * Instantiates a new Arg.
     *
     * @param argType the arg type
     * @param escape  the escape
     * @param config  the config
     */
    public Arg(ArgType argType, boolean escape, Config<?> config) {
        this.repInStr = wrap(ids.generate() + argType.name());
        this.escape = escape;
        this.isUserInput = argType.isUserInput;
        this.argType = argType;
        this.config = config;
    }

    private static String wrap(String wrapping) {
        return "---" + wrapping + "---";
    }

    /**
     * Is user input boolean.
     *
     * @return the boolean
     */
    public boolean isUserInput() {
        return isUserInput;
    }

    /**
     * Sets user input.
     *
     * @param userInput the user input
     */
    public void setUserInput(boolean userInput) {
        isUserInput = userInput;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arg)) return false;
        Arg arg = (Arg) o;
        return Objects.equals(repInStr, arg.repInStr);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return repInStr;
    }

    /**
     * Create val string.
     *
     * @param val the val
     * @return the string
     */
    public String createVal(Object val) {
        if (val == null && config != null && config.canUseDefault)
            val = config.getDefault();
//        String str = val.toString();

        assert val != null;

        if (val instanceof ObjCallback<?> callback)
            val = callback.get();
        String str = switch (this.argType) {

            case Date -> //mmddyyyy bc sql is dumb
                    "#" + (StrUtils.formatDateSQL((Date) val)) + "#";
            default -> val.toString();
        };
        if (escape)
            str = "'" + str + "'";
        return str;
    }
}
