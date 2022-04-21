package ver14.SharedClasses.UI.Buttons;

import ver14.SharedClasses.Callbacks.Callback;

import java.awt.*;

public class ValueBtn<T> extends MyJButton {
    public ValueBtn(String text, Font font, T value, Callback<T> onClick) {
        super(text, font, () -> {
            onClick.callback(value);
        });
    }
}