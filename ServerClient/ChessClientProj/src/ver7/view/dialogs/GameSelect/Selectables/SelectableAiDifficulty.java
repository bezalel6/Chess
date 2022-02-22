package ver7.view.dialogs.GameSelect.Selectables;

import ver7.SharedClasses.StrUtils;
import ver7.SharedClasses.TimeFormat;
import ver7.view.dialogs.Selectable;

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
