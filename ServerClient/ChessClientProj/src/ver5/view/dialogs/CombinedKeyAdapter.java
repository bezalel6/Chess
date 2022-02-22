package ver5.view.dialogs;

import ver5.SharedClasses.Callback;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CombinedKeyAdapter {
    public static KeyAdapter combinedKeyAdapter(Callback callback) {
        return new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                callback.callback();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                callback.callback();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                callback.callback();
            }
        };
    }
}
