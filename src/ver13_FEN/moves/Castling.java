package ver12_myJbutton.moves;

import ver12_myJbutton.Board;
import ver12_myJbutton.Location;
import ver12_myJbutton.types.Rook;

public class Castling extends SpecialMove {
    private Rook rook;
    private Location kingMiddleMove, kingFinalLoc, rookFinalLoc, rookMiddleLoc, rookStartingLoc;

    public Castling(Move move, Rook rook, Location kingMiddleMove, Location kingFinalLoc, Location rookFinalLoc, Location rookMiddleLoc) {
        super(move, SpecialMoveType.LONG_CASTLE);
        this.rook = rook;
        this.kingMiddleMove = kingMiddleMove;
        this.kingFinalLoc = kingFinalLoc;
        this.rookFinalLoc = rookFinalLoc;
        this.rookMiddleLoc = rookMiddleLoc;
        this.rookStartingLoc = new Location(rook.getLoc());
    }

    public Castling(Move move, Rook rook, Location kingMiddleMove, Location kingFinalLoc, Location rookFinalLoc) {
        super(move, SpecialMoveType.SHORT_CASTLE);
        this.rook = rook;
        this.kingMiddleMove = kingMiddleMove;
        this.kingFinalLoc = kingFinalLoc;
        this.rookFinalLoc = rookFinalLoc;
        this.rookStartingLoc = new Location(rook.getLoc());
    }

    public Location getRookStartingLoc() {
        return rookStartingLoc;
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

    public String getAnnotation() {
        return rookMiddleLoc == null ? "O-O" : "O-O-O";
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
