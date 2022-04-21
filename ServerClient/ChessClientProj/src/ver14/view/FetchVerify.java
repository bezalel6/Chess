package ver14.view;

import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.UI.MyLbl;
import ver14.view.IconManager.IconManager;

import javax.swing.*;
import java.time.ZonedDateTime;

public class FetchVerify extends MyLbl {
    private final ImageIcon verifiedIcon, notVerifiedIcon, loadingIcon;

    private ZonedDateTime startedLoading;

    public FetchVerify() {
        this(IconManager.greenCheck, IconManager.redX, IconManager.loadingIcon);
    }

    public FetchVerify(ImageIcon verifiedIcon, ImageIcon notVerifiedIcon, ImageIcon loadingIcon) {
        this.verifiedIcon = verifiedIcon;
        this.notVerifiedIcon = notVerifiedIcon;
        this.loadingIcon = loadingIcon;
        setFont(FontManager.Dialogs.fetchVerifyLbl);
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
        startedLoading = ZonedDateTime.now();
        setIcon(loadingIcon);
    }
}
