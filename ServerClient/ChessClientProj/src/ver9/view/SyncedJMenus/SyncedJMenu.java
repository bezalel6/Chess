package ver9.view.SyncedJMenus;

import ver9.SharedClasses.Callbacks.MessageCallback;
import ver9.SharedClasses.FontManager;
import ver9.SharedClasses.Sync.SyncedListType;
import ver9.SharedClasses.messages.Message;
import ver9.view.Dialog.Components.Parent;
import ver9.view.Dialog.Components.SyncableListComponent;
import ver9.view.Dialog.Dialogs.Header;
import ver9.view.Dialog.Selectables.Button.MenuItem;
import ver9.view.Dialog.Selectables.Button.SelectableBtn;
import ver9.view.Dialog.Selectables.Selectable;
import ver9.view.Dialog.SyncableList;

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
            super(header.getTxt());
            setText(header.getTxt());
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
            setText(header.getTxt() + str);
        }
    }
}
