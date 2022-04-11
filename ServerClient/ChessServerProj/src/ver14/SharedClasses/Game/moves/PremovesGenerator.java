package ver14.SharedClasses.Game.moves;

import ver14.SharedClasses.Game.BoardSetup.Board;
import ver14.SharedClasses.Game.BoardSetup.Square;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.pieces.Piece;

public class PremovesGenerator {
    public static void main(String[] args) {
        System.out.println(generatePreMoves(Board.startingPos(), PlayerColor.WHITE));
    }

    public static MovesList generatePreMoves(Board board, PlayerColor clr) {
        MovesList list = new MovesList();
        for (var square : board) {
            Piece piece = square.getPiece();
            if (!square.isEmpty() && piece.isOnMyTeam(clr)) {
                for (Direction dir : square.getPiece().pieceType.getWalkingDirections()) {
                    Location loc = square.getLoc();
                    do {
                        loc = Location.getLoc(loc, dir.perspective(clr));
                        if (loc == null)
                            break;
                        var lookingAt = board.getPiece(loc);
                        if (lookingAt != Square.EMPTY_PIECE) {
                            if (piece.pieceType.isAttack(dir, loc.getMaxDistance(square.getLoc()))) {
                                list.add(new Move(square.getLoc(), loc, lookingAt.pieceType));
                            }
//                            break;
                        } else
                            list.add(new Move(square.getLoc(), loc));

                    } while (true);
                }
            }
        }
        System.out.println("generated " + list);
        return list;
    }
}
