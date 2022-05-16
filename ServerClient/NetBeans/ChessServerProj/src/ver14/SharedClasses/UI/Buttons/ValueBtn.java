package ver14.SharedClasses.UI.Buttons;

import ver14.SharedClasses.Callbacks.Callback;

import java.awt.*;


/**
 * Value btn - a value holding button.
 *
 * @param <T> the value's type
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ValueBtn<T> extends MyJButton {
    /**
     * instantiates a new value btn.
     *
     * @param text    the text
     * @param font    the font
     * @param value   the value
     * @param onClick the on click
     */
    public ValueBtn(String text, Font font, T value, Callback<T> onClick) {
        super(text, font, () -> {
            onClick.callback(value);
        });
    }
}