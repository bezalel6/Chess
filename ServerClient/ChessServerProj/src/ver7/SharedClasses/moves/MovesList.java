package ver7.SharedClasses.moves;

import ver7.SharedClasses.Hashable;

import java.util.ArrayList;

public class MovesList extends ArrayList<Move> implements Hashable {

    protected boolean doneAdding = false;

    public MovesList() {

    }

    public MovesList(MovesList other) {
//        this(other.pins, other.attacked, other.myKingLoc);
        addAll(other);
        //todo cp list
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


    public void doneAdding() {
        doneAdding = true;

    }


}
