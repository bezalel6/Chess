package ver14.view.Dialog.Dialogs.LoginProcess.Components.Register;

import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.LoginInfo;
import ver14.SharedClasses.Threads.ThreadsManager;
import ver14.SharedClasses.messages.Message;
import ver14.SharedClasses.ui.LinkLabel;
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

public class RegisterUsernamePnl extends UsernamePnl {
    protected static final int minLoadTime = 500;
    private final FetchingThread fetchingThread;
    private final Map<String, Boolean> availabilityMap = new HashMap<>();
    private final Map<String, ArrayList<String>> suggestionsMap = new HashMap<>();
    private final FetchVerify fetchVerifyPnl;
    private final WinPnl suggestionsPnl;
    private ZonedDateTime lastCheckTime = null;
    private volatile boolean isLoading = false;
    private String lastCheckedStr = null;

    public RegisterUsernamePnl(LoginInfo loginInfo, Parent parent) {
        super(true, loginInfo, parent);
        fetchVerifyPnl = new FetchVerify();
        addSecondaryComp(fetchVerifyPnl);
        this.suggestionsPnl = new WinPnl();
        bottomPnl.add(suggestionsPnl);
        fetchingThread = new FetchingThread();
    }

    @Override
    public boolean verifyField() {
        if (!super.verifyField()) {
            fetchVerifyPnl.nothing();
            return false;
        }
        return checkAvailable();
    }

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

        fetchingThread.startFetch(username);

        return false;
    }

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

    private void removeSuggestions() {
        suggestionsPnl.removeContent();
        suggestionsPnl.setHeader(null);
    }

    @Override
    public String errorDetails() {
        if (!super.verifyRegEx()) {
            fetchVerifyPnl.nothing();
            return super.errorDetails();
        }
        return isLoading ? "checking if username is available" : "username is not available";
    }

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

    public class FetchingThread extends ThreadsManager.HandledThread {
        private String fetching;

        public FetchingThread() {
            setRunnable(() -> {
                while (!isInterrupted()) {
                    synchronized (this) {
                        wait();
                        lastCheckTime = ZonedDateTime.now();
                        parent.askServer(Message.checkUsernameAvailability(fetching), res -> serverResponded(res, fetching));
                    }
                }
            });
            start();
        }

        public synchronized void startFetch(String username) {
            fetching = username;
            notify();
        }

    }

}
