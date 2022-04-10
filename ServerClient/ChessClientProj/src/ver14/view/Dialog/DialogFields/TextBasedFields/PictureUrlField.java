package ver14.view.Dialog.DialogFields.TextBasedFields;

import ver14.SharedClasses.RegEx;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.FetchVerify;
import ver14.view.IconManager.IconManager;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PictureUrlField extends TextField {
    private final FetchVerify verify;

    private final JLabel displayLbl;


    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Map<String, ImageIcon> map = new HashMap<>();

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

    protected boolean verify(ImageIcon icon) {
        verify.verify(icon != null);
        displayLbl.setIcon(icon);
        return icon != null;
    }
}
