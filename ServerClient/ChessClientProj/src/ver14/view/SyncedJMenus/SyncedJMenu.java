package ver14.view.SyncedJMenus;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.SharedClasses.UI.FontManager;
import ver14.view.Dialog.BackOk.BackOkPnl;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Components.SyncableListComponent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.Button.MenuItem;
import ver14.view.Dialog.Selectables.Button.SelectableBtn;
import ver14.view.Dialog.Selectables.Selectable;
import ver14.view.Dialog.SyncableList;

import javax.swing.*;
import java.awt.*;

/**
 * a synchronized jmenu.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class SyncedJMenu extends SyncableListComponent {

    /**
     * Instantiates a new Synced j menu.
     *
     * @param header   the header
     * @param listType the list type
     */
    public SyncedJMenu(Header header, SyncedListType listType) {
        super(header, listType, null);
        setParent(new MyM(header));
        header.setFont(FontManager.JMenus.headers);
    }

    @Override
    public Component add(Component comp) {
        return getJMenu().add(comp);
    }

    /**
     * Gets j menu.
     *
     * @return the j menu
     */
    public JMenu getJMenu() {
        return (MyM) parent;
    }

    @Override
    public void removeContentComponent(Component comp) {
        super.removeContentComponent(comp);
        getJMenu().remove(comp);
    }

    @Override
    protected SelectableBtn createButton(Selectable item) {
        return new MenuItem(item, s -> {
        });
    }

    @Override
    protected void onSelected() {

    }

    /**
     * My implementation of a jmenu used for a {@link SyncedJMenu}.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    static class MyM extends JMenu implements Parent {
        /**
         * The Header.
         */
        private final Header header;

        /**
         * Instantiates a new My m.
         *
         * @param header the header
         */
        public MyM(Header header) {
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
}
