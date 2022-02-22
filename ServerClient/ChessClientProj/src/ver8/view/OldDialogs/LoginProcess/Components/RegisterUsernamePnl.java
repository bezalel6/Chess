package ver8.view.OldDialogs.LoginProcess.Components;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.messages.Message;
import ver8.view.OldDialogs.MyDialog;
import ver8.view.FetchVerify;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterUsernamePnl extends UsernamePnl {
    protected static final int availabilityCoolDown = 500;
    private final Map<String, Boolean> availabilityMap = new HashMap<>();
    private final FetchVerify fetchVerifyPnl;
    private ZonedDateTime lastCheckTime = null;
    private volatile boolean isLoading = false;
    private String lastCheckedStr = null;

    public RegisterUsernamePnl(LoginInfo loginInfo, MyDialog parent) {
        super(loginInfo, true, parent);
        fetchVerifyPnl = new FetchVerify();
        addSecondaryComp(fetchVerifyPnl);
        doneCreating();
    }

    @Override
    public boolean verify() {
        if (!super.verify()) {
            fetchVerifyPnl.nothing();
            return false;
        }
        return checkAvailable();
    }

    @Override
    public String getError() {
        if (!super.verifyRegEx()) {
            fetchVerifyPnl.nothing();
            return super.getError();
        }
        if (isLoading) {
            return "checking if username is available";
        }
        return "username is not available";
    }

    private boolean checkAvailable() {
        String username = getText();

        if (availabilityMap.containsKey(username)) {
            boolean res = availabilityMap.get(username);
            fetchVerifyPnl.verify(res);
            isLoading = false;
            return res;
        }
        fetchVerifyPnl.load();
        parent.repackWin();

        if (lastCheckTime == null || !Objects.equals(username, lastCheckedStr))
            lastCheckTime = ZonedDateTime.now();
        lastCheckedStr = username;

        if (lastCheckTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS) < availabilityCoolDown) {
            isLoading = true;
            return false;
        }

        lastCheckTime = ZonedDateTime.now();
        parent.askServer(Message.checkUsernameAvailability(username), msg -> {
            isLoading = false;
            boolean result = msg.getAvailable();
            availabilityMap.put(username, result);
            fetchVerifyPnl.verify(result);
            parent.repackWin();
        });
        return false;
    }

}
