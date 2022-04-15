package ver14.view.Dialog.BackOk;

import ver14.SharedClasses.Callbacks.VoidCallback;

public interface BackOkInterface {
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

    //override and return null if you dont want to create the button
    default String getBackText() {
        return "back";
    }

//    default boolean enableBackOk() {
//        return true;
//    }

    //override and return null if you dont want to create the button
    default String getOkText() {
        return "ok";
    }

    void onBack();

    void onOk();
}
