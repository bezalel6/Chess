package ver36_no_more_location.model_classes.eval_classes;

import ver36_no_more_location.model_classes.Board;
import ver36_no_more_location.model_classes.moves.Move;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class Book {
    private final static String pathToBook = "src/Assets/GameInfo/Book/Games.txt";
    private final static File book = new File(pathToBook);
//    private final static ConcurrentHashMap<Long, ArrayList<String>> positionsHashMap = initPositions();
//
//    private static ConcurrentHashMap<Long, ArrayList<String>> initPositions() {
//        System.out.println("initializing positions");
//        ConcurrentHashMap<Long, ArrayList<String>> ret = new ConcurrentHashMap<>();
//        try {
//            Scanner scanner = new Scanner(book);
//            while (scanner.hasNextLine()) {
//                Board currentGame = new Board(Positions.getAllPositions().get(0).getFen());
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

//    public static String getBookMove(Board board) {
//        long hash = board.getBoardHash().getFullHash();
//        return positionsHashMap.get(hash).get(new Random().nextInt(positionsHashMap.get(hash).size()));
//    }

    public static String getBookMove(Board board) {
        try {
            Stack<Move> moves = board.getMoveStack();
            StringBuilder completePgn = new StringBuilder();
            for (Move move : moves) {
                String moveStr = move.getAnnotation();
                completePgn.append(moveStr).append(" ");
            }
            Scanner scanner = new Scanner(book);
            ArrayList<String> bookMoves = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(completePgn.toString())) {
                    String s = line.substring(line.indexOf(completePgn.toString()) + completePgn.length());
                    bookMoves.add(s.substring(0, s.indexOf(' ')).trim());
                }
            }
            if (!bookMoves.isEmpty())
                return bookMoves.get(new Random().nextInt(bookMoves.size()));
        } catch (FileNotFoundException e) {

        }
        return null;
    }
}
