package ver9.view.Dialog.Components;

import ver9.SharedClasses.Callbacks.MessageCallback;
import ver9.SharedClasses.messages.Message;
import ver9.view.Dialog.SyncableList;

public interface Parent {
    void registerSyncedList(SyncableList list);

    void askServer(Message msg, MessageCallback onRes);

    void onUpdate();

    default void addToNavText(String str) {
    }
}
