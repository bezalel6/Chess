package ver14.view.Dialog.Components;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.messages.Message;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.SyncableList;

public interface Parent {
    void registerSyncedList(SyncableList list);

    void askServer(Message msg, MessageCallback onRes);

    void onUpdate();

    default void addToNavText(String str) {
    }

    DialogCard currentCard();

    void done();

    void back();

}
