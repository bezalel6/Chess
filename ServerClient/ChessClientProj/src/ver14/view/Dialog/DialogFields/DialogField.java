package ver14.view.Dialog.DialogFields;

import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.SharedClasses.DBActions.Arg.Config;
import ver14.SharedClasses.DBActions.Arg.Described;
import ver14.SharedClasses.UI.Buttons.ValueBtn;
import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.Utils.RegEx;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.CanError;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Cards.SimpleDialogCard;
import ver14.view.Dialog.Components.DialogComponent;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Components.Verified;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.DialogFields.TextBasedFields.NumberField;
import ver14.view.Dialog.DialogFields.TextBasedFields.PictureUrlField;
import ver14.view.Dialog.DialogFields.TextBasedFields.TextField;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.WinPnl;
import ver14.view.ErrorPnl;

import java.awt.*;
import java.util.ArrayList;

/**
 * Dialog field - represents a dialog field for the client to fill.
 *
 * @param <T> the type of info this field will hold
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class DialogField<T> extends DialogComponent implements Verified {
    /**
     * The constant cols.
     */
    private static final int cols = 2;
    /**
     * The Err lbl.
     */
    protected final ErrorPnl errLbl;
    /**
     * The Not equals to.
     */
    private final ArrayList<CanError<T>> notEqualsTo;
    /**
     * The Config.
     */
    protected Config<T> config;
    /**
     * The On default click ok.
     */
    private boolean onDefaultClickOk = true;
    /**
     * The No res.
     */
    private boolean noRes = false;

    /**
     * Instantiates a new Dialog field.
     *
     * @param fieldHeader the field header
     * @param parent      the parent
     */
    protected DialogField(Header fieldHeader, Parent parent) {
        this(cols, fieldHeader, parent);
    }

    /**
     * Instantiates a new Dialog field.
     *
     * @param cols   the cols
     * @param header the header
     * @param parent the parent
     */
    protected DialogField(int cols, Header header, Parent parent) {
        super(cols, header, parent);
        this.notEqualsTo = new ArrayList<>();
        errLbl = new ErrorPnl();
        bottomPnl.add(errLbl);
        setFont(FontManager.Dialogs.dialogInput);

    }

    /**
     * Create field according to an argument. meant to create the required information fields when creating database requests.
     *
     * @param arg         the arg
     * @param fieldParent the field parent
     * @return the dialog field
     */
    public static DialogField<?> createField(Arg arg, Parent fieldParent) {
        if (!arg.isUserInput())
            return null;

        Header fieldHeader = new Header(arg.config.description, false);
        DialogField field = switch (arg.argType) {

            case Date -> new DateField(fieldHeader, fieldParent);
            case Text -> new TextField(fieldHeader, fieldParent, RegEx.Any);
            case Url -> new TextField(fieldHeader, fieldParent, RegEx.URL);
            case PictureUrl -> new PictureUrlField(fieldHeader, fieldParent);
            case ServerAddress -> new TextField(fieldHeader, fieldParent, RegEx.IPPortAddress);
            case Number -> new NumberField(fieldHeader, fieldParent);

            default -> throw new IllegalStateException("Unexpected value: " + arg.argType);
        };
        field.setConfig(arg.config);
//        field.setOnDefaultClickOk(false);
        return field;
    }

    /**
     * Sets the configuration of this field.
     *
     * @param config the config
     */
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

    /**
     * Create value button.
     * used for creating default buttons for fields that allow default values.
     *
     * @param desc the desc
     * @return the value btn
     */
    private ValueBtn<T> createValBtn(Described<T> desc) {
        return new ValueBtn<>(desc.description(), FontManager.Dialogs.dialog, desc.obj(), this::valueBtnPresses);
    }

    /**
     * Value btn presses.
     *
     * @param val the value
     */
    public void valueBtnPresses(T val) {
        setValue(val);
        if (onDefaultClickOk) {
            parent.tryOk(false);
        }
    }

    /**
     * Sets weather or not clicking a default value button should click the ok button.
     *
     * @param onDefaultClickOk the on default click ok
     */
    public void setOnDefaultClickOk(boolean onDefaultClickOk) {
        this.onDefaultClickOk = onDefaultClickOk;
    }

    /**
     * Add a secondary component to this field. the component will be added to the side of the main component.
     *
     * @param comp the secondary component
     */
    protected void addSecondaryComp(Component comp) {
        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.fill = GridBagConstraints.BOTH;
        add(comp, gbc);
    }

    /**
     * Add a main component.
     *
     * @param comp the comp
     */
    protected void addMainComp(Component comp) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        nextLine();
        add(comp, gbc);
    }

    /**
     * this value cannot be equals to.
     *
     * @param canError the can error
     */
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

    /**
     * Gets the current value of this field.
     *
     * @return the value
     */
    protected abstract T getValue();

    /**
     * Verify field.
     *
     * @return true if the field has verified successfully, false otherwise.
     */
    protected abstract boolean verifyField();

    /**
     * Sets value.
     *
     * @param value the value
     */
    public abstract void setValue(T value);

    /**
     * Gets the result of this field.
     *
     * @return this field's value if it is verified, null otherwise.
     */
    public T getResult() {
        if (noRes)
            return null;
        return getValue();
    }

    /**
     * show err.
     *
     * @param err the error
     */
    public void err(String err) {
        err = StrUtils.format(err);
        errLbl.setText(err);
//        parent.repackWin();
    }

    /**
     * Create card out of this field.
     *
     * @return the dialog card
     */
    public DialogCard createCard() {
        assert parent instanceof Dialog;
        var ret = SimpleDialogCard.create(this, (Dialog) parent);
        ret.setOverrideableSize();
        return ret;
    }
}
