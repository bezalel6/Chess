package ver8.view.Dialog.Dialogs.LoginProcess.Components.Register;

import ver8.SharedClasses.LoginInfo;
import ver8.SharedClasses.messages.Message;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Dialogs.LoginProcess.Components.Login.UsernamePnl;
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

    public RegisterUsernamePnl(LoginInfo loginInfo, Dialog parent) {
        super(true, loginInfo, parent);
        fetchVerifyPnl = new FetchVerify();
        addSecondaryComp(fetchVerifyPnl);
    }

    @Override
    public boolean verify() {
        if (!super.verify()) {
            fetchVerifyPnl.nothing();
            return false;
        }
        return checkAvailable();
    }

    private boolean checkAvailable() {
        String username = getValue();

        if (availabilityMap.containsKey(username)) {
            boolean res = availabilityMap.get(username);
            fetchVerifyPnl.verify(res);
            isLoading = false;
            return res;
        }

        if (isLoading)
            return false;

        fetchVerifyPnl.load();

        repackWin();
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
                    boolean result = msg.getAvailable();
                    availabilityMap.put(username, result);
                    fetchVerifyPnl.verify(result);
//            parent.repackWin();
//            repackWin();
                    System.out.println("got username availability callback");
                    onInputUpdate();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return false;
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
