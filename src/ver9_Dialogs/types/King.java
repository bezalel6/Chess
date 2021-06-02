package ver9_Dialogs.types;

import ver9_Dialogs.Location;
import ver9_Dialogs.Move;

import java.util.ArrayList;

public class King extends Piece {
    public static int worth = 3;
    public Castling kingSide, queenSide;

    public King(Location loc, colors pieceColor, boolean hasMoved) {
        super(worth, loc, pieceColor, types.KING, "K", hasMoved);
    }

    @Override
    public ArrayList<Move> canMoveTo(Piece[][] pieces) {
        kingSide = null;
        queenSide = null;
        ArrayList<Move> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        add(ret, myR + 1, myC, pieces);
        add(ret, myR, myC + 1, pieces);
        add(ret, myR + 1, myC + 1, pieces);
        add(ret, myR - 1, myC, pieces);
        add(ret, myR, myC - 1, pieces);
        add(ret, myR - 1, myC - 1, pieces);
        add(ret, myR + 1, myC - 1, pieces);
        add(ret, myR - 1, myC + 1, pieces);
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (!getHasMoved() && p != null && p.isOnMyTeam(this) && p instanceof Rook && !p.getHasMoved()) {
                    Location rookLoc = p.getLoc();

                    if (myC > rookLoc.getCol() && myC - 1 > -1 && myC - 2 > -1) {
                        Location kingMiddleMove = new Location(myR, myC - 1), kingFinalLoc = new Location(myR, myC - 2), rookFinalLoc = new Location(myR, myC - 1), rookMiddleLoc = new Location(myR, myC - 3);
                        queenSide = new Castling((Rook) p, kingMiddleMove, kingFinalLoc, rookFinalLoc, rookMiddleLoc);
                        Move thisMove = add(ret, new Move(this, kingFinalLoc, false), pieces);
                        if (thisMove != null)
                            thisMove.setSpecialMove(Move.SpecialMoves.LONG_CASTLE);
                    } else {
                        Location kingMiddleMove = new Location(myR, myC + 1), kingFinalLoc = new Location(myR, myC + 2), rookFinalLoc = new Location(myR, myC + 1), rookMiddleLoc = null;
                        kingSide = new Castling((Rook) p, kingMiddleMove, kingFinalLoc, rookFinalLoc, rookMiddleLoc);
                        Move thisMove = add(ret, new Move(this, kingFinalLoc, false), pieces);
                        if (thisMove != null)
                            thisMove.setSpecialMove(Move.SpecialMoves.SHORT_CASTLE);
                    }

                }
            }
        }
        return ret;
    }

}
