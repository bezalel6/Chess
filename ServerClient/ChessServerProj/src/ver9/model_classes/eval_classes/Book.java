package ver9.model_classes.eval_classes;

import ver9.SharedClasses.moves.Move;
import ver9.model_classes.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class Book {
    private final static String pathToBook = "src/assets/GameInfo/Book/Games.txt";
    private final static File book = new File(pathToBook);
//    private final static ConcurrentHashMap<Long, ArrayList<String>> positionsHashMap = initPositions();
//
//    private static ConcurrentHashMap<Long, ArrayList<String>> initPositions() {
//        System.out.println("initializing positions");
//        ConcurrentHashMap<Long, ArrayList<String>> ret = new ConcurrentHashMap<>();
//        try {
//            Scanner scanner = new Scanner(book);
//            while (scanner.hasNextLine()) {
//                Model currentGame = new Model(Positions.getAllPositions().get(0).getFen());
//                String line = scanner.nextLine();
//                String[] split = line.split(" ");
//                for (int i = 0; i < split.length - 1; i++) {
//                    String moveStr = split[i];
//                    currentGame.applyMove(moveStr);
//                    long hash = currentGame.hashBoard().getFullHash();
//                    if (!ret.containsKey(hash)) {
//                        ret.put(hash, new ArrayList<>());
//                    }
//                    ret.get(hash).add(moveStr);
//                }
//            }
//        } catch (FileNotFoundException e) {
//
//        }
//        return ret;
//    }

//    public static String getBookMove(Model board) {
//        long hash = board.getBoardHash().getFullHash();
//        return positionsHashMap.get(hash).get(new Random().nextInt(positionsHashMap.get(hash).size()));
//    }

    public static String getBookMove(Model model) {
        try {
            Stack<Move> moves = model.getMoveStack();
            StringBuilder completePgn = new StringBuilder();
            for (Move move : moves) {
                String moveStr = move.getAnnotation().trim();
                completePgn.append(moveStr).append(" ");
            }
            Scanner scanner = new Scanner(book);
            ArrayList<String> bookMoves = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(completePgn.toString())) {
                    String s = line.substring(line.indexOf(completePgn.toString()) + completePgn.length());
                    bookMoves.add(s.substring(0, s.indexOf(' ')).trim());
                    return bookMoves.get(0);
                }
            }
            if (!bookMoves.isEmpty())
                return bookMoves.get(new Random().nextInt(bookMoves.size()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
