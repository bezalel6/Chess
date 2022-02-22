package ver12.SharedClasses;

import java.util.HashMap;
import java.util.Random;

public class IDsGenerator {
    private final HashMap<String, Object> map = new HashMap<>();

    public synchronized String generate() {
        String ret;
        do {
            ret = new Random().nextInt() + "";
        } while (!canUseId(ret));
        map.put(ret, new Object());
        return ret;
    }

    public boolean canUseId(String id) {
        return !map.containsKey(id);
    }
}
