package ver14.view.Dialog.BackOk;

import ver14.SharedClasses.Callbacks.VoidCallback;

/**
 * represents an object with navigation capabilities.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface BackOkInterface {
    /**
     * The constant noInterface.
     */
    BackOkInterface noInterface = new BackOkInterface() {
        @Override
        public String getBackText() {
            return null;
        }

        @Override
        public String getOkText() {
            return null;
        }

        @Override
        public void onBack() {

        }

        @Override
        public void onOk() {

        }
    };

    /**
     * Create simple interface.
     *
     * @param onOk the on ok
     * @return the back ok interface
     */
    static BackOkInterface createSimpleInterface(VoidCallback onOk) {
        return new BackOkInterface() {
            @Override
            public String getBackText() {
                return null;
            }

            @Override
            public void onBack() {

            }

            @Override
            public void onOk() {
                onOk.callback();
            }
        };
    }

    /**
     * text for the back button. should return null for not creating the back button.
     *
     * @return the text for the back button, or null for not creating it.
     */
    default String getBackText() {
        return "back";
    }


    /**
     * text for the ok button. should return null for not creating the ok button.
     *
     * @return the text for the ok button, or null for not creating it.
     */
    default String getOkText() {
        return "ok";
    }


    /**
     * called on click of the back button.
     */
    void onBack();

    /**
     * called on click of the ok button.
     */
    void onOk();
}
