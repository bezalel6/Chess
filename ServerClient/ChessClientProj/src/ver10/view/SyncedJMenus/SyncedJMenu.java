package ver10.view.SyncedJMenus;

import ver10.SharedClasses.Callbacks.MessageCallback;
import ver10.SharedClasses.FontManager;
import ver10.SharedClasses.Sync.SyncedListType;
import ver10.SharedClasses.messages.Message;
import ver10.view.Dialog.Components.Parent;
import ver10.view.Dialog.Components.SyncableListComponent;
import ver10.view.Dialog.Dialogs.Header;
import ver10.view.Dialog.Selectables.Button.MenuItem;
import ver10.view.Dialog.Selectables.Button.SelectableBtn;
import ver10.view.Dialog.Selectables.Selectable;
import ver10.view.Dialog.SyncableList;

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

    class MyM extends JMenu implements Parent {
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
