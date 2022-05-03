package ver14.view;

import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.UI.MyLbl;
import ver14.view.IconManager.IconManager;

import javax.swing.*;
import java.time.ZonedDateTime;

/**
 * Fetch verify - represents a label with defined states: loading, nothing, verified and not verified.
 * for each of the above states, the label has a predefined graphic to show with it. used to show the status of an online fetch.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class FetchVerify extends MyLbl {
    /**
     * The Verified icon.
     */
    private final ImageIcon verifiedIcon, /**
     * The Not verified icon.
     */
    notVerifiedIcon, /**
     * The Loading icon.
     */
    loadingIcon;

    /**
     * The Started loading.
     */
    private ZonedDateTime startedLoading;

    /**
     * Instantiates a new Fetch verify.
     */
    public FetchVerify() {
        this(IconManager.greenCheck, IconManager.redX, IconManager.loadingIcon);
    }

    /**
     * Instantiates a new Fetch verify.
     *
     * @param verifiedIcon    the verified icon
     * @param notVerifiedIcon the not verified icon
     * @param loadingIcon     the loading icon
     */
    public FetchVerify(ImageIcon verifiedIcon, ImageIcon notVerifiedIcon, ImageIcon loadingIcon) {
        this.verifiedIcon = verifiedIcon;
        this.notVerifiedIcon = notVerifiedIcon;
        this.loadingIcon = loadingIcon;
        setFont(FontManager.Dialogs.fetchVerifyLbl);
        nothing();
    }

    /**
     * Nothing.
     */
    public void nothing() {
        setIconWOtxt(null);
        setText("---");
    }

    /**
     * Sets icon w otxt.
     *
     * @param icon the icon
     */
    public void setIconWOtxt(Icon icon) {
        super.setIcon(icon);

    }

    /**
     * Verify.
     *
     * @param verified the verified
     */
    public void verify(boolean verified) {

        setIcon(verified ? verifiedIcon : notVerifiedIcon);
    }


    @Override
    public void setIcon(Icon icon) {
        setIconWOtxt(icon);
        setText("");
    }

    /**
     * Load.
     */
    public void load() {
        startedLoading = ZonedDateTime.now();
        setIcon(loadingIcon);
    }
}
