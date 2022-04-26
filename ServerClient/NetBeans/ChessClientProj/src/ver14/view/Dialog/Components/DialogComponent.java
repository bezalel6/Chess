package ver14.view.Dialog.Components;

import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.WinPnl;

public abstract class DialogComponent extends WinPnl implements Child {
    protected Parent parent;

    public DialogComponent(Header header, Parent parent) {
        super(header);
        this.parent = parent;
    }

    public DialogComponent(int cols, Header header, Parent parent) {
        super(cols, header);
        this.parent = parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
        onUpdate();
    }

    protected void onUpdate() {
        if (parent != null)
            parent.onUpdate();
    }


    @Override
    public Parent parent() {
        return parent;
    }


}
