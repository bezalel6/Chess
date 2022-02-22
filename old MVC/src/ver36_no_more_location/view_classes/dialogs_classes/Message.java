package ver36_no_more_location.view_classes.dialogs_classes;

import ver36_no_more_location.view_classes.dialogs_classes.dialog_objects.DialogObject;

public class Message extends Dialogs {

    private DialogObject dialogObject;

    public Message(String title, DialogObject messageDialog) {
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
