package ver9.view.Dialog.Components;

import ver9.SharedClasses.Sync.SyncableItem;
import ver9.SharedClasses.Sync.SyncedItems;
import ver9.SharedClasses.Sync.SyncedListType;
import ver9.view.Dialog.Dialogs.Header;
import ver9.view.Dialog.Selectables.Button.SelectableBtn;
import ver9.view.Dialog.Selectables.Game;
import ver9.view.Dialog.Selectables.Selectable;
import ver9.view.Dialog.Selectables.SelectableUserInfo;
import ver9.view.Dialog.SyncableList;
import ver9.view.Dialog.WinPnl;

import java.util.ArrayList;

public abstract class SyncableListComponent extends ListComponent implements SyncableList {
    private final SyncedListType listType;

    public SyncableListComponent(Header header, SyncedListType listType, Parent parent) {
        super(WinPnl.MAKE_SCROLLABLE, header, parent);
        this.listType = listType;
        if (parent != null)
            parent.registerSyncedList(this);
    }

    @Override
    public void setParent(Parent parent) {
        super.setParent(parent);
        parent.registerSyncedList(this);
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
            SyncableItem item;
            if (value instanceof Game game) {
                item = game.getGameInfo();
            } else if (value instanceof SelectableUserInfo userInfo) {
                item = userInfo.getUserInfo();
            } else {
                assert value instanceof SyncableItem;
                item = (SyncableItem) value;
            }
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
        addComponents(Selectable.createSelectables(add));
        onUpdate();
    }


}
