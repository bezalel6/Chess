package ver14.SharedClasses.UI.MyJframe;

import ver14.SharedClasses.Callbacks.VoidCallback;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 * represents a key adapted
 */
public class MyAdapter extends KeyAdapter {
    private static final long coolDown = 1000;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Map<Integer, HeldDown> heldDownMap = new HashMap<>();
    private final Map<Set<Integer>, VoidCallback> actions = new HashMap<>();
    private Integer lastPressedKey = null;
    private long lastPressedTime = 0;


    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if (!pressedKeys.contains(e.getKeyCode()) && heldDownMap.containsKey(e.getKeyCode())) {
            heldDownMap.get(e.getKeyCode()).startPress();
        }
        pressedKeys.add(e.getKeyCode());
        if (lastPressedKey != null && e.getKeyCode() == lastPressedKey && System.currentTimeMillis() - lastPressedTime <= coolDown) {
            return;
        }
        lastPressedTime = System.currentTimeMillis();
        lastPressedKey = e.getKeyCode();
        actions.forEach((set, callback) -> {
            if (pressedKeys.containsAll(set))
                callback.callback();
        });

    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        if (pressedKeys.contains(e.getKeyCode()) && heldDownMap.containsKey(e.getKeyCode())) {
            heldDownMap.get(e.getKeyCode()).endPress();
        }
        pressedKeys.remove(e.getKeyCode());
        lastPressedKey = (lastPressedKey != null && e.getKeyCode() == lastPressedKey) ? null : lastPressedKey;
    }

    /**
     * Add action on held down.
     *
     * @param heldDown the action
     */
    public void addHeldDown(HeldDown heldDown) {
        heldDownMap.put(heldDown.key(), heldDown);
    }

    /**
     * Add action set.
     *
     * @param action the action
     * @param keys   the keys
     * @return the set
     */
    public Set<Integer> addAction(VoidCallback action, Integer... keys) {
        Set<Integer> ret = new HashSet<>(List.of(keys));
        actions.put(ret, action);
        return ret;
    }

    /**
     * Remove action.
     *
     * @param action the action
     */
    public void removeAction(Set<Integer> action) {
        actions.remove(action);
    }


    /**
     * represents a holding down key event with a start and an end.
     */
    public interface HeldDown {
        /**
         * Start press.
         */
        void startPress();

        /**
         * End press.
         */
        void endPress();

        /**
         * the key code.
         *
         * @return the key code
         */
        int key();
    }

}
