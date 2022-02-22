package ver8.view.Dialog.Cards;

import ver8.SharedClasses.Callbacks.VoidCallback;
import ver8.view.Dialog.Components.DialogComponent;
import ver8.view.Dialog.Dialog;
import ver8.view.OldDialogs.Navigation.BackOkInterface;

public class SimpleDialogCard extends DialogCard {
    public SimpleDialogCard(CardHeader cardHeader, Dialog parentDialog, boolean addBackOk) {
        super(cardHeader, parentDialog, addBackOk);

    }

    public static SimpleDialogCard create(DialogComponent component, Dialog parent) {
        VoidCallback onOk = null;
        if (component instanceof BackOkInterface backOk) {
            onOk = backOk::onOk;
        }
        CardHeader header = new CardHeader(component.header.getTxt());
        return create(header, parent, onOk, component);
    }

    public static SimpleDialogCard create(CardHeader header, Dialog parent, VoidCallback onOk, DialogComponent... components) {
        return new SimpleDialogCard(header, parent, onOk != null) {
            {
                for (DialogComponent comp : components) {
                    addDialogComponent(comp);
                }
            }

            @Override
            public void onOk() {
                super.onOk();
                onOk.callback();
            }
        };
    }
}
