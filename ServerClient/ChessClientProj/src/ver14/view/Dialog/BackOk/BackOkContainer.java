package ver14.view.Dialog.BackOk;

public interface BackOkContainer extends BackOkInterface {


    @Override
    default String getBackText() {
        return actualInterface().getBackText();
    }

    BackOkInterface actualInterface();

    @Override
    default String getOkText() {
        return actualInterface().getOkText();
    }

    @Override
    default void onBack() {
        actualInterface().onBack();
    }

    @Override
    default void onOk() {
        actualInterface().onOk();
    }
}
