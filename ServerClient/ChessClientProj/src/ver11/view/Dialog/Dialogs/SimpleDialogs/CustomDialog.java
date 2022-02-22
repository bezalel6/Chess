package ver11.view.Dialog.Dialogs.SimpleDialogs;

import ver11.SharedClasses.DBActions.Arg.Arg;
import ver11.view.Dialog.BackOk.CancelOk;
import ver11.view.Dialog.Cards.CardHeader;
import ver11.view.Dialog.Cards.SimpleDialogCard;
import ver11.view.Dialog.Dialog;
import ver11.view.Dialog.DialogFields.DialogField;
import ver11.view.Dialog.DialogProperties;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomDialog extends Dialog implements CancelOk {
    private final int resultsArrSize;
    private Map<DialogField<?>, Integer> map = new HashMap<>();
    private boolean noRes = false;

    /**
     * @param parentWin
     * @param properties - a general description of the dialog
     * @param args       - specific
     */
    public CustomDialog(Component parentWin, DialogProperties properties, Arg... args) {
        super(null, properties, parentWin);
        this.resultsArrSize = args.length;
        ArrayList<DialogField<?>> fields = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            Arg arg = args[i];
            DialogField<?> field = DialogField.createField(arg, this);
            if (field != null) {
//                if(arg)
                fields.add(field);
                map.put(field, i);
            }
        }
        if (!fields.isEmpty())
            setup(fields);
        else closeDialog();
    }


    protected void setup(ArrayList<DialogField<?>> components) {
        setup(components.toArray(DialogField[]::new));
    }

    protected void setup(DialogField<?>... components) {
        cardsSetup(null, SimpleDialogCard.create(new CardHeader(properties), this, this, components));
    }


    public Object[] getResults() {
        if (noRes)
            return null;
        Object[] results = new Object[resultsArrSize];
        map.forEach((key, value) -> results[value] = key.getResult());
        return results;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

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
