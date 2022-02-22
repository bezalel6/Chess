package ver3.view.dialogs_classes;


import ver3.view.dialogs_classes.dialog_objects.DialogObject;

public class DialogMessage extends Dialogs {

    private DialogObject dialogObject;

    public DialogMessage(String title, DialogObject messageDialog) {
        super(title);
        this.dialogObject = messageDialog;
    }

    @Override
    public Object run() {
        bodyPnl.add(dialogObject.getComponent());
        return runDialog();
    }

    @Override
    void setBottomPnl() {
        bottomPnl.add(createOkButton());
    }
}
