package ver8.view.Wishfull.List.Synced;

import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.Wishfull.List.ComponentsList;
import ver8.view.Wishfull.List.SelfContainedList;
import ver8.view.Wishfull.dialogs.SelectedCallBack;

public class SyncedGames extends SelfContainedList implements SyncableList {

    private final SyncedListType syncedListType;

    public SyncedGames(SelectedCallBack onSelect, String header, Parent parent, SyncedListType syncedListType) {
        super(ListType.Buttons, onSelect, header, true, parent);
        this.syncedListType = syncedListType;
        parent.registerSyncedList(this);
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

    @Override
    public void onOk() {

    }


}
