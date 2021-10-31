package ver34_faster_move_generation.model_classes.moves;

import ver34_faster_move_generation.Player;
import ver34_faster_move_generation.model_classes.GameStatus;

import static ver34_faster_move_generation.model_classes.pieces.Piece.*;

public class MoveAnnotation {
    public static final int PIECE = 0, CAPTURE = 1, DESTINATION = 2, GAME_OVER = 3;
    public static final String DRAW = "½½", MATE = "#", CHECK_ANN = "+";
    public static final String[] GAME_STATUS_ANNOTATIONS = {MATE, DRAW, CHECK_ANN, ""};
    private static final String CAPTURE_ANN = "x";

    private String piece, capture, destination, promotion, gameStatus, uniqueStr;

    public MoveAnnotation(Move move) {
//        int movingPieceType = move.getMovingPieceType();
        int capturingPieceType = move.getCapturingPieceType();
        piece = "";
//        if (movingPieceType == PAWN) {
//            if (move.isCapturing())
//                piece = move.getMovingFrom().getColString();
//        } else {
//            piece = PIECES_FENS[Player.WHITE][movingPieceType];
//        }
        capture = "";
        if (move.isCapturing()) {
//            capture = CAPTURE_ANN;
            if (isValidPieceType(capturingPieceType)) {
                setCapture(capturingPieceType);
            }
        }
        destination = move.getMovingTo().toString();

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
    }

    public void setUniqueStr(String uniqueStr) {
        this.uniqueStr = uniqueStr;

    }

    public void setGameStatus(GameStatus gStatus) {
        gameStatus = gStatus.toString();
    }

    public void setCheck(boolean check) {
        gameStatus = check ? CHECK_ANN : "";
    }

    public void setCapture(int capturingPieceType) {
        capture = CAPTURE_ANN;
    }

    public void overrideEverythingButGameStatus(String str) {
        piece = str;
        capture = destination = promotion = "";
    }

    public void setPromotion(String promotionString) {
        this.promotion = promotionString;
    }

    @Override
    public String toString() {
        return piece + uniqueStr + capture + destination + promotion + gameStatus;
    }
}
