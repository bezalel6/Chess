package ver14.Model;

import ver14.Model.MoveGenerator.MoveGenerator;
import ver14.SharedClasses.Location;
import ver14.SharedClasses.PlayerColor;
import ver14.SharedClasses.Utils.ArrUtils;
import ver14.SharedClasses.board_setup.Board;
import ver14.SharedClasses.moves.Direction;
import ver14.SharedClasses.pieces.Piece;
import ver14.SharedClasses.pieces.PieceType;

public class FastAttackedSquares {
    private final Board board;
    private final Location loc;

    public FastAttackedSquares(Board board, Location loc) {
        this.board = board;
        this.loc = loc;
    }

    public boolean isAttacked() {
        PlayerColor myClr = board.getPiece(loc).playerColor;
        Direction[] directions = PieceType.QUEEN.getAttackingDirections();
        directions = ArrUtils.concat(directions, PieceType.KNIGHT.getAttackingDirections());
        for (Direction direction : directions) {
            for (int i = 0; i < MoveGenerator.numSquaresToEdge(loc, direction); i++) {
                Location checking = Location.getLoc(loc, i + 1, direction);
                if (checking == null)
                    break;
                Piece piece = board.getPiece(checking);
                if (piece != null) {
                    if (piece.isOnMyTeam(myClr) || !piece.pieceType.isAttack(direction, loc.getMaxDistance(checking)))
                        break;

                    return true;
                }
            }
        }

        return false;
    }
}
