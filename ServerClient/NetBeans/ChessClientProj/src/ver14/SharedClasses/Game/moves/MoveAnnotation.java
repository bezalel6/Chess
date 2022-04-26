package ver14.SharedClasses.Game.Moves;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Utils.StrUtils;


/**
 * Move annotation - utility class that annotates moves.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MoveAnnotation {
    /**
     * The constant CAPTURE_ANN.
     */
    private static final String CAPTURE_ANN = "x";


    /**
     * Annotate move.
     *
     * @param move        the move
     * @param movingPiece the moving piece
     * @return the annotation
     */
    public static String annotate(Move move, Piece movingPiece) {
        return annotate(move, movingPiece, "");
    }

    /**
     * Annotate move with a unique string.
     *
     * @param move        the move
     * @param movingPiece the moving piece
     * @param unique      the unique string
     * @return the string
     */
    public static String annotate(Move move, Piece movingPiece, String unique) {
        if (movingPiece.pieceType == PieceType.PAWN) {
            String promotionStr = move.getMoveFlag() == Move.MoveFlag.Promotion ? "=" + move.getPromotingTo().getWhitePieceFen() : "";
            if (move.isCapturing()) {
                return StrUtils.dontCapFull(move.getMovingFrom().getColString().toLowerCase() + CAPTURE_ANN + move.getMovingTo() + promotionStr);
            }
            return StrUtils.dontCapFull(move.getMovingTo().toString() + promotionStr);
        }
        String pieceTypeNotation = movingPiece.pieceType.getWhitePieceFen();
        String completeButStatus;

        if (move.getMoveFlag().isCastling) {
            completeButStatus = move.getMoveFlag().castlingSide.castlingNotation;
        } else {
            completeButStatus = pieceTypeNotation + unique + (move.isCapturing() ? CAPTURE_ANN : "") + move.getMovingTo();
        }

        return StrUtils.dontCapFull(completeButStatus + move.getGameStatusStr());
    }

    /**
     * Basic annotate a move. just the source and destination.
     *
     * @param move the move
     * @return the string
     */
    public static String basicAnnotate(BasicMove move) {
        return StrUtils.dontCapWord(move.getMovingFrom() + "" + move.getMovingTo());

    }


}
