package ver14.SharedClasses.Game.Moves;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Utils.StrUtils;


public class MoveAnnotation {
    public static final int PIECE = 0, CAPTURE = 1, DESTINATION = 2, GAME_OVER = 3;
    private static final String CAPTURE_ANN = "x";


    public static String annotate(Move move, Piece movingPiece) {
        return annotate(move, movingPiece, "");
    }

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

    public static String basicAnnotate(BasicMove move) {
        return StrUtils.dontCapWord(move.getMovingFrom() + "" + move.getMovingTo());

    }


}
