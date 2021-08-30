package ver18_icon_manager.types;

import ver18_icon_manager.Board;
import ver18_icon_manager.Location;
import ver18_icon_manager.moves.Move;

import java.util.ArrayList;

public class Rook extends Piece {
    public Rook(Location loc, int pieceColor, boolean hasMoved) {
        super(loc, pieceColor, ROOK, "R", hasMoved);
    }

    public Rook(Piece other) {
        super(other);
    }


    @Override
    public ArrayList<ArrayList<Move>> generatePseudoMoves(Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList();
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();

        ArrayList<Move> temp = new ArrayList<>();
        for (int i = 1; i < ROWS - 1; i++) {
            addMove(myR + i, myC, temp, board);

        }
        ret.add(temp);
        temp = new ArrayList<>();
        for (int i = 1; i < ROWS - 1; i++) {
            addMove(myR, myC + i, temp, board);

        }
        ret.add(temp);
        temp = new ArrayList<>();
        for (int i = 1; i < ROWS - 1; i++) {
            addMove(myR - i, myC, temp, board);


        }
        ret.add(temp);
        temp = new ArrayList<>();
        for (int i = 1; i < ROWS - 1; i++) {
            addMove(myR, myC - i, temp, board);
        }
        ret.add(temp);

        return ret;
    }

    private void addMove(int r, int l, ArrayList<Move> list, Board board) {
        Location loc = new Location(r, l);
        if (!isInBounds(loc))
            return;
        list.add(new Move(getLoc(), loc, board));
    }
}
