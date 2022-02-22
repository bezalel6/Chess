package ver7.view.List.Synced;

import ver7.SharedClasses.Sync.SyncedItems;
import ver7.SharedClasses.Sync.SyncedListType;
import ver7.view.List.ComponentsList;
import ver7.view.List.SelfContainedList;
import ver7.view.dialogs.SelectedCallBack;

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
