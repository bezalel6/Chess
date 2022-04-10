package ver14.view.Dialog.Components;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.messages.Message;
import ver14.view.Dialog.BackOk.CancelOk;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.SyncableList;

public interface Parent {
    void registerSyncedList(SyncableList list);

    void askServer(Message msg, MessageCallback onRes);

    void onUpdate();

    default void addToNavText(String str) {
    }

    default void dialogWideErr(String err) {
        if (this instanceof Dialog dialog)
            dialog.dialogWideErr(err);
    }

    void done();

    void back();

    default boolean tryCancel() {
        DialogCard card = currentCard();
        if (card instanceof CancelOk cancelOk) {
            cancelOk.onCancel();
            return true;
        }
        return false;
    }

    DialogCard currentCard();

    default boolean tryOk(boolean verify) {
        DialogCard card = currentCard();
        if (card != null && (!verify || card.isOkEnabled())) {
            card.onOk();
            return true;
        }
        return false;
    }

    void addOnClose(VoidCallback callback);

}
