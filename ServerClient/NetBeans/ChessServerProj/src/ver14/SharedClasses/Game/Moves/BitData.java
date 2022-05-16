package ver14.SharedClasses.Game.Moves;


/**
 * utility class for storing useful board constants in bitboard format
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class BitData {
    /**
     * The constant notAFile.
     * the whole board but the A file
     */
    public static final long notAFile = 0xfefefefefefefefeL;
    /**
     * The constant notHFile.
     * the whole board but the H file
     */
    public static final long notHFile = 0x7f7f7f7f7f7f7f7fL;
    /**
     * The constant everything.
     * the whole board
     */
    public static final long everything = 0xffffffffffffffffL;
}
