package ver14.view.Dialog.Cards;

import ver14.view.Dialog.BackOk.BackOkInterface;
import ver14.view.Dialog.Components.DialogComponent;
import ver14.view.Dialog.Dialog;

/**
 * Simple dialog card - used for simple creation of a dialog card.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class SimpleDialogCard extends DialogCard {
    /**
     * The Back ok.
     */
    private final BackOkInterface backOk;

    /**
     * Instantiates a new Simple dialog card.
     *
     * @param cardHeader   the card header
     * @param parentDialog the parent dialog
     * @param backOk       the back ok
     */
    public SimpleDialogCard(CardHeader cardHeader, Dialog parentDialog, BackOkInterface backOk) {
        super(cardHeader, parentDialog, backOk);
        this.backOk = backOk;
    }

    /**
     * Create simple dialog card.
     *
     * @param component the component
     * @param parent    the parent
     * @return the simple dialog card
     */
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

    /**
     * Create simple dialog card.
     *
     * @param header     the header
     * @param parent     the parent
     * @param backOk     the back ok
     * @param components the components
     * @return the simple dialog card
     */
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

    @Override
    public void onBack() {
        super.onBack();
        if (backOk != null) {
            backOk.onBack();

        }
    }

    @Override
    public void onOk() {
        super.onOk();
        if (backOk != null) {
            backOk.onOk();
        }
    }
}
