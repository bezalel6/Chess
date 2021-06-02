package ver4.types;

import ver4.Location;

public class Castling {
    private ver4.types.Rook rook;
    private Location kingMiddleMove, kingFinalLoc, rookFinalLoc, rookMiddleLoc;

    public Castling(Rook rook, Location kingMiddleMove, Location kingFinalLoc, Location rookFinalLoc, Location rookMiddleLoc) {
        this.rook = rook;
        this.kingMiddleMove = kingMiddleMove;
        this.kingFinalLoc = kingFinalLoc;
        this.rookFinalLoc = rookFinalLoc;
        this.rookMiddleLoc = rookMiddleLoc;
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
}
