package ver6.view.dialogs.game_select.selectable;

import ver6.SharedClasses.StrUtils;
import ver6.SharedClasses.TimeFormat;
import ver6.view.dialogs.Selectable;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public enum SelectableAiDifficulty implements Selectable {
    EASY(new TimeFormat(TimeUnit.SECONDS.toMillis(2))),
    MEDIUM(new TimeFormat(TimeUnit.SECONDS.toMillis(10))),
    HARD(new TimeFormat(TimeUnit.SECONDS.toMillis(30)));

    public final TimeFormat timeFormat;

    SelectableAiDifficulty(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getText() {
        return name() + " " + StrUtils.createTimeStr(timeFormat.timeInMillis);
    }
}
