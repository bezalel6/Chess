package ver12.view.Dialog.BackOk;

import ver12.view.Dialog.Dialogs.BackOkInterface;

public interface CancelOk extends BackOkInterface {
    @Override
    default String getBackText() {
        return "cancel";
    }
}
