package ver14.Model.Eval;

import ver14.Model.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


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

    /**
     * looks for a game matching the current game inside the book games database.
     * if one is found, the next move is saved.
     * after going through all games in the database, if any matching game was found, a random move from the saved moves is returned.
     * if no matching game was found, null is returned.
     *
     * @param model - current game position
     * @return a random move from every game found in the games database if one is found. null otherwise
     */
    public static String getBookMove(Model model) {
        try {
            String completePgn = (model.getPGN());
            Scanner scanner = new Scanner(book);
            ArrayList<String> bookMoves = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(completePgn)) {
                    String s = line.substring(line.indexOf(completePgn) + completePgn.length());
                    bookMoves.add(s.substring(0, s.indexOf(' ')).trim());
//                    return bookMoves.get(0);
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
