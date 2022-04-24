package ver14;

import org.intellij.lang.annotations.Language;
import ver14.DB.DB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;


/**
 * Username suggestions utility class that generates username suggestions for players who are trying to register and their username is used already
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class UsernameSuggestions {
//tooptimize O(no)

    /**
     * The constant maxSuggestions.
     */
    private static final int maxSuggestions = 10;
    /**
     * The constant numOfIterationsPerOptionGroup.
     */
    private static final int numOfIterationsPerOptionGroup = 10;
    /**
     * The constant preUnderscore.
     */
    private static final MatchIterations preUnderscore = ((result, iteration) -> "_" + result.group());
    /**
     * The constant postUnderscore.
     */
//    todo add rand nums
    private static final MatchIterations postUnderscore = ((result, iteration) -> result.group() + "_");
    /**
     * The constant bothUnderscore.
     */
    private static final MatchIterations bothUnderscore = ((result, iteration) -> "_" + result.group() + "_");
    /**
     * The constant upper.
     */
    private static final MatchIterations upper = (matchGroup, i) -> matchGroup.group().toUpperCase();
    /**
     * The constant lower.
     */
    private static final MatchIterations lower = (matchGroup, i) -> matchGroup.group().toLowerCase();


    /**
     * Create suggestions array list.
     *
     * @param username the username
     * @return the array list
     */
    public static ArrayList<String> createSuggestions(String username) {
        ArrayList<ArrayList<String>> options = new ArrayList<>();
        options.addAll(createOptions("([0-9]+)|([0-9])", username, (matchGroup, i) -> {
            int num = Integer.parseInt(matchGroup.group()) + i;
            return num + "";
        }, (matchGroup, i) -> {
            int num = Integer.parseInt(matchGroup.group()) - i;
            return num + "";
        }, preUnderscore, postUnderscore, bothUnderscore));
        options.addAll(createOptions("([A-Z])", username, preUnderscore, postUnderscore, bothUnderscore));
        options.addAll(createOptions("(^[a-z])|(([0-9]|_)[a-z])", username, upper, lower));
        options.addAll(createOptions("(.*)^", username, (matchGroup, i) -> matchGroup.group() + Math.abs(new Random().nextInt())));

//        options.addAll(createOptions("(.*$)",username,))

        return createResults(options);
    }

    /**
     * Create options array list.
     *
     * @param regex           the regex
     * @param username        the username
     * @param matchIterations the match iterations
     * @return the array list
     */
    private static ArrayList<ArrayList<String>> createOptions(@Language("RegExp") String regex, String username, MatchIterations... matchIterations) {
        ArrayList<ArrayList<String>> options = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        pattern.matcher(username).results().forEach(match -> {
            ArrayList<String>[] arr = new ArrayList[matchIterations.length];
            Arrays.setAll(arr, i -> new ArrayList<>());

            for (int i = 1; i <= numOfIterationsPerOptionGroup; i++) {
                for (int j = 0; j < matchIterations.length; j++) {
                    MatchIterations iterations = matchIterations[j];
                    String currentIterationStr = iterations.createStr(match, i);
                    String str = username.substring(0, match.start()).concat(currentIterationStr).concat(username.substring(match.end()));
                    arr[j].add(str);
                }
            }
            options.addAll(Arrays.stream(arr).toList());
        });
        return options;
    }

    /**
     * Create results array list.
     *
     * @param optionsLists the options lists
     * @return the array list
     */
    private static ArrayList<String> createResults(ArrayList<ArrayList<String>> optionsLists) {
        ArrayList<String> suggestions = new ArrayList<>();
        int[] indices = new int[optionsLists.size()];
        Arrays.fill(indices, 0);
//        num of fully searched lists
        int numOfSearchedLists = 0;
        outer:
        while (numOfSearchedLists < optionsLists.size()) {
            for (int i = 0; i < optionsLists.size(); i++) {
                ArrayList<String> options = optionsLists.get(i);
                boolean keepSearching = true;
                if (options.isEmpty()) {
                    keepSearching = false;
                    numOfSearchedLists++;
                }
                while (keepSearching && indices[i] < options.size()) {
                    String suggestion = options.get(indices[i]++);
                    if (!suggestions.contains(suggestion) && !DB.isUsernameExists(suggestion)) {
                        suggestions.add(suggestion);
                        if (suggestions.size() >= maxSuggestions) {
                            break outer;
                        }
                        keepSearching = false;
                    }
                    if (indices[i] >= options.size()) {
                        keepSearching = false;
                        numOfSearchedLists++;
                    }
                }
            }
        }
        return suggestions;

    }

    /**
     * Match iterations.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    private interface MatchIterations {
        /**
         * Create str string.
         *
         * @param result    the result
         * @param iteration should start at 1
         * @return string
         */
        String createStr(MatchResult result, int iteration);
    }
}
