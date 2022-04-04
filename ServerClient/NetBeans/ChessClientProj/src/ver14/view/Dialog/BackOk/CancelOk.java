package ver14.view.Dialog.BackOk;

import ver14.view.Dialog.Dialogs.BackOkInterface;

public interface CancelOk extends BackOkInterface {
    @Override
    default String getBackText() {
        return "cancel";
    }
}
