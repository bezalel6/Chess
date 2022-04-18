package ver14;

import org.intellij.lang.annotations.Language;
import ver14.DB.DB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class UsernameSuggestions {

    //tooptimize O(no)
    private static final int maxSuggestions = 10;
    private static final int numOfIterationsPerOptionGroup = 10;
    private static final MatchIterations preUnderscore = ((result, iteration) -> "_" + result.group());
    //    todo add rand nums
    private static final MatchIterations postUnderscore = ((result, iteration) -> result.group() + "_");
    private static final MatchIterations bothUnderscore = ((result, iteration) -> "_" + result.group() + "_");
    private static final MatchIterations upper = (matchGroup, i) -> matchGroup.group().toUpperCase();
    private static final MatchIterations lower = (matchGroup, i) -> matchGroup.group().toLowerCase();


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

//        options.addAll(createOptions("(.*$)",username,))

        return createResults(options);
    }

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

    private interface MatchIterations {
        /**
         * @param iteration should start at 1
         * @return
         */
        String createStr(MatchResult result, int iteration);
    }
}
