package ver8.view.Dialog.Components;

import ver8.SharedClasses.Sync.SyncableItem;
import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.Selectables.Selectable;
import ver8.view.Dialog.Selectables.SelectableButton;
import ver8.view.Dialog.SyncableList;
import ver8.view.Dialog.WinPnl;
import ver8.view.OldDialogs.Header;

import java.util.ArrayList;

public abstract class SyncableListComponent extends ListComponent implements SyncableList {
    private final SyncedListType listType;

    public SyncableListComponent(Header header, SyncedListType listType, Dialog parent) {
        super(WinPnl.MAKE_SCROLLABLE, header, parent);
        this.listType = listType;
        parent.registerSyncableList(this);
    }

    @Override
    public SyncedListType syncedListType() {
        return listType;
    }

    @Override
    public void sync(SyncedItems items) {
        ArrayList<SelectableButton> remove = new ArrayList<>();
        SyncedItems add = items.clean();
        btns.forEach(btn -> {
            Selectable value = btn.getValue();
            assert value instanceof SyncableItem;
            SyncableItem item = (SyncableItem) value;
            if (items.containsKey(item.ID())) {
                add.remove(item.ID());
            } else {
                remove.add(btn);
            }
        });
        remove.forEach(btn -> {
            btns.remove(btn);
            remove(btn);
        });
        addComponents(Selectable.createSelectables(add));
    }


}
