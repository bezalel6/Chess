package ver14.view.Dialog.Components;

import ver14.SharedClasses.Sync.SyncableItem;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.Button.SelectableBtn;
import ver14.view.Dialog.Selectables.Selectable;
import ver14.view.Dialog.SyncableList;
import ver14.view.Dialog.WinPnl;
import ver14.view.IconManager.Size;

import java.util.ArrayList;

public abstract class SyncableListComponent extends ListComponent implements SyncableList {
    public static final Size listSize = new Size(250);
    public static final Size listItemSize = new Size(listSize) {{
        multMe(0.7);
    }};
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
        if (parent != null) {
            parent.addToNavText(" (" + listSize() + ")");
            parent.enableNavBtn(listSize() > 0);
        }
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
