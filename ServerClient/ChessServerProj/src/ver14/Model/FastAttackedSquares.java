package ver14.Model;

import ver14.Model.MoveGenerator.MoveGenerator;
import ver14.SharedClasses.Game.BoardSetup.Board;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.moves.Direction;
import ver14.SharedClasses.Game.pieces.Piece;

import java.util.List;

public class FastAttackedSquares {


    public static boolean isAttacked(Board board, Location loc, PlayerColor attackedBy) {
        List<Direction> allUsedDirections = Direction.ALL_USED_DIRECTIONS;
        for (int j = 0, allUsedDirectionsSize = allUsedDirections.size(); j < allUsedDirectionsSize; j++) {
            Direction direction = allUsedDirections.get(j);
            int sqrs = MoveGenerator.numSquaresToEdge(loc, direction);
            for (int i = 0; i < sqrs; i++) {
                Location checking = Location.getLoc(loc, i + 1, direction);
                if (checking == null)
                    break;
                Piece piece = board.getPiece(checking);
                if (piece != null) {

//                    todo opposite thingy
                    if (!piece.isOnMyTeam(attackedBy) || !piece.pieceType.isAttack(direction.opposite(), loc.getMaxDistance(checking)))
                        break;

                    return true;
                }
            }
        }

        return false;
    }
}
