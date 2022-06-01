package ver14.view.Dialog.BackOk;

/**
 * represents a cancel ok options navigation.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface CancelOk extends BackOkInterface {
    @Override
    default String getBackText() {
        return "cancel";
    }

    /**
     * On cancel.
     */
    default void onCancel() {
        onBack();
    }
}
