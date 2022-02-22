package ver5.SharedClasses;

import java.util.HashMap;
import java.util.Random;

public class IDsGenerator {
    private HashMap<String, Object> map = new HashMap<>();

    public String generate() {
        String ret;
        do {
            ret = new Random().nextInt() + "";
        } while (map.containsKey(ret));
        map.put(ret, new Object());
        return ret;
    }
}
