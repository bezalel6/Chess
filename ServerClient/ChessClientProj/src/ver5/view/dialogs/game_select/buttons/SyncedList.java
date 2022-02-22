package ver5.view.dialogs.game_select.buttons;

import ver5.SharedClasses.messages.Message;
import ver5.SharedClasses.messages.SyncedListType;
import ver5.view.dialogs.MyDialog;
import ver5.view.dialogs.Selectable;
import ver5.view.dialogs.SelectedCallBack;
import ver5.view.dialogs.game_select.selectable.SelectableGame;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class SyncedList extends SelectablesList {
    private static final int coolDown = 1000;
    private final SyncedListType syncedListType;
    private ZonedDateTime lastCheckedTime = null;

    public SyncedList(String header, SelectedCallBack onSelect, MyDialog parent, SyncedListType syncedListType) {
        super(new Selectable[0], header, null, onSelect, parent, true);
        this.syncedListType = syncedListType;
    }

    @Override
    public boolean verify() {
//        cycle();
        return super.verify();
    }

    private void cycle() {
        if (lastCheckedTime != null && lastCheckedTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS) < coolDown) {
            return;
        }
        lastCheckedTime = ZonedDateTime.now();
        parent.askServer(new Message(syncedListType.messageType), msg -> {
            sync(SelectableGame.createArr(msg.getRequestedGames()));
        });
    }

}
