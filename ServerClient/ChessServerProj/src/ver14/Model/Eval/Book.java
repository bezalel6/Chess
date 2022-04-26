package ver14.Model.Eval;

import ver14.Model.Model;
import ver14.SharedClasses.Misc.Enviornment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


/**
 * Book - utility class for interacting with the opening book.
 * an opening book is a database containing lines of openings.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Book {
    /**
     * The constant pathToBook.
     */
    private final static String pathToBook = (!Enviornment.IS_JAR ? "src" : "") + "/assets/GameInfo/Book/Games.txt";
    /**
     * The constant book.
     */
    private final static File book = new File(pathToBook);


    /**
     * looks for a game matching the current game inside the book games database.
     * if one is found, the next move is saved.
     * after going through all games in the database, if any matching game was found, a random move from the saved moves is returned.
     * if no matching game was found, null is returned.
     *
     * @param model - current game position
     * @return a random move from every game found in the games' database if one is found. null otherwise
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
