package ver24_flip_board.view_classes.dialogs_classes;

import ver24_flip_board.view_classes.dialogs_classes.dialog_objects.DialogButton;
import ver24_flip_board.view_classes.dialogs_classes.dialog_objects.DialogLabel;

import javax.swing.*;

public class YesOrNo extends Dialogs {
    public static final int YES = 0, NO = 1;

    private final String msg;

    public YesOrNo(String title, String msg) {
        super(title);
        this.msg = msg;
    }

    @Override
    public Object run() {
        bodyPnl.add(new DialogLabel(msg));
        return runDialog();
    }

    @Override
    void setBottomPnl() {
        bottomPnl.add(new DialogButton(YES, "Yes"));
        bottomPnl.add(new DialogButton(NO, "No"));
    }
}
