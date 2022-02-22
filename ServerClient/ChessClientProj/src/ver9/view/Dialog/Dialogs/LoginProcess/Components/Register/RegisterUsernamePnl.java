package ver9.view.Dialog.Dialogs.LoginProcess.Components.Register;

import ver9.SharedClasses.LoginInfo;
import ver9.SharedClasses.messages.Message;
import ver9.SharedClasses.ui.LinkLabel;
import ver9.view.Dialog.Components.Parent;
import ver9.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
import ver9.view.FetchVerify;

import javax.swing.*;
import java.awt.*;
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
    private final JPanel suggestionsPnl;
    private ZonedDateTime lastCheckTime = null;
    private volatile boolean isLoading = false;
    private String lastCheckedStr = null;

    public RegisterUsernamePnl(LoginInfo loginInfo, Parent parent) {
        super(true, loginInfo, parent);
        fetchVerifyPnl = new FetchVerify();
        addSecondaryComp(fetchVerifyPnl);
        this.suggestionsPnl = new JPanel(new GridLayout(0, 4));
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
            long s = availabilityCoolDown - lastCheckTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
            try {
                Thread.sleep(Math.max(0, s));

                lastCheckTime = ZonedDateTime.now();
                parent.askServer(Message.checkUsernameAvailability(username), msg -> {
                    isLoading = false;
                    suggestionsPnl.removeAll();
                    boolean result = msg.getAvailable();
                    availabilityMap.put(username, result);
                    fetchVerifyPnl.verify(result);
                    if (!result) {
                        ArrayList<String> suggestions = msg.getUsernameSuggestions();
                        suggestionsMap.put(username, suggestions);
                        setSuggestions(suggestions);
                    }
                    onUpdate();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return false;
    }

    private void setSuggestions(ArrayList<String> suggestions) {
        suggestionsPnl.removeAll();
        suggestions.forEach(suggestion -> {
            availabilityMap.put(suggestion, true);
            suggestionsPnl.add(new LinkLabel(suggestion, () -> {
                suggestionsPnl.removeAll();
                textField.setText(suggestion);
            }));
        });
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
