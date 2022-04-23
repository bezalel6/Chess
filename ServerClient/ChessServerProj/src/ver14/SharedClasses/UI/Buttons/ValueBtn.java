package ver14.SharedClasses.UI.Buttons;

import ver14.SharedClasses.Callbacks.Callback;

import java.awt.*;

/*
 * ValueBtn
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * ValueBtn -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * ValueBtn -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class ValueBtn<T> extends MyJButton {
    public ValueBtn(String text, Font font, T value, Callback<T> onClick) {
        super(text, font, () -> {
            onClick.callback(value);
        });
    }
}