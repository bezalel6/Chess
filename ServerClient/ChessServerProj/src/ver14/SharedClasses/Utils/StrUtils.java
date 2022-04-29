package ver14.SharedClasses.Utils;

import org.intellij.lang.annotations.Language;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * utility class for String related utilities
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class StrUtils {

    public static String bitsStr(long num) {
        if (num == 0)
            return "no bit is set";
        StringBuilder bldr = new StringBuilder("bits: ");
        int pos = 0;
        while (num != 0) {
            if ((num & 1) != 0) {
                bldr.append(pos + 1).append(" ");
            }
            num >>= 1;
            pos++;
        }
        bldr.append("are set");
        return bldr.toString();
    }


    /**
     * Is absolute url boolean.
     *
     * @param urlString the url string
     * @return the boolean
     */
    public static boolean isAbsoluteUrl(String urlString) {
        boolean result = false;
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            if (protocol != null && protocol.trim().length() > 0)
                result = true;
        } catch (MalformedURLException e) {
            return false;
        }
        return result || RegEx.URL.check(urlString);
    }

    /**
     * Dont cap full string.
     *
     * @param str the str
     * @return the string
     */
    public static String dontCapFull(String str) {
        return prefixToWord(str, RegEx.Prefixes.DONT_CAP_FULL);
    }

    /**
     * Prefix to word string.
     *
     * @param str    the str
     * @param prefix the prefix
     * @return the string
     */
    private static String prefixToWord(String str, String prefix) {
        Matcher matcher = Pattern.compile("(?=[a-zA-Z0-9])").matcher(str);
        if (matcher.find()) {
            return str.substring(0, matcher.start()) + prefix + str.substring(matcher.end());
        }
        return prefix + str;
    }

    /**
     * Html new lines string.
     *
     * @param str the str
     * @return the string
     */
    public static String htmlNewLines(String str) {
        if (str == null)
            return null;
        if (str.startsWith("<html>"))
            return str;
        return "<html>%s</html>".formatted(str.replace("\n", "<br />"));
    }

    /**
     * Count matches int.
     *
     * @param str   the str
     * @param match the match
     * @return the int
     */
    public static int countMatches(String str, @Language("RegExp") String match) {
        Pattern pattern = Pattern.compile(match);
        Matcher matcher = pattern.matcher(str);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Format string [ ] [ ].
     *
     * @param mat the mat
     * @return the string [ ] [ ]
     */
    public static String[][] format(String[][] mat) {
        return Arrays.stream(mat).map(StrUtils::format).toList().toArray(String[][]::new);
    }

    /**
     * Format string [ ].
     *
     * @param arr the arr
     * @return the string [ ]
     */
    public static String[] format(String[] arr) {
        return Arrays.stream(arr).map(StrUtils::format).toList().toArray(String[]::new);
    }

    /**
     * Format string.
     *
     * @param str the str
     * @return the string
     */
    public static String format(String str) {
        if (isEmpty(str)) return str;
        if (str.contains(RegEx.Prefixes.DONT_CAP_FULL)) {
//            return str.replace(RegEx.Prefixes.DONT_CAP_FULL, "");
//            return str;
            return clean(str);
        }
        StringBuilder ret = new StringBuilder();

        str = str.replaceAll("((?<=([A-Za-z][a-z]{1,10000000}))([A-Z]))", " $3");
        String[] split = str.split("(?=[ \\n\\t])|(<[^>]*>)");
        for (String s : split) {
            String word = s;
            if (word.equals("")) continue;
            while (RegEx.StrUtilSkip.check(word) && word.length() > 1) {
                ret.append(word.charAt(0));
                word = word.substring(1);
            }
            if (word.startsWith(RegEx.Prefixes.DATE_TIME)) {
                word = formatDate(word);
            } else if (s.contains(RegEx.Prefixes.DONT_CAP_WORD)) {
                word = word.replace(RegEx.Prefixes.DONT_CAP_WORD, "");
            } else if (RegEx.URL.check(word) || word.equals("vs")) {
//                word = "<a href=\"%s\">%s</a>".formatted(word, word);
            } else if (!word.matches("^[A-Z]+$")) {
                word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            }
            ret.append(word);
        }
        return clean(ret.toString());
    }

    /**
     * Is empty boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    /**
     * Clean string.
     *
     * @param str the str
     * @return the string
     */
    public static String clean(String str) {
        return str.replaceAll("[%s%s]".formatted(RegEx.Prefixes.DONT_CAP_FULL, RegEx.Prefixes.DONT_CAP_WORD), "");
    }

    /**
     * Format date string.
     *
     * @param longStr the long str
     * @return the string
     */
    public static String formatDate(String longStr) {
        longStr = longStr.replaceAll(RegEx.Prefixes.DATE_TIME, "");
        try {
            return formatDate(new Date(Long.parseLong(longStr)));
        } catch (NumberFormatException e) {
        }
        return longStr;
    }

    /**
     * Format date string.
     *
     * @param date the date
     * @return the string
     */
    public static String formatDate(Date date) {
        return formatDate(date, "dd/MM/yyyy hh:mm:ss aa");
    }

    /**
     * Format date string.
     *
     * @param date   the date
     * @param format the format
     * @return the string
     */
    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format, Locale.US).format(date);
    }

    /**
     * Parse urls string.
     *
     * @param str the str
     * @return the string
     */
    public static String parseURLS(String str) {
        StringBuilder bldr = new StringBuilder();
        String[] arr = str.split("(<? )");
        for (String s : arr) {
            if (RegEx.URL.check(s)) {
                s = "<a href=\"%s\">%s</a>".formatted(s, s);
            }
            bldr.append(s);
        }
        return bldr.toString();
    }

    /**
     * Uppercase string.
     *
     * @param str the str
     * @return the string
     */
    public static String uppercase(String str) {
        return dontCapWord(str.toUpperCase());
    }

    /**
     * Dont cap word string.
     *
     * @param str the str
     * @return the string
     */
    public static String dontCapWord(String str) {
        return prefixToWord(str, RegEx.Prefixes.DONT_CAP_WORD);
    }

    /**
     * Format date sql string.
     *
     * @param date the date
     * @return the string
     */
    public static String formatDateSQL(Date date) {
        return formatDate(date, "MM/dd/yyyy hh:mm:ss aa");
    }

    /**
     * Gets port.
     *
     * @param socket the socket
     * @return the port
     */
    public static int getPort(Socket socket) {
        return getPort(socket.getRemoteSocketAddress());
    }

    /**
     * Gets port.
     *
     * @param socketAddress the socket address
     * @return the port
     */
    public static int getPort(SocketAddress socketAddress) {
        return Integer.parseInt(socketAddress.toString().split(":")[1]);
    }

    /**
     * Gets url.
     *
     * @param socket the socket
     * @return the url
     */
    public static String getUrl(Socket socket) {
        return getUrl(socket.getRemoteSocketAddress());
    }

    /**
     * Gets url.
     *
     * @param socketAddress the socket address
     * @return the url
     */
    public static String getUrl(SocketAddress socketAddress) {
        return socketAddress.toString().split(":")[0];
    }

    /**
     * Fit inside string.
     *
     * @param str  the str
     * @param comp the comp
     * @return the string
     */
    public static String fitInside(String str, JComponent comp) {
//        return str;
        return "<html><body><p style='width: %spx;'>%s</p></body></html>".formatted(Math.max(comp.getWidth(), 20), str);
    }

    /**
     * Fix html string.
     *
     * @param str the str
     * @return the string
     */
    public static String fixHtml(String str) {
        if (!isEmpty(str) && str.contains("</html>") && str.split("</html>").length > 1) {
            String after = str.split("</html>")[1];
            return str.split("</html>")[0] + after + "</html>";
        }
        return str;
    }

