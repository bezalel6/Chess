package ver14.SharedClasses.Utils;

import org.intellij.lang.annotations.Language;
import ver14.SharedClasses.Game.GameSetup.AiParameters;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//ew
public class RegEx implements Serializable {
    public static final RegEx Fen = new RegEx("^$|\\s*([rnbqkpRNBQKP1-8]+\\/){7}([rnbqkpRNBQKP1-8]+)\\s[bw-]\\s(([a-hkqA-HKQ]{1,4})|(-))\\s(([a-h][36])|(-))\\s\\d+\\s\\d+\\s*", "standard fen");

    public static final RegEx Username = new RegEx("^[a-zA-Z0-9_.-]{5,10}$", "5-10 characters a-z 0-9 _.-", Prefixes.GUEST_PREFIX, "User");


    public static final RegEx Password = new RegEx("^[a-zA-Z0-9_.-]{5,10}$", "5-10 characters a-z 0-9 _.-", "password");


    public static final RegEx Icon = new RegEx("\\.(png|gif)$", "");


    public static final RegEx StrUtilSkip = new RegEx("(^[ \\n\\t\\[])|(<[^>]*>)", "");//skip over spcaes and stuff


    public static final RegEx Numbers = new RegEx("^[0-9]+$", "enter number");


    public static final RegEx URL = new RegEx("[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@%_\\+.~#?&//=]*)", "enter valid url");


    public static final RegEx DontSaveGame = new RegEx(Prefixes.GUEST_PREFIX + "|" + AiParameters.AiType.MyAi + "|" + AiParameters.AiType.Stockfish, "");


    public static final RegEx IPPAddress = new RegEx("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3} *: *[0-9]{2,5}$", "[IP : PORT]");


    public static final RegEx Any = new RegEx("", "");
    
    public final String[] dontMatch;
    private final String details;
    private final @Language("RegExp")
    String regEx;
    private boolean useDontMatch;

    public RegEx(@Language("RegExp") String regEx, String details, String... dontMatch) {
        this(regEx, details, true, dontMatch);
    }

    public RegEx(@Language("RegExp") String regEx, String details, boolean useDontMatch, String... dontMatch) {
        this.regEx = regEx;
        this.dontMatch = dontMatch;
        this.useDontMatch = useDontMatch;
        this.details = details;
    }

    public static boolean isSavedDate(String str) {
        return Pattern.compile("date", Pattern.CASE_INSENSITIVE).matcher(str).find();
    }

    public RegEx canBeEmpty(boolean bool, String emptyDetails) {
        if (bool)
            return new RegEx("(^$)|(%s)".formatted(regEx), details + " " + emptyDetails, dontMatch);
        return this;
    }

    public RegEx get(boolean useDontMatch) {
        return new RegEx(regEx, details, dontMatch) {{
            setUseDontMatch(useDontMatch);
        }};
    }

    public void setUseDontMatch(boolean useDontMatch) {
        this.useDontMatch = useDontMatch;
    }

    public String getDetails() {
        if (!useDontMatch) {
            return details;
        }
        return details + (dontMatch.length == 0 ? "" : "\ncannot contain " + Arrays.toString(dontMatch));
    }


    public boolean check(String str) {
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

    public String getRegex() {
        return regEx;
    }

    public static class Prefixes {
        public static final String GUEST_PREFIX = "GUEST";
        //        public static final String DONT_CAP_FULL = "!~~!";
        public static final String DONT_CAP_FULL = "\u200B";
        //        public static final String DONT_CAP_WORD = "!~!";
        public static final String DONT_CAP_WORD = "\u2060";

        public static final String DATE_TIME = ":dt:";
    }

}
