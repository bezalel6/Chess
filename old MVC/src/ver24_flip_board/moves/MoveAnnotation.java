package ver24_flip_board.moves;

import ver24_flip_board.model_classes.GameStatus;

import static ver24_flip_board.model_classes.pieces.Piece.*;

public class MoveAnnotation {
    public static final int PIECE = 0, CAPTURE = 1, DESTINATION = 2, GAME_OVER = 3;
    public static final int WIN_OR_LOSS = 0, TIE = 1, CHECK = 2, GAME_GOES_ON = 3;
    public static final String DRAW = "½½", MATE = "#", CHECK_ANN = "+";
    public static final String[] GAME_STATUS_ANNOTATIONS = {MATE, DRAW, CHECK_ANN, ""};
    private static final String CAPTURE_ANN = "x";

    private String piece, capture, destination, promotion, gameStatus;

    public MoveAnnotation(Move move) {
        int movingPlayer = move.getMovingPlayer();
        int movingPieceType = move.getMovingPieceType();
        int capturingPieceType = move.getCapturingPieceType();
        piece = "";
        if (Player.isValidPlayer(movingPlayer))
            if (movingPieceType == PAWN) {
                if (move.isCapturing())
                    piece = move.getMovingFrom().getColString();
            } else {
                piece = PIECES_FENS[movingPlayer][movingPieceType];
            }
        capture = "";
        if (move.isCapturing()) {
//            capture = CAPTURE_ANN;
            if (isValidPieceType(capturingPieceType)) {
                setCapture(Player.getOpponent(movingPlayer), capturingPieceType);
            }
        }
        destination = move.getMovingTo().toString();

        gameStatus = "";
        promotion = "";
    }

    public MoveAnnotation(MoveAnnotation other) {
        this.piece = other.piece;
        this.capture = other.capture;
        this.gameStatus = other.gameStatus;
        this.destination = other.destination;
        this.promotion = other.promotion;
    }

    public void setGameStatus(GameStatus gStatus) {
        gameStatus = GAME_STATUS_ANNOTATIONS[gStatus.getGameStatusType()];
    }

    public void setCheck(boolean check) {
        gameStatus = check ? CHECK_ANN : "";
    }

    public void setCapture(int capturingPieceColor, int capturingPieceType) {
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
        return piece + capture + destination + promotion + gameStatus;
    }
}
