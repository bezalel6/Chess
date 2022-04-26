package ver14.view.Dialog.BackOk;

public interface CancelOk extends BackOkInterface {
    @Override
    default String getBackText() {
        return "cancel";
    }

    default void onCancel() {
        onBack();
    }
}
