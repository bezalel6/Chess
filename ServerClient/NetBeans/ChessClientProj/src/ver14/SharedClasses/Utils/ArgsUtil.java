package ver14.SharedClasses.Utils;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 * The type Args utils.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ArgsUtil {
    /**
     * The Stream supplier.
     */
    private final Supplier<Stream<String>> streamSupplier;

    /**
     * Instantiates a new Args util.
     *
     * @param args the args
     */
    private ArgsUtil(String[] args) {
        this.streamSupplier = () -> Arrays.stream(args);
    }

    /**
     * Create args util.
     *
     * @param args the args
     * @return the args util
     */
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

    /**
     * Plain text ignore case optional arg.
     *
     * @param str the str
     * @return the optional arg
     */
    public OptionalArg plainTextIgnoreCase(String str) {
        return new OptionalArg(streamSupplier.get().filter(s -> s.equalsIgnoreCase(str)).findAny().orElse(null));
    }

    /**
     * Optional arg.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public record OptionalArg(String str) {

        /**
         * Gets string.
         *
         * @return the string
         */
        public String getString() {
            return str;
        }

        /**
         * Gets boolean.
         *
         * @param ifErr the if err
         * @return the boolean
         */
        public Boolean getBoolean(Boolean ifErr) {
            try {
                checkExists();
                return Boolean.parseBoolean(str);
            } catch (Exception e) {
                return ifErr;
            }
        }

        /**
         * Check exists.
         *
         * @throws Exception the exception
         */
        private void checkExists() throws Exception {
            if (!exists())
                throw new Exception();
        }

        /**
         * Exists boolean.
         *
         * @return the boolean
         */
        public boolean exists() {
            return !StrUtils.isEmpty(str);
        }

        /**
         * Gets int.
         *
         * @return the int
         */
        public int getInt() {
            return getInt(-1);
        }

        /**
         * Gets int.
         *
         * @param ifErr the if err
         * @return the int
         */
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
