package ver3.types;

import ver3.Location;

public class Castling {
    private Rook rook;
    private Location kingMiddleMove,kingFinalLoc,rookFinalLoc;

    public Castling(Rook rook, Location kingMiddleMove, Location kingFinalLoc, Location rookFinalLoc) {
        this.rook = rook;
        this.kingMiddleMove = kingMiddleMove;
        this.kingFinalLoc = kingFinalLoc;
        this.rookFinalLoc = rookFinalLoc;
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
}
