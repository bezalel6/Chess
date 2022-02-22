package ver11.view.Dialog.Components;

import ver11.SharedClasses.Callbacks.MessageCallback;
import ver11.SharedClasses.messages.Message;
import ver11.view.Dialog.SyncableList;

public interface Parent {
    void registerSyncedList(SyncableList list);

    void askServer(Message msg, MessageCallback onRes);

    void onUpdate();

    default void addToNavText(String str) {
    }

    void done();

    void back();

}
