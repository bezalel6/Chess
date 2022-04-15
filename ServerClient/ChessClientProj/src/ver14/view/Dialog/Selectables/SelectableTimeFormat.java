package ver14.view.Dialog.Selectables;

import ver14.SharedClasses.Game.TimeFormat;
import ver14.SharedClasses.Utils.StrUtils;

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
        return StrUtils.createTimeStr(timeFormat.timeInMillis);
    }

    @Override
    public String toString() {
        return "SelectableTimeFormat{" +
                "timeFormat=" + timeFormat +
                getText() +
                '}';
    }
}
