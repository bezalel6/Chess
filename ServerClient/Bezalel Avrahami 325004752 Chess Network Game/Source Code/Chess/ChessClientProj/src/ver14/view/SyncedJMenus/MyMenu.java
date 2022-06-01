package ver14.view.SyncedJMenus;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.view.Dialog.BackOk.BackOkPnl;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.SyncableList;

import javax.swing.*;

/**
 * My implementation of a jmenu used for a {@link SyncedJMenu}.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
class MyMenu extends JMenu implements Parent {
    /**
     * The Header.
     */
    private final Header header;

    /**
     * Instantiates a new Menu.
     *
     * @param header the menu's header
     */
    public MyMenu(Header header) {
        super(header.getText());
        setText(header.getText());
        setIcon(header.getIcon());
        this.header = header;
    }

    @Override
    public void registerSyncedList(SyncableList list) {

    }

    @Override
    public void askServer(Message msg, MessageCallback onRes) {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void addToNavText(String str) {
        setText(header.getText() + str);
    }

    @Override
    public void done() {

    }

    @Override
    public void back() {

    }

    @Override
    public DialogCard currentCard() {
        return null;
    }

    @Override
    public BackOkPnl backOkPnl() {
        return null;
    }

    @Override
    public void addOnClose(VoidCallback callback) {

    }
}
