package ver8.view.Wishfull.dialogs.GameSelect.Selectables;

import ver8.SharedClasses.StrUtils;
import ver8.SharedClasses.TimeFormat;
import ver8.view.Wishfull.dialogs.Selectable;

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
