package ver14.view.Dialog.Dialogs.LoginProcess.Components.Register;

import ver14.SharedClasses.Login.LoginInfo;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.UI.LinkLabel;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver14.view.Dialog.WinPnl;
import ver14.view.FetchVerify;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Register username field. a field that after verifying the regex requirements for usernames, checks if the username is available.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class RegisterUsernamePnl extends UsernamePnl {
    /**
     * The constant minLoadTime.
     */
    protected static final int minLoadTime = 500;
    /**
     * The Availability map.
     */
    private final Map<String, Boolean> availabilityMap = new HashMap<>();
    /**
     * The Suggestions map.
     */
    private final Map<String, ArrayList<String>> suggestionsMap = new HashMap<>();
    /**
     * The Fetch verify pnl.
     */
    private final FetchVerify fetchVerifyPnl;
    /**
     * The Suggestions pnl.
     */
    private final WinPnl suggestionsPnl;
    /**
     * The Executor.
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    /**
     * The Last check time.
     */
    private ZonedDateTime lastCheckTime = null;
    /**
     * The Is loading.
     */
    private volatile boolean isLoading = false;
    /**
     * The Last checked str.
     */
    private String lastCheckedStr = null;


    /**
     * Instantiates a new Register username pnl.
     *
     * @param loginInfo the login info
     * @param parent    the parent
     */
    public RegisterUsernamePnl(LoginInfo loginInfo, Parent parent) {
        super(true, loginInfo, parent);
        fetchVerifyPnl = new FetchVerify();
        addSecondaryComp(fetchVerifyPnl);
        this.suggestionsPnl = new WinPnl();
        bottomPnl.add(suggestionsPnl);

        parent.addOnClose(executor::shutdown);
    }


    @Override
    public boolean verifyField() {
        if (!super.verifyField()) {
            fetchVerifyPnl.nothing();
            return false;
        }
        return checkAvailable();
    }

    /**
     * Check if the current username is available on the server.
     *
     * @return true if the username is available, false otherwise.
     */
    private boolean checkAvailable() {
        String username = getValue();

        if (availabilityMap.containsKey(username)) {
            boolean res = availabilityMap.get(username);
            if (!res && suggestionsMap.containsKey(username)) {
                setSuggestions(suggestionsMap.get(username));
            }
            fetchVerifyPnl.verify(res);
            isLoading = false;
            return res;
        }

        if (isLoading)
            return false;

        isLoading = true;
        fetchVerifyPnl.load();
        onUpdate();

        if (lastCheckTime == null || !Objects.equals(username, lastCheckedStr))
            lastCheckTime = ZonedDateTime.now();
        lastCheckedStr = username;

        fetch(username);

        return false;
    }

    /**
     * Sets username suggestions.
     *
     * @param suggestions the suggestions
     */
    private void setSuggestions(ArrayList<String> suggestions) {
        removeSuggestions();
        Header header;
        int cols = 1;
        if (!suggestions.isEmpty()) {
            header = new Header("suggestions:", false);
            for (int i = 3; i < suggestions.size(); i++) {
                if (suggestions.size() % i == 0) {
                    cols = i;
                    break;
                }
            }
        } else {
            header = new Header("no suggestions");
        }
        suggestionsPnl.setCols(cols);

        header.setFont(FontManager.small);
        suggestionsPnl.setHeader(header);

        suggestions.forEach(suggestion -> {
            availabilityMap.put(suggestion, true);
            suggestionsPnl.add(new LinkLabel(suggestion, () -> {
                removeSuggestions();
                textField.setText(suggestion);
            }));
        });
    }

    /**
     * Fetch result from the server.
     *
     * @param un the un
     */
    private void fetch(String un) {
        executor.submit(() -> {
            lastCheckTime = ZonedDateTime.now();
            parent.askServer(Message.checkUsernameAvailability(un), res -> serverResponded(res, un));
        });
    }

    /**
     * Remove suggestions.
     */
    private void removeSuggestions() {
        suggestionsPnl.removeContent();
        suggestionsPnl.setHeader(null);
    }

    /**
     * Server responded.
     *
     * @param response the response
     * @param username the username
     */
    private void serverResponded(Message response, String username) {
        long ms = minLoadTime - lastCheckTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        isLoading = false;
        boolean result = response.getAvailable();
        availabilityMap.put(username, result);
        fetchVerifyPnl.verify(result);
        if (!result) {
            ArrayList<String> suggestions = response.getUsernameSuggestions();
            suggestionsMap.put(username, suggestions);
            setSuggestions(suggestions);
        } else {
            removeSuggestions();
        }
        onUpdate();
    }

    @Override
    public String errorDetails() {
        if (!super.verifyRegEx()) {
            fetchVerifyPnl.nothing();
            return super.errorDetails();
        }
        return isLoading ? "checking if username is available" : "username is not available";
    }

}
