package ver8.view.List.Synced;

import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.Sync.SyncedListType;
import ver8.view.List.OldComponentsList;
import ver8.view.List.SelfContainedListOld;
import ver8.view.OldDialogs.SelectedCallBack;

public class SyncedGames extends SelfContainedListOld implements OldSyncableList {

    public SyncedGames(SelectedCallBack onSelect, String header, Parent parent) {
        super(ListType.Buttons, onSelect, header, true, parent);
    }


    @Override
    public SyncedListType syncedListType() {
        return SyncedListType.JOINABLE_GAMES;
    }

    @Override
    public void sync(SyncedItems items) {
        super.sync(items);
    }

    @Override
    public OldComponentsList getComponentsList() {
        return this;
    }

    @Override
    public void onOk() {

    }


}
