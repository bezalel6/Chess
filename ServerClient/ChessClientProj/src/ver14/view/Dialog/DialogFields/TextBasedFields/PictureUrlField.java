package ver14.view.Dialog.DialogFields.TextBasedFields;

import ver14.SharedClasses.Utils.RegEx;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.FetchVerify;
import ver14.view.IconManager.IconManager;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * a Picture url field.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class PictureUrlField extends TextField {
    /**
     * The Verify.
     */
    private final FetchVerify verify;

    /**
     * The Display lbl.
     */
    private final JLabel displayLbl;


    /**
     * The Executor service.
     */
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * The Map.
     */
    private final Map<String, ImageIcon> map = new HashMap<>();

    /**
     * Instantiates a new Picture url field.
     *
     * @param dialogLabel the dialog label
     * @param parent      the parent
     */
    public PictureUrlField(Header dialogLabel, Parent parent) {
        super(dialogLabel, parent, RegEx.URL);
        displayLbl = new JLabel();
        verify = new FetchVerify();

        addMainComp(displayLbl);
        addSecondaryComp(verify);
    }

    @Override
    protected boolean verifyField() {
        verify.load();
        if (map.containsKey(getValue())) {
            return verify(map.get(getValue()));
        }
        boolean regVer = super.verifyRegEx();
        String currentVal = getValue();
        if (regVer)
            executorService.submit(() -> {
                ImageIcon icon = IconManager.loadOnline(currentVal, IconManager.PROFILE_PIC_SIZE.mult(4));
                map.put(currentVal, icon);
                onUpdate();
            });
        else verify.nothing();
        return false;
    }

    /**
     * Verify boolean.
     *
     * @param icon the icon
     * @return the boolean
     */
    protected boolean verify(ImageIcon icon) {
        verify.verify(icon != null);
        displayLbl.setIcon(icon);
        return icon != null;
    }
}
