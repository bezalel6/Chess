package ver13.view.SyncedJMenus;

import ver13.SharedClasses.Callbacks.MessageCallback;
import ver13.SharedClasses.FontManager;
import ver13.SharedClasses.Sync.SyncedListType;
import ver13.SharedClasses.messages.Message;
import ver13.view.Dialog.Components.Parent;
import ver13.view.Dialog.Components.SyncableListComponent;
import ver13.view.Dialog.Dialogs.Header;
import ver13.view.Dialog.Selectables.Button.MenuItem;
import ver13.view.Dialog.Selectables.Button.SelectableBtn;
import ver13.view.Dialog.Selectables.Selectable;
import ver13.view.Dialog.SyncableList;

import javax.swing.*;
import java.awt.*;

public abstract class SyncedJMenu extends SyncableListComponent {

    public SyncedJMenu(Header header, SyncedListType listType) {
        super(header, listType, null);
        setParent(new MyM(header));
        header.setFont(FontManager.menuItemsFont);
    }

    @Override
    public Component add(Component comp) {
        return getJMenu().add(comp);
    }

    public JMenu getJMenu() {
        return (MyM) parent;
    }

    @Override
    protected void removeContentComponent(Component comp) {
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

    static class MyM extends JMenu implements Parent {
        private final Header header;

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
    }
}