package ver14.SharedClasses.Utils;

/**
 * Math utility class.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MathUtils {
    /**
     * Log in base 2.
     *
     * @param num the number
     * @return the result
     */
    public static double log2(double num) {
        return log(num, 2);
    }

    /**
     * Log.
     *
     * @param num  the number
     * @param base the base
     * @return the result
     */
    public static double log(double num, int base) {
        return Math.log(num) / Math.log(base);
    }
}
