package ver14.view.Dialog.DialogFields;

import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.Arg.Config;
import ver14.SharedClasses.DBActions.Arg.Described;
import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.RegEx;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.ui.ObjBtn;
import ver14.view.Dialog.CanError;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Cards.SimpleDialogCard;
import ver14.view.Dialog.Components.DialogComponent;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.DialogFields.TextBasedFields.NumberField;
import ver14.view.Dialog.DialogFields.TextBasedFields.TextField;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Verified;
import ver14.view.Dialog.WinPnl;
import ver14.view.ErrorPnl;

import java.awt.*;
import java.util.ArrayList;

public abstract class DialogField<T> extends DialogComponent implements Verified {
    private static final int cols = 2;
    protected final ErrorPnl errLbl;
    private final ArrayList<CanError<T>> notEqualsTo;
    protected Config<T> config;
    private boolean onDefaultClockOk = true;
    private boolean noRes = false;

    protected DialogField(Header fieldHeader, Parent parent) {
        this(cols, fieldHeader, parent);
    }

    protected DialogField(int cols, Header header, Parent parent) {
        super(cols, header, parent);
        this.notEqualsTo = new ArrayList<>();
        errLbl = new ErrorPnl();
        bottomPnl.add(errLbl);
        setFont(FontManager.Dialogs.dialogInput);

    }

    public static DialogField<?> createField(Arg arg, Parent fieldParent) {
        if (!arg.isUserInput())
            return null;

        Header fieldHeader = new Header(arg.config.description, false);
        DialogField<?> field = switch (arg.argType) {

            case Date -> new DateField(fieldHeader, fieldParent);
            case Text -> new TextField(fieldHeader, fieldParent, RegEx.Any);
            case ServerAddress -> new TextField(fieldHeader, fieldParent, RegEx.IPPAddress);
            case Number -> new NumberField(fieldHeader, fieldParent);

            default -> throw new IllegalStateException("Unexpected value: " + arg.argType);
        };
        field.setConfig(arg.config);
        field.setOnDefaultClockOk(false);
        return field;
    }

    public final void setConfig(Config<T> config) {
        this.config = config;
        if (config != null) {
            if (config.canUseDefault) {
                setValue(config.getDefault());
                addInNewLine(createValBtn(config.getDescribedDefault()));
            }
            var suggestions = config.getValuesSuggestion();
            WinPnl pnl = new WinPnl(suggestions.size() % 3, null);
            suggestions.forEach(suggestion -> pnl.add(createValBtn(suggestion)));
            addInNewLine(pnl);
        }

    }

    public void setOnDefaultClockOk(boolean onDefaultClockOk) {
        this.onDefaultClockOk = onDefaultClockOk;
    }

    private ObjBtn<T> createValBtn(Described<T> desc) {
        return new ObjBtn<>(desc.description(), FontManager.Dialogs.dialog, desc.obj(), this::valueBtnPresses);
    }

    public void valueBtnPresses(T val) {
        setValue(val);
        if (onDefaultClockOk) {
            parent.tryOk(false);
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

    public void notEqualsTo(CanError<T> canError) {
        notEqualsTo.add(canError);
    }

    @Override
    public boolean verify() {
        String error = notEqualsTo.stream().filter(canError -> getValue().equals(canError.obj())).findAny().map(CanError::errorDetails).orElse(null);
        boolean verified = error == null;
        if (verified) {
            verified = verifyField();
            error = errorDetails();
        }
        if (!verified)
            errLbl.setText(error);
        else
            errLbl.clear();
        noRes = !verified;
        return verified;
    }

    protected abstract T getValue();

    protected abstract boolean verifyField();

    protected abstract void setValue(T value);

    public T getResult() {
        if (noRes)
            return null;
        return getValue();
    }

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
