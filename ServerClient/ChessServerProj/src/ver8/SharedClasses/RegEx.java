package ver8.SharedClasses;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum RegEx {
    Fen("^$|\\s*([rnbqkpRNBQKP1-8]+\\/){7}([rnbqkpRNBQKP1-8]+)\\s[bw-]\\s(([a-hkqA-HKQ]{1,4})|(-))\\s(([a-h][36])|(-))\\s\\d+\\s\\d+\\s*", "standard fen"),
    Username("^[a-zA-Z0-9_.-]{5,10}$", "5-10 characters a-z 0-9 _.-", Prefixes.GUEST_PREFIX, "User"),
    Password("^[a-zA-Z0-9_.-]{5,10}$", "5-10 characters a-z 0-9 _.-", "password"),
    Icon("\\.(png|gif)$", ""),
    StrUtilSkip("^[ \\n\\t\\[]", ""),
    DontSaveGame(Prefixes.GUEST_PREFIX, "");
    public final String[] dontMatch;
    private final String details;
    private final String regEx;

    RegEx(String regEx, String details, String... dontMatch) {
        this.regEx = regEx;
        this.dontMatch = dontMatch;
        this.details = details;
    }

    public String getDetails(boolean useDontMatch) {
        if (!useDontMatch) {
            return details;
        }
        return details + (dontMatch.length == 0 ? "" : "\ncannot contain " + Arrays.toString(dontMatch));
    }

    public boolean check(String str) {
        return check(str, true);
    }

    public boolean check(String str, boolean useDontMatch) {
        if (str == null)
            str = "";
        final Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(str);
        if (!useDontMatch) {
            return matcher.find();
        }
        String finalStr = str;
        return matcher.find() && Arrays.stream(dontMatch).noneMatch(s -> {
            Pattern p = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(finalStr);
            return m.find();
        });
    }

    public class Prefixes {
        public static final String GUEST_PREFIX = "GUEST";
        public static final String DONT_CAP_FULL = "\u200B";
        public static final String DONT_CAP_WORD = "\u2060";

    }

}
