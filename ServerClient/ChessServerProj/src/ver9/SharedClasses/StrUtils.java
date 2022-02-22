package ver9.SharedClasses;

import javax.swing.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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

    public static void main(String[] args) {
        String username = "abcd10gg6";
        ArrayList<String> options = new ArrayList<>();
        Pattern pattern = Pattern.compile("([0-9]+)");
        pattern.matcher(username).results().forEach(match -> {
            int num = Integer.parseInt(match.group());
            num++;
            String str = username.substring(0, match.start()).concat(num + "").concat(username.substring(match.end()));
            options.add(str);
        });
    }

    public static String format(String str) {
        if (str == null || str.trim().equals("")) return str;
        if (str.contains(RegEx.Prefixes.DONT_CAP_FULL)) {
            return str.replace(RegEx.Prefixes.DONT_CAP_FULL, "");
        }
        StringBuilder ret = new StringBuilder();

        str = str.replaceAll("((?<=([A-Za-z][a-z]{1,10000000}))([A-Z]))", " $3");
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

    public static String strINN(Object... objs) {
        StringBuilder bldr = new StringBuilder();
        for (Object obj : objs) {
            if (obj != null) {
                bldr.append(obj).append(", ");
            }
        }
        return bldr.toString();
    }
}
