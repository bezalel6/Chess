package ver10.view.Dialog.BackOk;

import ver10.view.Dialog.Dialogs.BackOkInterface;

public interface CancelOk extends BackOkInterface {
    @Override
    default String getBackText() {
        return "cancel";
    }
}