//    public static String wrapInHtml(String str) {
//        return "<html><body><p style='width: 300px;'>" + str + "</p></body></html>";
////        return str;
//    }

    /**
     * Str inn string.
     *
     * @param objs the objs
     * @return the string
     */
    public static String strINN(Object... objs) {
        StringBuilder bldr = new StringBuilder();

        for (Object obj : objs) {
            if (obj != null) {
                bldr.append(obj).append(", ");
            }
        }
        return bldr.toString();
    }

    /**
     * Split arr string.
     *
     * @param arr the arr
     * @return the string
     */
    public static String splitArr(Object[] arr) {
        return splitArr(", ", arr);
    }

    /**
     * Split arr string.
     *
     * @param divide the divide
     * @param arr    the arr
     * @return the string
     */
    public static String splitArr(String divide, Object[] arr) {
        return splitArr(divide, arr, false);
    }

    /**
     * Split arr string.
     *
     * @param divide the divide
     * @param arr    the arr
     * @param format the format
     * @return the string
     */
    public static String splitArr(String divide, Object[] arr, boolean format) {
        StringBuilder bldr = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            String str = arr[i].toString();
            bldr.append(format ? format(str) : str);
            if (i < arr.length - 1) {
                bldr.append(divide);
            }
        }
        return bldr.toString();
    }

    /**
     * Create time greeting string.
     *
     * @return the string
     */
    public static String createTimeGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String str = "good " + TimeRange.getTimeRange(hour) + "!";
        return format(str);
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        System.out.println(createTimeStr(TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(12) + TimeUnit.SECONDS.toMillis(5) + 100));
    }

    /**
     * Create time str string.
     *
     * @param millis the millis
     * @return the string
     */
    public static String createTimeStr(long millis) {
        String format = "%02d";
        long mins = TimeUnit.MILLISECONDS.toMinutes(millis);
        long sec = TimeUnit.MILLISECONDS.toSeconds(millis - TimeUnit.MINUTES.toMillis(mins));
        long xSec = millis % 1000;
        xSec /= 100;

        return (format + ":" + format + ".%01d").formatted(mins, sec, xSec);
    }

    /**
     * Awful string.
     *
     * @param og the og
     * @return the string
     */
    public static String awful(String og) {
        StringBuilder bldr = new StringBuilder();
        boolean b = false;
        for (char c : og.toCharArray()) {
            bldr.append(b ? Character.toUpperCase(c) : Character.toLowerCase(c));
            b = !b;
        }
        return bldr.toString();
    }

    /**
     * Date time prefix string.
     *
     * @param str the str
     * @return the string
     */
    public static String dateTimePrefix(String str) {
        return prefixToWord(str, RegEx.Prefixes.DATE_TIME);
    }

    /**
     * Repeat string.
     *
     * @param iterationThingy the iteration thingy
     * @param numOfIterations the num of iterations
     * @return the string
     */
    public static String repeat(IterationThingy<String> iterationThingy, int numOfIterations) {
        StringBuilder bldr = new StringBuilder();
        for (int i = 0; i < numOfIterations; i++) {
            bldr.append(iterationThingy.iteration(i, i == numOfIterations - 1));
        }
        return bldr.toString();
    }

    /**
     * Time range.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    enum TimeRange {
        /**
         * Night time range.
         */
        Night(23, 6),
        /**
         * Early morning time range.
         */
        EarlyMorning(6, 10),
        /**
         * Late morning time range.
         */
        LateMorning(10, 12),
        /**
         * Early after noon time range.
         */
        EarlyAfterNoon(12, 14),
        /**
         * After noon time range.
         */
        AfterNoon(14, 17),
        /**
         * Evening time range.
         */
        Evening(17, 20),
        /**
         * Late evening time range.
         */
        LateEvening(20, 23);
        /**
         * The Map.
         */
        final static Map<Integer, TimeRange> map;

        static {
            map = new HashMap<>();
            for (TimeRange timeRange : values()) {

                int numOfI = Math.abs(timeRange.start - timeRange.end);
                for (int i = 0; i < numOfI; i++) {
                    int hour = (timeRange.start + i) % 24;
                    map.put(hour, timeRange);
                }
            }
        }

        /**
         * The Start.
         */
        final int start,
        /**
         * End time range.
         */
        end;

        /**
         * Instantiates a new Time range.
         *
         * @param start the start
         * @param end   the end
         */
        TimeRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        /**
         * Gets time range.
         *
         * @param hour the hour
         * @return the time range
         */
        public static TimeRange getTimeRange(int hour) {
            return map.get(hour);
        }

        /**
         * Is between boolean.
         *
         * @param hour the hour
         * @return the boolean
         */
        public boolean isBetween(int hour) {
            return hour >= start && hour < end;
        }
    }

    /**
     * Iteration thingy.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public interface IterationThingy<T> {
        /**
         * Iteration t.
         *
         * @param i      the
         * @param isLast the is last
         * @return the t
         */
        T iteration(int i, boolean isLast);

    }

}
