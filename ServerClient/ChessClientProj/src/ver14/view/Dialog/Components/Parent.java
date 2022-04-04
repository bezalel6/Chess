package ver14.view.Dialog.Components;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.messages.Message;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.SyncableList;

public interface Parent {
    void registerSyncedList(SyncableList list);

    void askServer(Message msg, MessageCallback onRes);

    void onUpdate();

    default void addToNavText(String str) {
    }

    void done();

    void back();

    default void tryOk(boolean verify) {
        DialogCard card = currentCard();
        if (card != null && (!verify || card.isOkEnabled()))
            card.onOk();
    }

    DialogCard currentCard();

    void addOnClose(VoidCallback callback);

}
