package ver6.view.dialogs.game_select.buttons;

import ver6.SharedClasses.Sync.SyncedItems;
import ver6.SharedClasses.Sync.SyncedListType;
import ver6.view.List.ComponentsList;
import ver6.view.List.Synced.SyncableList;
import ver6.view.dialogs.MyDialog;
import ver6.view.dialogs.Navigation.OldNavable;
import ver6.view.dialogs.Selectable;
import ver6.view.dialogs.SelectedCallBack;

import java.util.ArrayList;
import java.util.Collection;

public class SyncedSelectablesList extends SelectablesList implements SyncableList {
    public final SyncedListType syncedListType;
    private OldNavable navParent;

    public SyncedSelectablesList(String header, SelectedCallBack onSelect, MyDialog parent, SyncedListType syncedListType) {
        super(new ArrayList<>(), header, null, onSelect, parent, true);
        this.syncedListType = syncedListType;
        if (parent != null)
            parent.registerSyncedList(this);
    }

    public void setNavParent(OldNavable navParent) {
        this.navParent = navParent;
        setNavBtn();
    }

    private void setNavBtn() {
        if (navParent != null)
            navParent.resetNavBtn(" (" + listSize() + ")");

    }

    @Override
    public void sync(Collection<Selectable> selectables, Selectable... _defaultValue) {
        super.sync(selectables, _defaultValue);
        setNavBtn();
    }

    @Override
    public SyncedListType syncedListType() {
        return syncedListType;
    }

    @Override
    public void sync(SyncedItems items) {
        super.sync(items);
    }

    @Override
    public ComponentsList list() {
        return this;
    }
}
