package ver14.SharedClasses.Game.Moves;

import java.util.ArrayList;

/*
 * MovesList
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * MovesList -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * MovesList -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class MovesList extends ArrayList<Move> {

    private long hash = 0;

    public MovesList() {

    }

    public MovesList(MovesList other) {
//        this(other.pins, other.attacked, other.myKingLoc);
        other.stream().map(Move::new).forEach(this::add);
//        addAll(other);
        //todo cp list
    }

    @Override
    public boolean add(Move move) {
        hash ^= move.hashCode();
        return super.add(move);
    }

    public Move findMove(BasicMove basicMove) {
        return findMove(basicMove, m -> true);
    }

    public Move findMove(BasicMove basicMove, CompareMoves compareMoves) {
        return basicMove == null ? null : stream().filter(m -> m.equals(basicMove) && compareMoves.equals(m)).findAny().orElse(null);
    }

    public long getHash() {
        return hash;
    }

    public String createSimpleStr() {
        return stream().map(BasicMove::getBasicMoveAnnotation).collect(StringBuilder::new, (stringBuilder, s) -> {
            stringBuilder.append(s).append("\n");
        }, StringBuilder::append) + "";
    }

    public interface CompareMoves {
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