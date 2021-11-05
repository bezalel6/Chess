package ver34_faster_move_generation.model_classes.moves;

import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.model_classes.Board;
import ver34_faster_move_generation.model_classes.eval_classes.Evaluation;
import ver34_faster_move_generation.model_classes.pieces.Pawn;
import ver34_faster_move_generation.model_classes.pieces.Piece;

import java.util.Objects;


public class Move extends BasicMove implements Comparable<Move> {
    public static final int NOT_CAPTURING = -1;
    public static final int TEMP_CAPTURING = -2;
    MoveAnnotation moveAnnotation;
    BasicMove intermediateMove;
    private int capturingPieceType;
    private Location enPassantLoc;
    private int promotingTo;
    private boolean isReversible;
    private Evaluation moveEvaluation;
    private MoveFlag moveFlag;
    private int prevCastlingAbility;


    public Move(Location movingFrom, Location movingTo, int capturingPieceType) {
        this(movingFrom, movingTo);
        this.capturingPieceType = capturingPieceType;
    }

    public Move(Location movingFrom, Location movingTo) {
        super(movingFrom, movingTo);
        this.capturingPieceType = NOT_CAPTURING;
        this.moveAnnotation = new MoveAnnotation(this);
        this.intermediateMove = null;
        this.promotingTo = -1;
        this.moveFlag = MoveFlag.NormalMove;
    }

    public Move(Move other) {
        super(other);

        this.capturingPieceType = other.capturingPieceType;
        this.prevCastlingAbility = other.prevCastlingAbility;
        this.promotingTo = other.promotingTo;
        this.moveFlag = other.moveFlag;
        this.isReversible = other.isReversible;

        if (other.enPassantLoc != null)
            this.enPassantLoc = new Location(enPassantLoc);
        if (other.moveEvaluation != null)
            this.moveEvaluation = new Evaluation(other.moveEvaluation);
        if (other.intermediateMove != null)
            this.intermediateMove = new BasicMove(other.intermediateMove);

        this.moveAnnotation = new MoveAnnotation(other.moveAnnotation);

    }

    public static Move copyMove(Move move) {
        if (move instanceof Castling)
            return new Castling((Castling) move);
        else
            return new Move(move);
    }

    private static double guessEval(Move move) {
        double ret = 0;
//        int capturingPieceType = move.getCapturingPieceType();
//        int movingPieceType = move.getMovingPieceType();
//        if (Piece.isValidPieceType(capturingPieceType)) {
//            ret += 10 * Piece.getPieceWorth(movingPieceType) - Piece.getPieceWorth(capturingPieceType);
//        }
        if (move.moveFlag == MoveFlag.Promotion) {
            ret += 5 * Piece.getPieceWorth(move.promotingTo);
        } else if (move.moveFlag == MoveFlag.EnPassant) {
            ret += 0.00001;
        } else if (move instanceof Castling) {
            ret += 10;
        }
        ret -= 100000;
        return ret;
    }

    public int getPromotingTo() {
        return promotingTo;
    }

    public void setPromotingTo(int promotingTo) {
        this.promotingTo = promotingTo;
    }

    public MoveFlag getMoveFlag() {
        return moveFlag;
    }

    public void setMoveFlag(MoveFlag moveFlag) {
        this.moveFlag = moveFlag;
    }
    //endregion

    public BasicMove getIntermediateMove() {
        return intermediateMove;
    }

    public void setIntermediateMove(BasicMove intermediateMove) {
        this.intermediateMove = intermediateMove;
    }

    public boolean isCheck() {
        return moveEvaluation != null && moveEvaluation.isCheck();
    }

    public Evaluation getMoveEvaluation() {
        return moveEvaluation;
    }

    public void setMoveEvaluation(Evaluation moveEvaluation) {
        this.moveEvaluation = new Evaluation(moveEvaluation);
        moveAnnotation.setGameStatus(moveEvaluation.getGameStatus());
    }

    public int getCapturingPieceType() {
        return capturingPieceType;
    }

    public boolean isReversible() {
        return isReversible;
    }

    public void setReversible(Piece movingPiece) {
        isReversible = !(isCapturing() || movingPiece instanceof Pawn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return capturingPieceType == move.capturingPieceType && promotingTo == move.promotingTo && isReversible == move.isReversible && Objects.equals(moveAnnotation, move.moveAnnotation) && Objects.equals(intermediateMove, move.intermediateMove) && Objects.equals(enPassantLoc, move.enPassantLoc) && Objects.equals(moveEvaluation, move.moveEvaluation) && moveFlag == move.moveFlag;
    }

    public MoveAnnotation getMoveAnnotation() {
        return moveAnnotation;
    }

    public String getAnnotation() {
        return moveAnnotation.toString();
    }

    public boolean isCapturing() {
        return capturingPieceType != NOT_CAPTURING;
    }

    public void setCapturing(Piece piece, Board board) {
        int capturingPieceColor = piece.getPieceColor();
        this.capturingPieceType = piece.getPieceType();
        moveAnnotation.setCapture(capturingPieceType);
    }

    @Override
    public String toString() {
        return getAnnotation();
    }

    @Override
    public int compareTo(Move o) {
        if (moveEvaluation != null) {
            if (o.moveEvaluation != null) {
                return Double.compare(moveEvaluation.getEval(), o.moveEvaluation.getEval());
            }
            return 1;
        }
        if (o.moveEvaluation != null) {
            return -1;
        }
        return Double.compare(guessEval(this), guessEval(o));
    }

    public Location getEnPassantLoc() {
        return enPassantLoc;
    }

    public void setEnPassantLoc(Location epsnLoc) {
        enPassantLoc = epsnLoc;
    }

    public int getPrevCastlingAbility() {
        return prevCastlingAbility;
    }

    public void setPrevCastlingAbility(int prevCastlingAbility) {
        this.prevCastlingAbility = prevCastlingAbility;
    }

    public enum MoveFlag {NormalMove, EnPassant, DoublePawnPush, Promotion}

}