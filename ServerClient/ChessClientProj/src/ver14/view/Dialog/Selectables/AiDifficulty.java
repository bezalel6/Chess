package ver14.view.Dialog.Selectables;

import ver14.SharedClasses.TimeFormat;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public enum AiDifficulty implements Selectable {
    EASY(new TimeFormat(TimeUnit.SECONDS.toMillis(2))),
    MEDIUM(new TimeFormat(TimeUnit.SECONDS.toMillis(10))),
    HARD(new TimeFormat(TimeUnit.SECONDS.toMillis(30)));

    public final TimeFormat timeFormat;

    AiDifficulty(TimeFormat timeFormat) {
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
