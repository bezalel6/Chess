package ver14.SharedClasses.DBActions.Arg;

import ver14.SharedClasses.IDsGenerator;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Arg implements Serializable {
    private static final IDsGenerator ids;

    static {
        ids = new IDsGenerator();
    }

    public final String repInStr;
    public final boolean escape;
    public final ArgType argType;
    public final Config config;
    private boolean isUserInput;

    public Arg(ArgType argType) {
        this(argType, null);
    }

    public Arg(ArgType argType, Config config) {
        this(argType, false, config);
    }

    public Arg(ArgType argType, boolean escape, Config config) {
        this.repInStr = wrap(ids.generate() + argType.name());
        this.escape = escape;
        this.isUserInput = argType.isUserInput;
        this.argType = argType;
        this.config = config;
    }

    private static String wrap(String wrapping) {
        return "---" + wrapping + "---";
    }

    public boolean isUserInput() {
        return isUserInput;
    }

    public void setUserInput(boolean userInput) {
        isUserInput = userInput;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arg)) return false;
        Arg arg = (Arg) o;
        return Objects.equals(repInStr, arg.repInStr);
    }

    @Override
    public String toString() {
        return repInStr;
    }

    public String createVal(Object val) {
        if (val == null && config != null && config.canUseDefault)
            val = config.getDefault();
//        String str = val.toString();

        assert val != null;
        return switch (this.argType) {

            case Date -> //mmddyyyy bc sql is dumb
                    "#" + (StrUtils.formatDateSQL((Date) val)) + "#";
//            case Date -> ((Date) val).getTime() + "";

            default -> val.toString();
        };
//        return str;
    }
}
