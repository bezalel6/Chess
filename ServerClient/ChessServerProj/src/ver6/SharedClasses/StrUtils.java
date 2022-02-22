package ver6.SharedClasses;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class StrUtils {

    public static String dontCapFull(String str) {
        return RegEx.Prefixes.DONT_CAP_FULL + str;
    }

    public static String dontCapWord(String str) {
        return RegEx.Prefixes.DONT_CAP_WORD + str;
    }

    public static String htmlNewLines(String str) {
        return "<html>%s</html>".formatted(str.replace("\n", "<br />"));
    }

    public static String uppercaseFirstLetters(String str) {
        if (str == null || str.trim().equals("")) return str;
        if (str.contains(RegEx.Prefixes.DONT_CAP_FULL)) {
            return str.replace(RegEx.Prefixes.DONT_CAP_FULL, "");
        }
        StringBuilder ret = new StringBuilder();
//        String[] split = str.split("[ \\n\\t]");
        String[] split = str.split("(?=[ \\n\\t])");
        for (String s : split) {
            String word = s;
            if (word.equals("")) continue;
            while (RegEx.StrUtilSkip.check(word.charAt(0) + "", true) && word.length() > 1) {
                ret.append(word.charAt(0));
                word = word.substring(1);
            }
            if (word.startsWith(RegEx.Prefixes.DONT_CAP_WORD)) {
                ret.append(word.replace(RegEx.Prefixes.DONT_CAP_WORD, ""));
            } else {
                ret.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
            }
        }
        return ret.toString();
    }

//    public static String wrapInHtml(String str) {
//        return "<html><body><p style='width: 300px;'>" + str + "</p></body></html>";
////        return str;
//    }

    public static String fitInside(String str, JComponent comp) {
//        return str;
        return "<html><body><p style='width: %spx;'>%s</p></body></html>".formatted(Math.max(comp.getWidth(), 200), str);
    }

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
}
