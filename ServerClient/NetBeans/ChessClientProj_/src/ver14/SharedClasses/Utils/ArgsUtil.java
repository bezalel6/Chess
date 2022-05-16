package ver14.SharedClasses.Utils;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 * represents a utility for integrating with {@link String}[] arguments
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
     * represents an argument that might have been passed.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public record OptionalArg(String str) {

        /**
         * Gets the string value of this argument.
         *
         * @return the string value of the argument if it was found, null otherwise.
         */
        public String getString() {
            return str;
        }

        /**
         * Gets boolean.
         *
         * @param defVal if it doesn't exist
         * @return the boolean
         */
        public Boolean getBoolean(Boolean defVal) {
            try {
                checkExists();
                return Boolean.parseBoolean(str);
            } catch (Exception e) {
                return defVal;
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
         * was this argument found
         *
         * @return <code>true</code> if the argument was found
         */
        public boolean exists() {
            return (str != null);
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
         * @param defVal the if err
         * @return the int
         */
        public int getInt(int defVal) {
            try {
                checkExists();
                return Integer.parseInt(str);
            } catch (Exception e) {
                return defVal;
            }
        }
    }
}
