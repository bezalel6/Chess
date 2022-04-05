package ver14.SharedClasses.ui;

import ver14.SharedClasses.Callbacks.Callback;

import java.awt.*;

public class ObjBtn<T> extends MyJButton {
    public ObjBtn(String text, Font font, T value, Callback<T> onClick) {
        super(text, font, () -> {
            onClick.callback(value);
        });
    }
}
