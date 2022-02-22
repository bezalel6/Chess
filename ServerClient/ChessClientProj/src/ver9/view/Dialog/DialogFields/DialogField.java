package ver9.view.Dialog.DialogFields;

import ver9.SharedClasses.StrUtils;
import ver9.view.Dialog.Components.DialogComponent;
import ver9.view.Dialog.Components.Parent;
import ver9.view.Dialog.Dialogs.Header;
import ver9.view.Dialog.Verified;
import ver9.view.ErrorPnl;

import java.awt.*;

public abstract class DialogField extends DialogComponent implements Verified {
    private static final int maxCols = 2;
    protected final ErrorPnl errLbl;

    protected DialogField(String dialogLabel, Parent parent) {
        this(maxCols, new Header(dialogLabel, false), parent);
    }

    protected DialogField(int cols, Header header, Parent parent) {
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
