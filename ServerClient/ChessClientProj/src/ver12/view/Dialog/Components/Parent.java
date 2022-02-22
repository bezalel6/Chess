package ver12.view.Dialog.Components;

import ver12.SharedClasses.Callbacks.MessageCallback;
import ver12.SharedClasses.messages.Message;
import ver12.view.Dialog.SyncableList;

public interface Parent {
    void registerSyncedList(SyncableList list);

    void askServer(Message msg, MessageCallback onRes);

    void onUpdate();

    default void addToNavText(String str) {
    }

    void done();

    void back();

}
