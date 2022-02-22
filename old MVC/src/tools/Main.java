package tools;

import org.w3c.dom.ranges.Range;
import ver30_flip_board_2.Controller;
import ver30_flip_board_2.Location;
import ver30_flip_board_2.Player;
import ver30_flip_board_2.model_classes.eval_classes.Tables;
import ver30_flip_board_2.model_classes.pieces.Piece;

import java.util.ArrayList;
import java.util.Objects;


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
    public static void main(String[] args) {
        System.out.println(convertToAwfulStr("i want to thank todays sponsor raid shadow legends"));

    }

    private static void a() {
        for (int type : Piece.PIECES_TYPES) {
            double[][] mat = new double[8][8];
            Tables.PieceTable table = Tables.getPieceTable(type);
            for (int player : Player.PLAYERS) {
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        Location loc = new Location(i, j);
                        double val = table.getValue(0, player, loc);
                        if (player == 0) {
                            mat[i][j] = val;
                        } else {
                            System.out.println(mat[i][j] + "  ==  " + val);
                            assert mat[i][j] == val;
                        }
                    }
                }
            }
        }
    }

    private static String convertToAwfulStr(String str) {
        StringBuilder s = new StringBuilder();
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = Character.toLowerCase(charArray[i]);
            s.append(i % 2 == 0 ? c : Character.toUpperCase(c));
        }
        return s.toString();
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

//    void matchLists() {
//        ArrayList<String> list1 = strToList(myResults);
//        ArrayList<String> list2 = strToList(stockfishResults);
//        list1.forEach(s -> {
//            if (!list2.contains(s)) {
//                list2.forEach(s2 -> {
//                    String d1 = s.substring(0, s.indexOf(':'));
//                    String d2 = s2.substring(0, s2.indexOf(':'));
//                    if (d2.equals(d1)) {
//                        System.out.println("Found Different results for " + d1 + ". my res = " + s.substring(s.indexOf(' ')) + " stockfish = " + s2.substring(s2.indexOf(' ')));
//                    }
//                });
//            }
//        });
//    }
}
