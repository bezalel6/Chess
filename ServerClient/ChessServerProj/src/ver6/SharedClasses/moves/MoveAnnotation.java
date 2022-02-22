package ver6.SharedClasses.moves;

import ver6.SharedClasses.StrUtils;
import ver6.SharedClasses.evaluation.GameStatus;
import ver6.SharedClasses.pieces.PieceType;

import java.io.Serializable;

public class MoveAnnotation implements Serializable {
    public static final int PIECE = 0, CAPTURE = 1, DESTINATION = 2, GAME_OVER = 3;

    private static final String CAPTURE_ANN = "x";

    private GameStatus gameStatus;
    private String piece = "";
    private String capture = "";
    private String destination = "";
    private String promotion = "";
    private String uniqueStr = "";
    private boolean overriding;

    public MoveAnnotation(Move move) {
        if (move.getThreefoldStatus() == Move.ThreefoldStatus.CLAIMED) {
            gameStatus = GameStatus.threeFoldRepetition();
            return;
        }

        PieceType capturingPieceType = move.getCapturingPieceType();
        if (move.isCapturing()) {
            if (capturingPieceType != null) {
                setCapture(capturingPieceType);
            }
        }
        destination = move.getMovingTo().toString();
        overriding = false;
        gameStatus = GameStatus.gameGoesOn();
    }

    public void setCapture(PieceType capturingPieceType) {
        capture = CAPTURE_ANN;
    }

    public MoveAnnotation(MoveAnnotation other) {
        this.piece = other.piece;
        this.capture = other.capture;
        this.gameStatus = other.gameStatus;
        this.destination = other.destination;
        this.promotion = other.promotion;
        this.uniqueStr = other.uniqueStr;
        this.overriding = other.overriding;
    }

    public void setDetailedAnnotation(Move move, PieceType movingPieceType) {
        if (!overriding) {
            if (movingPieceType == PieceType.PAWN) {
                if (move.isCapturing())
                    piece = move.getMovingFrom().getColString();
            } else {
                piece = movingPieceType.getWhitePieceFen();
            }
        }
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setUniqueStr(String uniqueStr) {
        this.uniqueStr = uniqueStr;
    }

    public void overrideEverythingButGameStatus(String str) {
        overriding = true;
        piece = str;
        capture = destination = promotion = "";
    }

    public void resetPromotion(Move move) {
        setPromotion(move.getPromotingTo());
    }

    public void setPromotion(PieceType promotingTo) {
        if (promotingTo != null)
            this.promotion = "=" + promotingTo.getWhitePieceFen();
    }

    public String getDetailedStr() {
        String gStatusStr = gameStatus == null ? "" : " " + gameStatus.getDetailedStr();
        return piece + uniqueStr + capture + destination + promotion + gStatusStr;
    }

    @Override
    public String toString() {
        String gStatusStr = gameStatus == null ? "" : " " + gameStatus;
        return StrUtils.dontCapWord(piece + uniqueStr + capture + destination + promotion + gStatusStr);
    }
}
