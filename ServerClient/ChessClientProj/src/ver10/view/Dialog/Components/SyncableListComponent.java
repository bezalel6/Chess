package ver10.view.Dialog.Components;

import ver10.SharedClasses.Sync.SyncableItem;
import ver10.SharedClasses.Sync.SyncedItems;
import ver10.SharedClasses.Sync.SyncedListType;
import ver10.view.Dialog.Dialogs.Header;
import ver10.view.Dialog.Selectables.Button.SelectableBtn;
import ver10.view.Dialog.Selectables.Selectable;
import ver10.view.Dialog.SyncableList;
import ver10.view.Dialog.WinPnl;

import java.util.ArrayList;

public abstract class SyncableListComponent extends ListComponent implements SyncableList {
    private final SyncedListType listType;

    public SyncableListComponent(Header header, SyncedListType listType, Parent parent) {
        super(WinPnl.MAKE_SCROLLABLE, header, parent);
        this.listType = listType;
        if (parent != null) {
            setParent(parent);
        }
    }

    @Override
    public void setParent(Parent parent) {
        super.setParent(parent);
        parent.registerSyncedList(this);
        onUpdate();
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        parent.addToNavText(" (" + listSize() + ")");
    }

    @Override
    public SyncedListType syncedListType() {
        return listType;
    }

    @Override
    public void sync(SyncedItems items) {
        ArrayList<SelectableBtn> remove = new ArrayList<>();
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
            if (selected == btn.getValue()) {
                selected = null;
            }
            btns.remove(btn);
            removeContentComponent(btn.comp());
        });
        addComponents(Selectable.createSelectables(add, canUseIcon()));
        onUpdate();
    }

    //can components have icons
    protected boolean canUseIcon() {
        return true;
    }

}
