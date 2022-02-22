package ver10.view.Dialog.Dialogs.LoginProcess.Components.Register;

import ver10.SharedClasses.FontManager;
import ver10.SharedClasses.LoginInfo;
import ver10.SharedClasses.messages.Message;
import ver10.SharedClasses.ui.LinkLabel;
import ver10.view.Dialog.Components.Parent;
import ver10.view.Dialog.Dialogs.Header;
import ver10.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver10.view.Dialog.WinPnl;
import ver10.view.FetchVerify;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterUsernamePnl extends UsernamePnl {
    protected static final int availabilityCoolDown = 500;
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

        //todo maybe block current thread
        new Thread(() -> {
            lastCheckTime = ZonedDateTime.now();
            parent.askServer(Message.checkUsernameAvailability(username), response -> {
                long s = availabilityCoolDown - lastCheckTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
                if (s > 0) {
                    try {
                        Thread.sleep(s);
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
            });
        }).start();

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
            header = new Header("no suggestions found");
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
        if (isLoading) {
            return "checking if username is available";
        }
        return "username is not available";
    }

}
