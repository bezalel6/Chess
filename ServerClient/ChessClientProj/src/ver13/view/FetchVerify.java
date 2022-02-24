package ver13.view;

import ver13.SharedClasses.FontManager;
import ver13.SharedClasses.ui.MyLbl;
import ver13.view.IconManager.IconManager;

import javax.swing.*;

public class FetchVerify extends MyLbl {
    private final ImageIcon verifiedIcon, notVerifiedIcon, loadingIcon;

    public FetchVerify() {
        this(IconManager.greenCheck, IconManager.redX, IconManager.loadingIcon);
    }

    public FetchVerify(ImageIcon verifiedIcon, ImageIcon notVerifiedIcon, ImageIcon loadingIcon) {
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
