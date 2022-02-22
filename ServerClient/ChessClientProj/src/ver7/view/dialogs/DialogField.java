package ver7.view.dialogs;

import ver7.SharedClasses.RegEx;
import ver7.SharedClasses.StrUtils;
import ver7.view.ErrorPnl;

import java.awt.*;

public abstract class DialogField extends WinPnl implements VerifiedComponent {
    protected static final Dimension defaultTextFieldSize = new Dimension(250, 20);
    private static final int maxCols = 2;
    public final RegEx verifyRegEx;
    protected final MyDialog parent;
    private final boolean useDontAddRegex;
    private final ErrorPnl errLbl;

    protected DialogField(String dialogLabel, RegEx verifyRegEx, boolean useDontAddRegex, MyDialog parent) {
        super(maxCols, new Header(dialogLabel), false);
        this.useDontAddRegex = useDontAddRegex;
        this.parent = parent;
        this.verifyRegEx = verifyRegEx;
        errLbl = new ErrorPnl();
        addToTopPnl(errLbl, false);
        parent.repackWin();
    }


    @Override
    public Dimension getPreferredSize() {
        if (parent.dialogFieldWidth == 0) {
            return super.getPreferredSize();
        }
        return new Dimension(parent.dialogFieldWidth, super.getPreferredSize().height);
    }

    @Override
    public boolean verify() {
//        return false;
        return verifyRegEx();
    }

    @Override
    public String getError() {
        return verifyRegEx.getDetails(useDontAddRegex);
    }

    protected boolean verifyRegEx() {
        return verifyRegEx.check(getText(), useDontAddRegex);
    }

    public abstract String getText();

    public void err(String err) {
        err = StrUtils.format(err);
        errLbl.setText(err);
        parent.repackWin();
    }

    public abstract int getMainCompWidth();

    protected void doneCreating() {
        doneAdding();
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
