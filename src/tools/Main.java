package tools;

import ver19_square_control.Location;

import java.util.ArrayList;


class dad {
    public dad() {

    }

    public dad(dad other) {

    }
}

class child extends dad {
    public child() {

    }

    public child(child other) {
        super();
        System.out.println("in cp");
    }

}

public class Main {
    //region STFU
    static String myResults =
            "b1d2: 1\n" +
                    "b1c3: 1\n" +
                    "b1a3: 1\n" +
                    "c1d2: 1\n" +
                    "c1e3: 1\n" +
                    "c1f4: 1\n" +
                    "c1g5: 1\n" +
                    "c1h6: 1\n" +
                    "d1d2: 1\n" +
                    "d1d3: 1\n" +
                    "d1d4: 1\n" +
                    "d1d5: 1\n" +
                    "d1d6: 1\n" +
                    "e1f1: 1\n" +
                    "e1f2: 1\n" +
                    "e1d2: 1\n" +
                    "h1g1: 1\n" +
                    "h1f1: 1\n" +
                    "a2a3: 1\n" +
                    "a2a4: 1\n" +
                    "b2b3: 1\n" +
                    "b2b4: 1\n" +
                    "c2c3: 1\n" +
                    "e2g3: 1\n" +
                    "e2c3: 1\n" +
                    "e2f4: 1\n" +
                    "e2d4: 1\n" +
                    "e2g1: 1\n" +
                    "g2g3: 1\n" +
                    "g2g4: 1\n" +
                    "h2h3: 1\n" +
                    "h2h4: 1\n" +
                    "c4d5: 1\n" +
                    "c4e6: 1\n" +
                    "c4f7: 1\n" +
                    "c4d3: 1\n" +
                    "c4b3: 1\n" +
                    "c4b5: 1\n" +
                    "c4a6: 1\n" +
                    "d7c8: 1\n" +
                    "d7c8: 1\n" +
                    "d7c8: 1\n" +
                    "d7c8: 1";
    static String stockfishResults =
            "a2a3: 1\n" +
                    "b2b3: 1\n" +
                    "c2c3: 1\n" +
                    "g2g3: 1\n" +
                    "h2h3: 1\n" +
                    "a2a4: 1\n" +
                    "b2b4: 1\n" +
                    "g2g4: 1\n" +
                    "h2h4: 1\n" +
                    "d7c8q: 1\n" +
                    "d7c8r: 1\n" +
                    "d7c8b: 1\n" +
                    "d7c8n: 1\n" +
                    "b1d2: 1\n" +
                    "b1a3: 1\n" +
                    "b1c3: 1\n" +
                    "e2g1: 1\n" +
                    "e2c3: 1\n" +
                    "e2g3: 1\n" +
                    "e2d4: 1\n" +
                    "e2f4: 1\n" +
                    "c1d2: 1\n" +
                    "c1e3: 1\n" +
                    "c1f4: 1\n" +
                    "c1g5: 1\n" +
                    "c1h6: 1\n" +
                    "c4b3: 1\n" +
                    "c4d3: 1\n" +
                    "c4b5: 1\n" +
                    "c4d5: 1\n" +
                    "c4a6: 1\n" +
                    "c4e6: 1\n" +
                    "c4f7: 1\n" +
                    "h1f1: 1\n" +
                    "h1g1: 1\n" +
                    "d1d2: 1\n" +
                    "d1d3: 1\n" +
                    "d1d4: 1\n" +
                    "d1d5: 1\n" +
                    "d1d6: 1\n" +
                    "e1f1: 1\n" +
                    "e1d2: 1\n" +
                    "e1f2: 1\n" +
                    "e1g1: 1";

    //endregion
    public static void main(String[] args) {
        child c = new child();
        System.out.println(c instanceof child);
        dad d = new dad(c);
        System.out.println(d instanceof child);
    }

    private static ArrayList<String> strToList(String str) {
        char[] arr = str.toCharArray();
        ArrayList<String> ret = new ArrayList<>();
        int prev = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '\n') {
                ret.add(str.substring(prev, i));
                prev = i + 1;
            }
        }
        ret.add(str.substring(prev));
        return ret;
    }

    void checkMatchingLocsHash() {
        ArrayList<Integer> locs = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                locs.add(new Location(i, j).hashCode());
            }
        }
        for (int i = 0, locsSize = locs.size(); i < locsSize; i++) {
            int h = locs.get(i);
            for (int j = 0, size = locs.size(); j < size; j++) {
                if (j == i)
                    continue;
                int x = locs.get(j);
                if (x == h)
                    System.out.println("found shiz! " + x);
            }
        }
    }

    void matchLists() {
        ArrayList<String> list1 = strToList(myResults);
        ArrayList<String> list2 = strToList(stockfishResults);
        list1.forEach(s -> {
            if (!list2.contains(s)) {
                list2.forEach(s2 -> {
                    String d1 = s.substring(0, s.indexOf(':'));
                    String d2 = s2.substring(0, s2.indexOf(':'));
                    if (d2.equals(d1)) {
                        System.out.println("Found Different results for " + d1 + ". my res = " + s.substring(s.indexOf(' ')) + " stockfish = " + s2.substring(s2.indexOf(' ')));
                    }
                });
            }
        });
    }
}
