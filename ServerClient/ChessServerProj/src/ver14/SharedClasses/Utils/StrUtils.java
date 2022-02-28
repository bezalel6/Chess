package ver14.SharedClasses.Utils;

import org.intellij.lang.annotations.Language;
import ver14.SharedClasses.RegEx;

import javax.swing.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtils {

    public static String dontCapFull(String str) {
        return prefixToWord(str, RegEx.Prefixes.DONT_CAP_FULL);
    }

    private static String prefixToWord(String str, String prefix) {
        Matcher matcher = Pattern.compile("(?=[a-zA-Z0-9])").matcher(str);
        if (matcher.find()) {
            return str.substring(0, matcher.start()) + prefix + str.substring(matcher.end());
        }
        return prefix + str;
    }

    public static String htmlNewLines(String str) {
        if (str.startsWith("<html>"))
            return str;
        return "<html>%s</html>".formatted(str.replace("\n", "<br />"));
    }

    public static int countMatches(String str, @Language("RegExp") String match) {
        Pattern pattern = Pattern.compile(match);
        Matcher matcher = pattern.matcher(str);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    public static String[][] format(String[][] mat) {
        return Arrays.stream(mat).map(StrUtils::format).toList().toArray(String[][]::new);
    }

    public static String[] format(String[] arr) {
        return Arrays.stream(arr).map(StrUtils::format).toList().toArray(String[]::new);
    }

    public static String format(String str) {
        if (isEmpty(str)) return str;
        if (str.contains(RegEx.Prefixes.DONT_CAP_FULL)) {
            return str.replace(RegEx.Prefixes.DONT_CAP_FULL, "");
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
            } else {
                word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            }
            ret.append(word);
        }
        return ret.toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    public static String formatDate(String longStr) {
        longStr = longStr.replaceAll(RegEx.Prefixes.DATE_TIME, "");
        try {
            return formatDate(new Date(Long.parseLong(longStr)));
        } catch (NumberFormatException e) {
        }
        return longStr;
    }

    public static String formatDate(Date date) {
        return formatDate(date, "dd/MM/yyyy hh:mm:ss aa");
    }

    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format, Locale.US).format(date);
    }

    public static String uppercase(String str) {
        return dontCapWord(str.toUpperCase());
    }

    public static String dontCapWord(String str) {
        return prefixToWord(str, RegEx.Prefixes.DONT_CAP_WORD);
    }

    public static String formatDateSQL(Date date) {
        return formatDate(date, "MM/dd/yyyy hh:mm:ss aa");
    }

    public static int getPort(Socket socket) {
        return getPort(socket.getRemoteSocketAddress());
    }

    public static int getPort(SocketAddress socketAddress) {
        return Integer.parseInt(socketAddress.toString().split(":")[1]);
    }

    public static String getUrl(Socket socket) {
        return getUrl(socket.getRemoteSocketAddress());
    }

    public static String getUrl(SocketAddress socketAddress) {
        return socketAddress.toString().split(":")[0];
    }

    public static String fitInside(String str, JComponent comp) {
//        return str;
        return "<html><body><p style='width: %spx;'>%s</p></body></html>".formatted(Math.max(comp.getWidth(), 200), str);
    }

//    public static String wrapInHtml(String str) {
//        return "<html><body><p style='width: 300px;'>" + str + "</p></body></html>";
////        return str;
//    }

    //todo make recursive
    public static String createTimeStr(long millis) {
        long h = TimeUnit.MILLISECONDS.toHours(millis);
        long m = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(h);
        long s = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        long xs = TimeUnit.MILLISECONDS.toMicros(millis) - TimeUnit.SECONDS.toMicros(TimeUnit.MILLISECONDS.toSeconds(millis));
        String divider = ":";
        //sorta decimal
        String decimal = ".";
        String hours = h == 0 ? "" : h + divider;
        String minutes = m == 0 ? "" : m + divider;
        String seconds = s + decimal;
        String micros = xs + "";
        int strEnd = Math.min(1, micros.length());
        micros = micros.substring(0, strEnd);

        return hours + minutes + seconds + micros;
    }

    public static String wrapInHtml(String str) {
        return str;
    }

    public static String strINN(Object... objs) {
        StringBuilder bldr = new StringBuilder();
        for (Object obj : objs) {
            if (obj != null) {
                bldr.append(obj).append(", ");
            }
        }
        return bldr.toString();
    }

    public static String splitArr(Object[] arr) {
        return splitArr(", ", arr);
    }

    public static String splitArr(String divide, Object[] arr) {
        return splitArr(divide, arr, false);
    }

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

    public static String clean(String str) {
        return str.replaceAll("[%s%s]".formatted(RegEx.Prefixes.DONT_CAP_FULL, RegEx.Prefixes.DONT_CAP_WORD), "");
    }

    public static String createTimeGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String str = "good " + TimeRange.getTimeRange(hour) + "!";
        return format(str);
    }

    public static void main(String[] args) {
        System.out.println(awful("InMeShwamp"));
    }

    public static String awful(String og) {
        StringBuilder bldr = new StringBuilder();
        boolean b = false;
        for (char c : og.toCharArray()) {
            bldr.append(b ? Character.toUpperCase(c) : Character.toLowerCase(c));
            b = !b;
        }
        return bldr.toString();
    }

    public static String dateTimePrefix(String str) {
        return prefixToWord(str, RegEx.Prefixes.DATE_TIME);
    }

    enum TimeRange {
        Night(23, 6),
        EarlyMorning(6, 10),
        LateMorning(10, 12),
        EarlyAfterNoon(12, 14),
        AfterNoon(14, 17),
        Evening(17, 20),
        LateEvening(20, 23);
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

        final int start, end;

        TimeRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public static TimeRange getTimeRange(int hour) {
            return map.get(hour);
        }

        public boolean isBetween(int hour) {
            return hour >= start && hour < end;
        }
    }
}
