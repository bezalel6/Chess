package ver34_faster_move_generation.model_classes.moves;

import ver34_faster_move_generation.Player;
import ver34_faster_move_generation.model_classes.GameStatus;
import ver34_faster_move_generation.model_classes.pieces.Piece;

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
        int capturingPieceType = move.getCapturingPieceType();
        piece = "";

        capture = "";
        if (move.isCapturing()) {
//            capture = CAPTURE_ANN;
            if (Piece.isValidPieceType(capturingPieceType)) {
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

    public void setDetailedAnnotation(int movingPieceType) {
        if (!overriding) {

            if (movingPieceType == Piece.PAWN) {
                if (move.isCapturing())
                    piece = move.getMovingFrom().getColString();
            } else {
                piece = Piece.PIECES_FENS[Player.WHITE][movingPieceType];
            }
        }
    }

    public void setUniqueStr(String uniqueStr) {
        this.uniqueStr = uniqueStr;
    }

    public void setGameStatus(GameStatus gStatus) {
        switch (gStatus.getGameStatusType()) {
            case TIE:
                gameStatus = DRAW;
                break;
            case CHECK:
                gameStatus = CHECK_ANN;
                break;
            case WIN_OR_LOSS:
                gameStatus = MATE;
                break;
            default:
                gameStatus = "";
                break;

        }
    }

    public void setCheck(boolean check) {
        gameStatus = check ? CHECK_ANN : "";
    }

    public void setCapture(int capturingPieceType) {
        capture = CAPTURE_ANN;
    }

    public void overrideEverythingButGameStatus(String str) {
        overriding = true;
        piece = str;
        capture = destination = promotion = "";
    }

    public void setPromotion(int promotingTo) {
        this.promotion = "=" + Piece.PIECES_FENS[0][promotingTo].charAt(0) + "";
    }

    @Override
    public String toString() {
        return piece + uniqueStr + capture + destination + promotion + gameStatus;
    }
}
