package ver8.view.Dialog.DialogFields;

import ver8.SharedClasses.StrUtils;
import ver8.view.Dialog.Components.DialogComponent;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Verified;
import ver8.view.ErrorPnl;
import ver8.view.OldDialogs.Header;

import java.awt.*;

public abstract class DialogField extends DialogComponent implements Verified {
    private static final int maxCols = 2;
    private final ErrorPnl errLbl;

    protected DialogField(String dialogLabel, Dialog parent) {
        this(maxCols, new Header(dialogLabel, false), parent);
    }

    protected DialogField(int cols, Header header, Dialog parent) {
        super(cols, header, parent);
        errLbl = new ErrorPnl();
        bottomPnl.add(errLbl);
    }

    @Override
    public boolean verify() {
        boolean verified = verifyField();
        if (!verified)
            errLbl.setText(errorDetails());
        else
            errLbl.clear();
        return verified;
    }

    protected abstract boolean verifyField();


    public void repackWin() {
        parent.repackWin();
    }

    public void err(String err) {
        err = StrUtils.format(err);
        errLbl.setText(err);
//        parent.repackWin();
    }

    protected void addMainComp(Component comp) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        add(comp, true, gbc);
    }

    protected void addSecondaryComp(Component comp) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        add(comp, true, gbc);
    }
}
