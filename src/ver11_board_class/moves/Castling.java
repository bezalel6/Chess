package ver11_board_class.moves;

import ver11_board_class.Board;
import ver11_board_class.Location;
import ver11_board_class.types.Rook;

public class Castling extends SpecialMove {
    private Rook rook;
    private Location kingMiddleMove, kingFinalLoc, rookFinalLoc, rookMiddleLoc;

    public Castling(Move move, Rook rook, Location kingMiddleMove, Location kingFinalLoc, Location rookFinalLoc, Location rookMiddleLoc, Board board) {
        super(move, SpecialMoveType.LONG_CASTLE, "O-O-O", board);
        this.rook = rook;
        this.kingMiddleMove = kingMiddleMove;
        this.kingFinalLoc = kingFinalLoc;
        this.rookFinalLoc = rookFinalLoc;
        this.rookMiddleLoc = rookMiddleLoc;
    }

    public Castling(Move move, Rook rook, Location kingMiddleMove, Location kingFinalLoc, Location rookFinalLoc, Board board) {
        super(move, SpecialMoveType.SHORT_CASTLE, "O-O", board);
        this.rook = rook;
        this.kingMiddleMove = kingMiddleMove;
        this.kingFinalLoc = kingFinalLoc;
        this.rookFinalLoc = rookFinalLoc;
    }

    public Location getRookMiddleLoc() {
        return rookMiddleLoc;
    }

    public Rook getRook() {
        return rook;
    }

    public Location getKingMiddleMove() {
        return kingMiddleMove;
    }

    public Location getKingFinalLoc() {
        return kingFinalLoc;
    }

    public Location getRookFinalLoc() {
        return rookFinalLoc;
    }

    @Override
    public String toString() {
        return "Castling{" +
                "rook=" + rook +
                ", kingMiddleMove=" + kingMiddleMove +
                ", kingFinalLoc=" + kingFinalLoc +
                ", rookFinalLoc=" + rookFinalLoc +
                ", rookMiddleLoc=" + rookMiddleLoc +
                '}';
    }
}
