package ver14.view.SyncedJMenus;

import ver14.SharedClasses.Sync.SyncedListType;
import ver14.SharedClasses.UI.FontManager;
import ver14.view.Dialog.Components.SyncableListComponent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.Button.MenuItem;
import ver14.view.Dialog.Selectables.Button.SelectableBtn;
import ver14.view.Dialog.Selectables.Selectable;

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
        setParent(new MyMenu(header));
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
        return (MyMenu) parent;
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

}
