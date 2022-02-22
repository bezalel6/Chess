package ver5.view.dialogs.LoginProcess.Components;

import ver5.SharedClasses.FontManager;
import ver5.SharedClasses.LoginInfo;
import ver5.SharedClasses.messages.Message;
import ver5.SharedClasses.ui.MyLbl;
import ver5.view.IconManager;
import ver5.view.dialogs.MyDialog;

import javax.swing.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterUsernamePnl extends UsernamePnl {
    protected static final int availabilityCoolDown = 500;
    private final Map<String, Boolean> availabilityMap = new HashMap<>();
    private final Verified verifiedPnl;
    private ZonedDateTime lastCheckTime = null;
    private volatile boolean isLoading = false;
    private String lastCheckedStr = null;

    public RegisterUsernamePnl(LoginInfo loginInfo, MyDialog parent) {
        super(loginInfo, true, parent);
        verifiedPnl = new Verified();
        addSecondaryComp(verifiedPnl);
        doneCreating();
    }

    @Override
    public boolean verify() {
        if (!super.verify()) {
            verifiedPnl.nothing();
            return false;
        }
        return checkAvailable();
    }

    @Override
    public String getError() {
        if (!super.verifyRegEx()) {
            verifiedPnl.nothing();
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
            verifiedPnl.verify(res);
            isLoading = false;
            return res;
        }
        verifiedPnl.load();
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
            verifiedPnl.verify(result);
            parent.repackWin();
        });
        return false;
    }

    static class Verified extends MyLbl {
        private final ImageIcon verifiedIcon, notVerifiedIcon, loadingIcon;

        public Verified() {
            this(IconManager.greenCheck, IconManager.redX, IconManager.loadingIcon);
        }

        public Verified(ImageIcon verifiedIcon, ImageIcon notVerifiedIcon, ImageIcon loadingIcon) {
            this.verifiedIcon = verifiedIcon;
            this.notVerifiedIcon = notVerifiedIcon;
            this.loadingIcon = loadingIcon;
            setFont(FontManager.loginProcess);
            nothing();
        }

        public void nothing() {
            setIconWOtxt(null);
            setText("---");
        }

        public void setIconWOtxt(Icon icon) {
            super.setIcon(icon);

        }

        public void verify(boolean verified) {
            setIcon(verified ? verifiedIcon : notVerifiedIcon);
        }

        @Override
        public void setIcon(Icon icon) {
            setIconWOtxt(icon);
            setText("");
        }

        public void load() {
            setIcon(loadingIcon);
        }
    }
}
