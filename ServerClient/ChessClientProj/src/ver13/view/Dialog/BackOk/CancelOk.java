package ver13.view.Dialog.BackOk;

import ver13.view.Dialog.Dialogs.BackOkInterface;

public interface CancelOk extends BackOkInterface {
    @Override
    default String getBackText() {
        return "cancel";
    }
}
