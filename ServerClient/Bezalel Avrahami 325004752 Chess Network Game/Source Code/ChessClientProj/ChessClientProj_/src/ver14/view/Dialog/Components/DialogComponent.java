package ver14.view.Dialog.Components;

import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.WinPnl;

/**
 * Dialog component - represents a dialog component.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class DialogComponent extends WinPnl implements Child {
    /**
     * The Parent.
     */
    protected Parent parent;

    /**
     * Instantiates a new Dialog component.
     *
     * @param header the header
     * @param parent the parent
     */
    public DialogComponent(Header header, Parent parent) {
        super(header);
        this.parent = parent;
    }

    /**
     * Instantiates a new Dialog component.
     *
     * @param cols   the cols
     * @param header the header
     * @param parent the parent
     */
    public DialogComponent(int cols, Header header, Parent parent) {
        super(cols, header);
        this.parent = parent;
    }

    /**
     * Sets parent.
     *
     * @param parent the parent
     */
    public void setParent(Parent parent) {
        this.parent = parent;
        onUpdate();
    }

    /**
     * On update.
     */
    protected void onUpdate() {
        if (parent != null)
            parent.onUpdate();
    }


    @Override
    public Parent parent() {
        return parent;
    }


}
