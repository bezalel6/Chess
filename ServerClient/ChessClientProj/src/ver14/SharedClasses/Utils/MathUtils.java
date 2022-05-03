package ver14.SharedClasses.Utils;

/**
 * Math utility class.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MathUtils {
    /**
     * Log 2 double.
     *
     * @param num the num
     * @return the double
     */
    public static double log2(double num) {
        return log(num, 2);
    }

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
