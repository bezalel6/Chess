package ver14.SharedClasses.Misc;

import java.util.HashMap;
import java.util.Random;


/**
 * IDs generator.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class IDsGenerator {
//    todo switch to set
    /**
     * The crated ids.
     */
    private final HashMap<String, Object> map = new HashMap<>();

    /**
     * Generate id.
     *
     * @return the string
     */
    public synchronized String generate() {
        String ret;
        do {
            ret = new Random().nextInt() + "";
        } while (!canUseId(ret));
        map.put(ret, new Object());
        return ret;
    }

    /**
     * Can use id boolean.
     *
     * @param id the id
     * @return the boolean
     */
    public boolean canUseId(String id) {
        return !map.containsKey(id);
    }
}
