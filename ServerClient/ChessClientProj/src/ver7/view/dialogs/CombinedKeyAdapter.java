package ver7.view.dialogs;

import ver7.SharedClasses.Callbacks.VoidCallback;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CombinedKeyAdapter {
    public static KeyAdapter combinedKeyAdapter(VoidCallback callback) {
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
