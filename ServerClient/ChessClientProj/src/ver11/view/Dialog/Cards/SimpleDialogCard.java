package ver11.view.Dialog.Cards;

import ver11.view.Dialog.Components.DialogComponent;
import ver11.view.Dialog.Dialog;
import ver11.view.Dialog.Dialogs.BackOkInterface;

public class SimpleDialogCard extends DialogCard {
    private final BackOkInterface backOk;

    public SimpleDialogCard(CardHeader cardHeader, Dialog parentDialog, BackOkInterface backOk) {
        super(cardHeader, parentDialog, backOk);
        this.backOk = backOk;
    }


    public static SimpleDialogCard create(DialogComponent component, Dialog parent) {
        BackOkInterface backOk = null;
        if (parent instanceof BackOkInterface b) {
            backOk = b;
        } else if (component instanceof BackOkInterface b) {
            backOk = b;
        }
        CardHeader header = new CardHeader(component.getHeader().getText());
        return create(header, parent, backOk, component);
    }

    public static SimpleDialogCard create(CardHeader header, Dialog parent, BackOkInterface backOk, DialogComponent... components) {

        return new SimpleDialogCard(header, parent, backOk) {
            {
                for (DialogComponent comp : components) {
                    comp.setParent(this);
                    addDialogComponent(comp);
                }
            }
        };
    }
}