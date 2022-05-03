package ver14.view.Dialog.Dialogs.SimpleDialogs;

import ver14.SharedClasses.DBActions.Arg.Arg;
import ver14.view.Dialog.BackOk.CancelOk;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.SimpleDialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.DialogFields.DialogField;
import ver14.view.Dialog.DialogProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * represents a Custom dialog, mostly intended to be used to get required input for db requests. (like getting a date ranges for a games in range request).
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class CustomDialog extends Dialog implements CancelOk {
    /**
     * The Results arr size.
     */
    private final int resultsArrSize;
    /**
     * The Map.
     */
    private Map<DialogField<?>, Integer> map = new HashMap<>();
    /**
     * The No res.
     */
    private boolean noRes = false;


    /**
     * Instantiates a new Custom dialog.
     *
     * @param dialogProperties - a general description of the dialog
     * @param args             - all the fields that need to be added to this dialog
     */
    public CustomDialog(DialogProperties dialogProperties, Arg... args) {
        super(dialogProperties);
        this.resultsArrSize = args.length;
        ArrayList<DialogField<?>> fields = new ArrayList<>();
        int numOfUserInputs = (int) Arrays.stream(args).filter(Arg::isUserInput).count();
        for (int i = 0; i < args.length; i++) {
            Arg arg = args[i];
            DialogField<?> field = DialogField.createField(arg, this);
            if (field != null) {
                field.setOnDefaultClickOk(numOfUserInputs == 1);
                fields.add(field);
                map.put(field, i);
            }
        }
        if (!fields.isEmpty())
            setup(fields);
        else closeDialog();
    }

    /**
     * Sets .
     *
     * @param components the components
     */
    protected void setup(ArrayList<DialogField<?>> components) {
        setup(components.toArray(DialogField[]::new));
    }

    /**
     * Sets .
     *
     * @param components the components
     */
    protected void setup(DialogField<?>... components) {
        var simple = SimpleDialogCard.create(new CardHeader(dialogProperties), this, this, components);
        simple.setOverrideableSize();
        cardsSetup(null, simple);
    }

    /**
     * Get results object [ ].
     *
     * @return the object [ ]
     */
    public Object[] getResults() {
        if (noRes)
            return null;
        Object[] results = new Object[resultsArrSize];
        map.forEach((key, value) -> results[value] = key.getResult());
        return results;
    }

    @Override
    public void onBack() {
        noRes = true;
        closeDialog();
    }

    @Override
    public void onOk() {
        closeDialog();
    }

}
