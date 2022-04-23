package ver14.SharedClasses.Misc;

import java.util.HashMap;
import java.util.Random;

/*
 * IDsGenerator
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * IDsGenerator -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * IDsGenerator -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

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
