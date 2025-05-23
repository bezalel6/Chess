package ver30_flip_board_2.view_classes.dialogs_classes;

import ver30_flip_board_2.view_classes.dialogs_classes.dialog_objects.DialogObject;

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
