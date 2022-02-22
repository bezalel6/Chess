package ver2.types;

import ver2.Location;
import ver2.Model;

import java.util.ArrayList;

public class Pawn extends Piece {
    public Pawn(Location loc, colors pieceColor) {
        super(1, loc, pieceColor, types.Pawn, "");
    }

    @Override
    public ArrayList<ver2.types.Path> canMoveTo() {
        ArrayList<ver2.types.Path> ret = new ArrayList();
        int diff=0;
        if (isWhite()) {
            diff--;
        } else {
            diff++;
        }
        boolean advance = true;
        Location pieceLoc = getLoc();
        int myR = pieceLoc.getRow();
        int myC = pieceLoc.getCol();
        for (int i = 0; i < Model.pieces.size() ; i++) {
            Location otherPieceLoc = Model.pieces.get(i).getLoc();
            int otherR = otherPieceLoc.getRow();
            int otherC = otherPieceLoc.getCol();

            if(otherR+diff==myR&&otherC==myC)
            {
                advance = false;
            }
            if(!isOnMyTeam(Model.pieces.get(i)) && myR-diff==otherR)
            {
                if(otherC-myC==-1||otherC-myC==1)
                    add(ret,otherPieceLoc);

            }
        }
        if(advance)
        {
            add(ret,myR-diff,myC);
        }
        if(!hasMoved())
        {
            add(ret,myR-diff*2,myC);
        }
        return ret;
    }
}
