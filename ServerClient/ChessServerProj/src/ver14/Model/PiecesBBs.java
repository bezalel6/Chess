package ver14.Model;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * Pieces b bs.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class PiecesBBs {
    /**
     * The Size.
     */
    private final int size;
    /**
     * The Bitboards.
     */
    private final Bitboard[] bitboards;
    /**
     * The Prev all.
     */
    private Bitboard prevAll = null;

    /**
     * Instantiates a new Pieces b bs.
     *
     * @param size the size
     */
    public PiecesBBs(int size) {
        this.size = size;

        bitboards = new Bitboard[size];
        for (int i = 0; i < size; i++) {
            bitboards[i] = new Bitboard() {{
                setOnSet(() -> prevAll = null);
            }};
        }

    }

    /**
     * Gets all.
     *
     * @return the all
     */
    public Bitboard getAll() {
        if (prevAll != null)
            return prevAll;
        Bitboard ret = new Bitboard();
        PieceType[] piece_types = PieceType.PIECE_TYPES;
        for (int i = 0, piece_typesLength = piece_types.length; i < piece_typesLength; i++) {
            PieceType pieceType = piece_types[i];
            ret.orEqual(getBB(pieceType));
        }
        prevAll = ret;

        return ret;
    }


    /**
     * Gets bb.
     *
     * @param pieceType the piece type
     * @return the bb
     */
    public Bitboard getBB(PieceType pieceType) {
        return bitboards[pieceType.asInt];
    }

    /**
     * Get bitboards bitboard [ ].
     *
     * @return the bitboard [ ]
     */
    public Bitboard[] getBitboards() {
        return bitboards;
    }

    /**
     * Gets piece type.
     *
     * @param bb the bb
     * @return the piece type
     */
    public PieceType getPieceType(Bitboard bb) {
        for (int i = 0, bitboardsLength = bitboards.length; i < bitboardsLength; i++) {
            Bitboard bitboard = bitboards[i];
            if (bitboard.anyMatch(bb)) {
                return PieceType.getPieceType(i);
            }
        }
        return null;
    }


    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return Arrays.stream(bitboards).map(Bitboard::prettyBoard).collect(Collectors.joining("\n"));
    }
}
