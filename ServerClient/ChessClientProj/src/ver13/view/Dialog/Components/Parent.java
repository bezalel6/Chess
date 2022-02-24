package ver13.view.Dialog.Components;

import ver13.SharedClasses.Callbacks.MessageCallback;
import ver13.SharedClasses.messages.Message;
import ver13.view.Dialog.SyncableList;

public interface Parent {
    void registerSyncedList(SyncableList list);

    void askServer(Message msg, MessageCallback onRes);

    void onUpdate();

    default void addToNavText(String str) {
    }

    void done();

    void back();

}
