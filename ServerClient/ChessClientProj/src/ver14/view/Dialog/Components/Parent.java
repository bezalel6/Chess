package ver14.view.Dialog.Components;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.UI.MyJFrame;
import ver14.view.Dialog.BackOk.BackOkPnl;
import ver14.view.Dialog.BackOk.CancelOk;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.SyncableList;

public interface Parent {

    default MyJFrame.MyAdapter keyAdapter() {
        throw new Error("key adapter not implemented");
    }

    void registerSyncedList(SyncableList list);

    void askServer(Message msg, MessageCallback onRes);

    void onUpdate();

    default void addToNavText(String str) {
    }

    default void dialogWideErr(String err) {
        if (this instanceof Dialog dialog)
            dialog.dialogWideErr(err);
        else if (this instanceof Child child) {
            child.parent().dialogWideErr(err);
        }
    }

    void done();

    void back();

    default void scrollToTop() {
        if (this instanceof Child child)
            child.parent().scrollToTop();
        System.out.println("im trying");
    }

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
        BackOkPnl pnl = backOkPnl();
        if (card != null && (!verify || (pnl != null && pnl.getOk() != null && pnl.getOk().isEnabled()))) {
            pnl.getOk().doClick();
            return true;
        }
        return false;
    }

    BackOkPnl backOkPnl();

    void addOnClose(VoidCallback callback);
}
