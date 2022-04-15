package ver14.view.Dialog.BackOk;

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
