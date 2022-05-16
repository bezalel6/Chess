package ver14.view.Dialog.BackOk;

/**
 * represents a container for a Back ok interface.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface BackOkContainer extends BackOkInterface {


    @Override
    default String getBackText() {
        return actualInterface().getBackText();
    }

    /**
     * Actual interface back ok interface.
     *
     * @return the back ok interface
     */
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
