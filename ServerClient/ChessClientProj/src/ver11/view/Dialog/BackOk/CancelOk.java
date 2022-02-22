package ver11.view.Dialog.BackOk;

import ver11.view.Dialog.Dialogs.BackOkInterface;

public interface CancelOk extends BackOkInterface {
    @Override
    default String getBackText() {
        return "cancel";
    }
}
