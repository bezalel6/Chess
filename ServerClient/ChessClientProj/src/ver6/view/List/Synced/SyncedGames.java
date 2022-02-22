package ver6.view.List.Synced;

import ver6.SharedClasses.Sync.SyncedItems;
import ver6.SharedClasses.Sync.SyncedListType;
import ver6.view.List.ComponentsList;
import ver6.view.List.SelfContainedList;
import ver6.view.dialogs.SelectedCallBack;

public class SyncedGames extends SelfContainedList implements SyncableList {

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
    public ComponentsList list() {
        return this;
    }

    @Override
    public void onOk() {

    }


}
