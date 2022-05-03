package ver14.SharedClasses.Utils;

import org.intellij.lang.annotations.Language;
import ver14.SharedClasses.Game.GameSetup.AiParameters;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Reg ex.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class RegEx implements Serializable {
    /**
     * The constant Fen.
     */
    public static final RegEx Fen = new RegEx("^$|\\s*([rnbqkpRNBQKP1-8]+\\/){7}([rnbqkpRNBQKP1-8]+)\\s[bw-]\\s(([a-hkqA-HKQ]{1,4})|(-))\\s(([a-h][36])|(-))\\s\\d+\\s\\d+\\s*", "standard fen");

    /**
     * The constant Username.
     */
    public static final RegEx Username = new RegEx("^[a-zA-Z0-9_.-]{5,10}$", "5-10 characters a-z 0-9 _.-", Prefixes.GUEST_PREFIX, "User");


    /**
     * The constant Password.
     */
    public static final RegEx Password = new RegEx("^[a-zA-Z0-9_.-]{5,10}$", "5-10 characters a-z 0-9 _.-", "password");


    /**
     * The constant Icon.
     */
    public static final RegEx Icon = new RegEx("\\.(png|gif)$", "");


    /**
     * The constant StrUtilSkip.
     */
    public static final RegEx StrUtilSkip = new RegEx("(^[ \\n\\t\\[])|(<[^>]*>)", "");//skip over spcaes and stuff


    /**
     * The constant Numbers.
     */
    public static final RegEx Numbers = new RegEx("^[0-9]+$", "enter number");


    /**
     * The constant URL.
     */
    public static final RegEx URL = new RegEx("[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@%_\\+.~#?&//=]*)", "enter valid url");


    /**
     * The constant DontSaveGame.
     */
    public static final RegEx DontSaveGame = new RegEx(Prefixes.GUEST_PREFIX + "|" + AiParameters.AiType.MyAi + "|" + AiParameters.AiType.Stockfish, "");


    /**
     * The constant IPPAddress.
     */
    public static final RegEx IPPortAddress = new RegEx("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3} *: *[0-9]{2,5}$", "[IP : PORT]");


    /**
     * The constant Any.
     */
    public static final RegEx Any = new RegEx("", "");

    /**
     * The Dont match.
     */
    public final String[] dontMatch;
    /**
     * The Details.
     */
    private final String details;
    /**
     * The Reg ex.
     */
    private final @Language("RegExp")
    String regEx;
    /**
     * The Use dont match.
     */
    private boolean useDontMatch;

    /**
     * Instantiates a new Reg ex.
     *
     * @param regEx     the reg ex
     * @param details   the details
     * @param dontMatch the dont match
     */
    public RegEx(@Language("RegExp") String regEx, String details, String... dontMatch) {
        this(regEx, details, true, dontMatch);
    }

    /**
     * Instantiates a new Reg ex.
     *
     * @param regEx        the reg ex
     * @param details      the details
     * @param useDontMatch the use dont match
     * @param dontMatch    the dont match
     */
    public RegEx(@Language("RegExp") String regEx, String details, boolean useDontMatch, String... dontMatch) {
        this.regEx = regEx;
        this.dontMatch = dontMatch;
        this.useDontMatch = useDontMatch;
        this.details = details;
    }

    /**
     * Is saved date boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isSavedDate(String str) {
        return Pattern.compile("date", Pattern.CASE_INSENSITIVE).matcher(str).find();
    }

    /**
     * Can be empty reg ex.
     *
     * @param bool         the bool
     * @param emptyDetails the empty details
     * @return the reg ex
     */
    public RegEx canBeEmpty(boolean bool, String emptyDetails) {
        if (bool)
            return new RegEx("(^$)|(%s)".formatted(regEx), details + " " + emptyDetails, dontMatch);
        return this;
    }

    /**
     * Get reg ex.
     *
     * @param useDontMatch the use dont match
     * @return the reg ex
     */
    public RegEx get(boolean useDontMatch) {
        return new RegEx(regEx, details, dontMatch) {{
            setUseDontMatch(useDontMatch);
        }};
    }

    /**
     * Sets use dont match.
     *
     * @param useDontMatch the use dont match
     */
    public void setUseDontMatch(boolean useDontMatch) {
        this.useDontMatch = useDontMatch;
    }

    /**
     * Gets details.
     *
     * @return the details
     */
    public String getDetails() {
        if (!useDontMatch) {
            return details;
        }
        return details + (dontMatch.length == 0 ? "" : "\ncannot contain " + Arrays.toString(dontMatch));
    }


    /**
     * Check boolean.
     *
     * @param str the str
     * @return the boolean
     */
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

    /**
     * Gets regex.
     *
     * @return the regex
     */
    public String getRegex() {
        return regEx;
    }

    /**
     * Prefixes.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class Prefixes {
        /**
         * The constant GUEST_PREFIX.
         */
        public static final String GUEST_PREFIX = "GUEST";
        /**
         * The constant DONT_CAP_FULL.
         */
//        public static final String DONT_CAP_FULL = "!~~!";
        public static final String DONT_CAP_FULL = "\u200B";
        /**
         * The constant DONT_CAP_WORD.
         */
//        public static final String DONT_CAP_WORD = "!~!";
        public static final String DONT_CAP_WORD = "\u2060";

        /**
         * The constant DATE_TIME.
         */
        public static final String DATE_TIME = ":dt:";
    }

}
