package ver4.SharedClasses;

import java.util.concurrent.TimeUnit;

public class StrUtils {
    public static String uppercaseFirstLetters(String str) {
        StringBuilder ret = new StringBuilder();
        for (String word : str.split(" ")) {
            ret.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");
        }
        return ret.toString();
    }

    public static String wrapInHtml(String str) {
        return "<html><body><p style='width: 200px;'>" + str + "</p></body></html>";
    }

    public static String createTimeStr(long millis) {
        long h = TimeUnit.MILLISECONDS.toHours(millis);
        long m = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(h);
        long s = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        long xs = TimeUnit.MILLISECONDS.toMicros(millis) - TimeUnit.SECONDS.toMicros(TimeUnit.MILLISECONDS.toSeconds(millis));
        String divider = ":";
        String hours = h == 0 ? "" : h + divider;
        String minutes = m == 0 ? "" : m + divider;
        String seconds = s + divider;
        String micros = xs + "";
        int strEnd = Math.min(1, micros.length());
        micros = micros.substring(0, strEnd);

        return hours + minutes + seconds + micros;
    }

}
