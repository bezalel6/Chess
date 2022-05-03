package ver14.SharedClasses.Game.Moves;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Moves list.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MovesList extends ArrayList<Move> implements Serializable {

    /**
     * The Hash.
     */
    private long hash = 0;

    /**
     * The Final hash.
     */
    private long finalHash = 0;

    /**
     * Instantiates a new Moves list.
     */
    public MovesList() {

    }

    /**
     * Instantiates a new Moves list.
     *
     * @param other the other
     */
    public MovesList(MovesList other) {
        this.finalHash = other.finalHash;
        other.stream().map(Move::new).forEach(m -> {
            m.setCreatedListHashSupplier(() -> finalHash);
            this.add(m);
        });
    }

    /**
     * Add move to the list and change the hash accordingly.
     *
     * @param move the move
     * @return the boolean
     */
    @Override
    public boolean add(Move move) {
        hash ^= move.hashCode();
        return super.add(move);
    }

    /**
     * Gets final hash.
     *
     * @return the final hash
     */
    public long getFinalHash() {
        return finalHash;
    }

    /**
     * Finalize hash.
     */
    public void finalizeHash() {
        this.finalHash = hash;
    }

    /**
     * Find move move.
     *
     * @param basicMove the basic move
     * @return the move
     */
    public Move findMove(BasicMove basicMove) {
        return findMove(basicMove, m -> true);
    }

    /**
     * Find move move.
     *
     * @param basicMove    the basic move
     * @param compareMoves the compare moves
     * @return the move
     */
    public Move findMove(BasicMove basicMove, CompareMoves compareMoves) {
        return basicMove == null ? null : stream().filter(m -> m.equals(basicMove) && compareMoves.equals(m)).findAny().orElse(null);
    }

    /**
     * Create simple str string.
     *
     * @return the string
     */
    public String createSimpleStr() {
        return stream().map(BasicMove::getBasicMoveAnnotation).collect(StringBuilder::new, (stringBuilder, s) -> {
            stringBuilder.append(s).append("\n");
        }, StringBuilder::append) + "";
    }

    /**
     * Compare moves.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public interface CompareMoves {
        /**
         * Equals boolean.
         *
         * @param comparingTo the comparing to
         * @return the boolean
         */
        boolean equals(Move comparingTo);
    }

//    @Override
//    public boolean add(Move move) {
//        throw new Error("USE ADD WITH PIECE TYPE");
//    }
//
//    public boolean add(Move adding, PieceType movingPiece) {
//        throw new Error("ADDING TO GENERAL MOVES LIST");
//    }
//
//    @Override
//    public boolean addAll(Collection<? extends Move> c) {
////        throw new Error("USE ADD WITH PIECE TYPE");
//        super.addAll(c);
//        return true;
//    }
//
//    public void addAll(Collection<? extends Move> c, PieceType pieceType) {
//        addAll(c);
//    }

//    private boolean canCastle(Castling castling) {
//        if (attacked.anyMatch(myKingLoc))
//            return false;
//
//        int mult = castling.getSide() == CastlingRights.Side.KING ? 1 : -1;
//
//        for (int i = 1; i <= 2; i++) {
//            if (attacked.anyMatch(Location.getLoc(castling.getMovingFrom(), i * mult))) {
//                return false;
//            }
//        }
//        return true;
//    }


}
