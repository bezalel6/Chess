package ver8.view.OldDialogs.GameSelect.buttons;

import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.List.OldComponentsList;
import ver8.view.List.Synced.OldSyncableList;
import ver8.view.OldDialogs.MyDialog;
import ver8.view.OldDialogs.Navigation.OldNavable;
import ver8.view.OldDialogs.Selectable;
import ver8.view.OldDialogs.SelectedCallBack;

import java.util.ArrayList;
import java.util.Collection;

public class SyncedSelectablesListOldOld extends SelectablesListOld implements OldSyncableList {
    public final SyncedListType syncedListType;
    private OldNavable navParent;

    public SyncedSelectablesListOldOld(String header, SelectedCallBack onSelect, MyDialog parent, SyncedListType syncedListType) {
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
    public OldComponentsList getComponentsList() {
        return this;
    }
}
