package ver8.view.OldDialogs.GameSelect.selectable;

import ver8.SharedClasses.StrUtils;
import ver8.SharedClasses.TimeFormat;
import ver8.view.OldDialogs.Selectable;

import javax.swing.*;
import java.util.ArrayList;

public class SelectableTimeFormat implements Selectable {

    public final TimeFormat timeFormat;

    private SelectableTimeFormat(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    public static SelectableTimeFormat[] values() {
        ArrayList<SelectableTimeFormat> selectableTimeFormats = new ArrayList<>();
        for (TimeFormat timeFormat : TimeFormat.PRESETS)
            selectableTimeFormats.add(new SelectableTimeFormat(timeFormat));
        return selectableTimeFormats.toArray(new SelectableTimeFormat[0]);
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getText() {
        return StrUtils.createTimeStr(timeFormat.timeInMillis) + "+" + StrUtils.createTimeStr(timeFormat.incrementInMillis);
    }

    @Override
    public String toString() {
        return "SelectableTimeFormat{" +
                "timeFormat=" + timeFormat +
                getText() +
                '}';
    }
}
