package ver14.SharedClasses.Utils;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * The type Args utils.
 */
public class ArgsUtil {
    private final Supplier<Stream<String>> streamSupplier;

    private ArgsUtil(String[] args) {
        this.streamSupplier = () -> Arrays.stream(args);
    }

    public static ArgsUtil create(String[] args) {
        return new ArgsUtil(args);
    }

    /**
     * Equals sign optional arg.
     * for any arg of this format: preEqualStr=%argval%
     *
     * @param preEqualStr the pre equal str
     * @return the optional arg value(assuming there is one) %argval% in the example above
     */
    public OptionalArg equalsSign(String preEqualStr) {
        String str = streamSupplier.get().filter(s -> s.startsWith(preEqualStr + "=")).findAny().orElse("");
        if (!StrUtils.isEmpty(str)) {
            str = str.substring(str.indexOf('=') + 1);
        }
        return new OptionalArg(str);
    }

    public OptionalArg plainTextIgnoreCase(String str) {
        return new OptionalArg(streamSupplier.get().filter(s -> s.equalsIgnoreCase(str)).findAny().orElse(null));
    }

    public record OptionalArg(String str) {

        public String getString() {
            return str;
        }

        public Boolean getBoolean(Boolean ifErr) {
            try {
                checkExists();
                return Boolean.parseBoolean(str);
            } catch (Exception e) {
                return ifErr;
            }
        }

        private void checkExists() throws Exception {
            if (!exists())
                throw new Exception();
        }

        public boolean exists() {
            return !StrUtils.isEmpty(str);
        }

        public int getInt() {
            return getInt(-1);
        }

        public int getInt(int ifErr) {
            try {
                checkExists();
                return Integer.parseInt(str);
            } catch (Exception e) {
                return ifErr;
            }
        }
    }
}
