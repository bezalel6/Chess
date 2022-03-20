package ver14.view.Dialog.Dialogs;

public interface BackOkInterface {
    //override and return null if you dont want to create the button
    default String getBackText() {
        return "back";
    }

    //override and return null if you dont want to create the button
    default String getOkText() {
        return "ok";
    }

    
    void onBack();

    void onOk();
}
