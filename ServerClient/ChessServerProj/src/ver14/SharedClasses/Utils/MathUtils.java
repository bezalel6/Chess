package ver14.SharedClasses.Utils;

/**
 * Math utils.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MathUtils {
    /**
     * Log.
     *
     * @param num  the num
     * @param base the base
     * @return the double
     */
    public static double log(double num, int base) {
        return Math.log(num) / Math.log(base);
    }
}
