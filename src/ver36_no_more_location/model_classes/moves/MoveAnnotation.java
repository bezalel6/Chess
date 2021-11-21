package ver36_no_more_location.model_classes.moves;

import ver36_no_more_location.Player;
import ver36_no_more_location.model_classes.GameStatus;
import ver36_no_more_location.model_classes.pieces.Piece;
import ver36_no_more_location.model_classes.pieces.PieceType;

public class MoveAnnotation {
    public static final int PIECE = 0, CAPTURE = 1, DESTINATION = 2, GAME_OVER = 3;
    //    DRAW = "½½"
    public static final String DRAW = "½½", MATE = "#", CHECK_ANN = "+";
    public static final String[] GAME_STATUS_ANNOTATIONS = {MATE, DRAW, CHECK_ANN, ""};
    private static final String CAPTURE_ANN = "x";

    private String piece, capture, destination, promotion, gameStatus, uniqueStr;
    private Move move;
    private boolean overriding;

    public MoveAnnotation(Move move) {
        this.move = move;
        PieceType capturingPieceType = move.getCapturingPieceType();
        piece = "";

        capture = "";
        if (move.isCapturing()) {
            if (capturingPieceType != null) {
                setCapture(capturingPieceType);
            }
        }
        destination = move.getMovingTo().toString();
        overriding = false;
        gameStatus = "";
        promotion = "";
        uniqueStr = "";
    }

    public MoveAnnotation(MoveAnnotation other) {
        this.piece = other.piece;
        this.capture = other.capture;
        this.gameStatus = other.gameStatus;
        this.destination = other.destination;
        this.promotion = other.promotion;
        this.uniqueStr = other.uniqueStr;
        this.move = other.move;
        this.overriding = other.overriding;
    }

    public static void setUniqueStrs(MoveAnnotation m1, MoveAnnotation m2) {

    }

    public void setDetailedAnnotation(PieceType movingPieceType) {
        if (!overriding) {

            if (movingPieceType == PieceType.PAWN) {
                if (move.isCapturing())
                    piece = move.getMovingFrom().getColString();
            } else {
                piece = Piece.getWhitePieceFen(movingPieceType);
            }
        }
    }

    public void setUniqueStr(String uniqueStr) {
        this.uniqueStr = uniqueStr;
    }

    public void setGameStatus(GameStatus gStatus) {
        switch (gStatus.getGameStatusType()) {
            case TIE -> gameStatus = DRAW;
            case CHECK -> gameStatus = CHECK_ANN;
            case WIN_OR_LOSS -> gameStatus = MATE;
            default -> gameStatus = "";
        }
    }

    public void setCheck(boolean check) {
        gameStatus = check ? CHECK_ANN : "";
    }

    public void setCapture(PieceType capturingPieceType) {
        capture = CAPTURE_ANN;
    }

    public void overrideEverythingButGameStatus(String str) {
        overriding = true;
        piece = str;
        capture = destination = promotion = "";
    }

    public void setPromotion(PieceType promotingTo) {
        this.promotion = "=" + Piece.getWhitePieceFen(promotingTo);
    }

    @Override
    public String toString() {
        return piece + uniqueStr + capture + destination + promotion + gameStatus;
    }
}
