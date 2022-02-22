package ver10.view.Dialog.Dialogs;

public interface BackOkInterface {
    default String getBackText() {
        return "back";
    }

    default String getOkText() {
        return "ok";
    }

    void onBack();

    void onOk();
}
