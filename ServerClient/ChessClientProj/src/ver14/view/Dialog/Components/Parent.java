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

/**
 * Parent - represents a parent of components.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface Parent {

    /**
     * Key adapter - gets the key adapter from the parent.
     *
     * @return the adapter
     */
    default MyJFrame.MyAdapter keyAdapter() {
        throw new Error("key adapter not implemented");
    }

    /**
     * Register a synced list with the server.
     *
     * @param list the list to register
     */
    void registerSyncedList(SyncableList list);

    /**
     * Send a message to the server.
     *
     * @param msg   the msg to send to the server
     * @param onRes the callback for when the server replies
     */
    void askServer(Message msg, MessageCallback onRes);

    /**
     * On update to the hierarchy.
     */
    void onUpdate();

    /**
     * Add to navigation text.
     *
     * @param str the str
     */
    default void addToNavText(String str) {
    }

    /**
     * Dialog wide err.
     *
     * @param err the err
     */
    default void dialogWideErr(String err) {
        if (this instanceof Dialog dialog)
            dialog.dialogWideErr(err);
        else if (this instanceof Child child) {
            child.parent().dialogWideErr(err);
        }
    }

    /**
     * the dialog is finished and it should close.
     */
    void done();

    /**
     * the Back button has been clicked and the dialog should probably go to the last card.
     */
    void back();

    /**
     * Scroll to the top of the dialog.
     */
    default void scrollToTop() {

        if (this instanceof Child child)
            child.parent().scrollToTop();
    }

    /**
     * Try canceling.
     *
     * @return true if successfully clicked the cancel button, false otherwise.
     */
    default boolean tryCancel() {
        DialogCard card = currentCard();
        if (card instanceof CancelOk cancelOk) {
            cancelOk.onCancel();
            return true;
        }
        return false;
    }

    /**
     * get the currently displayed dialog card
     *
     * @return the currently displayed dialog card
     */
    DialogCard currentCard();

    /**
     * Try clicking the ok button.
     *
     * @param verify should try verifying the component before clicking ok
     * @return true if successfully clicked ok, false otherwise.
     */
    default boolean tryOk(boolean verify) {
        DialogCard card = currentCard();
        BackOkPnl pnl = backOkPnl();
        if (card != null && (!verify || (pnl != null && pnl.getOk() != null && pnl.getOk().isEnabled()))) {
            pnl.getOk().doClick();
            return true;
        }
        return false;
    }

    /**
     * get the back ok panel of the current card
     *
     * @return the back ok pnl
     */
    BackOkPnl backOkPnl();

    /**
     * Add on close callback.
     *
     * @param callback the callback
     */
    void addOnClose(VoidCallback callback);

    /**
     * Enable nav button.
     *
     * @param b the b
     */
    default void enableNavBtn(boolean b) {
    }
}
