package ver10.view.Dialog.DialogFields;

import ver10.SharedClasses.DBActions.Arg.Arg;
import ver10.SharedClasses.DBActions.Arg.Config;
import ver10.SharedClasses.RegEx;
import ver10.SharedClasses.Utils.StrUtils;
import ver10.SharedClasses.ui.MyJButton;
import ver10.view.Dialog.Cards.DialogCard;
import ver10.view.Dialog.Cards.SimpleDialogCard;
import ver10.view.Dialog.Components.DialogComponent;
import ver10.view.Dialog.Components.Parent;
import ver10.view.Dialog.Dialog;
import ver10.view.Dialog.DialogFields.TextBasedFields.NumberField;
import ver10.view.Dialog.DialogFields.TextBasedFields.TextField;
import ver10.view.Dialog.Dialogs.Header;
import ver10.view.Dialog.Verified;
import ver10.view.ErrorPnl;

import java.awt.*;

public abstract class DialogField<V> extends DialogComponent implements Verified {
    private static final int cols = 2;
    protected final ErrorPnl errLbl;
    protected Config<V> config;
    private boolean noRes = false;

    protected DialogField(Header fieldHeader, Parent parent) {
        this(cols, fieldHeader, parent);
    }

    protected DialogField(int cols, Header header, Parent parent) {
        super(cols, header, parent);
        errLbl = new ErrorPnl();
        bottomPnl.add(errLbl);

    }

    public static DialogField<?> createField(Arg arg, Parent fieldParent) {
        if (!arg.argType.isUserInput)
            return null;

        Header fieldHeader = new Header(arg.config.description, false);
        DialogField<?> field = switch (arg.argType) {

            case Date -> new DateField(fieldHeader, fieldParent);
            case Text -> new TextField(fieldHeader, fieldParent, RegEx.Any);
            case Number -> new NumberField(fieldHeader, fieldParent);

            default -> throw new IllegalStateException("Unexpected value: " + arg.argType);
        };
        field.setConfig(arg.config);
        return field;
    }

    public final void setConfig(Config<V> config) {
        this.config = config;
        if (config != null && config.canUseDefault) {
            setValue(config.getDefault());
            addInNewLine(new MyJButton(config.defaultValueDesc, getFont(), () -> setValue(config.getDefault())));
        }
    }

    protected void addSecondaryComp(Component comp) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        add(comp, gbc);
    }

    protected void addMainComp(Component comp) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        add(comp, gbc);
    }

    @Override
    public boolean verify() {
        boolean verified = verifyField();
        if (!verified)
            errLbl.setText(errorDetails());
        else
            errLbl.clear();
        noRes = !verified;
        return verified;
    }

    protected abstract boolean verifyField();

    public V getResult() {
        if (noRes)
            return null;
        return getValue();
    }

    protected abstract V getValue();

    protected abstract void setValue(V value);

    public void err(String err) {
        err = StrUtils.format(err);
        errLbl.setText(err);
//        parent.repackWin();
    }

    public DialogCard createCard() {
        assert parent instanceof Dialog;
        return SimpleDialogCard.create(this, (Dialog) parent);
    }
}
