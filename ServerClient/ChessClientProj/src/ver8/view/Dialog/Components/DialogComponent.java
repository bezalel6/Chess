package ver8.view.Dialog.Components;

import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.WinPnl;
import ver8.view.OldDialogs.Header;

public abstract class DialogComponent extends WinPnl {
    protected final Dialog parent;

    public DialogComponent(Header header, Dialog parent) {
        super(header);
        this.parent = parent;
    }

    public DialogComponent(int cols, Header header, Dialog parent) {
        super(cols, header);
        this.parent = parent;
    }


    protected void onInputUpdate() {
        parent.onInputUpdate();
    }


}
