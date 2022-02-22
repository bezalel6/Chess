package ver10.view.Dialog.Components;

import ver10.SharedClasses.Callbacks.MessageCallback;
import ver10.SharedClasses.messages.Message;
import ver10.view.Dialog.SyncableList;

public interface Parent {
    void registerSyncedList(SyncableList list);

    void askServer(Message msg, MessageCallback onRes);

    void onUpdate();

    default void addToNavText(String str) {
    }

    void done();

    void back();

}
