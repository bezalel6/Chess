package ver34_faster_move_generation.model_classes.moves;

import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.Player;
import ver34_faster_move_generation.model_classes.Board;
import ver34_faster_move_generation.model_classes.BoardHash;
import ver34_faster_move_generation.model_classes.CastlingAbility;
import ver34_faster_move_generation.model_classes.eval_classes.Evaluation;
import ver34_faster_move_generation.model_classes.pieces.Piece;
import ver34_faster_move_generation.model_classes.pieces.Rook;

import java.util.ArrayList;
import java.util.Objects;


public class Move extends BasicMove implements Comparable<Move> {
    public static final int NOT_CAPTURING = -1;
    public static final int TEMP_CAPTURING = -2;
    MoveAnnotation moveAnnotation;
    BasicMove intermediateMove;
    private int capturingPieceType;
    private Location enPassantLoc;
    private int promotingTo;
    //    private ArrayList<Integer[]> disableCastling;
    private boolean isReversible;
    private Evaluation moveEvaluation;
    private MoveFlag moveFlag;


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

    //todo is this ok? should i b do the same thing i did in copy move?
    public Move(Move other) {
        super(other);
        this.capturingPieceType = other.capturingPieceType;
        if (other.intermediateMove != null)
            this.intermediateMove = new BasicMove(other.intermediateMove);
        this.promotingTo = other.promotingTo;
        this.moveFlag = other.moveFlag;
        this.moveAnnotation = new MoveAnnotation(other.moveAnnotation);
        this.isReversible = other.isReversible;
        this.enPassantLoc = other.enPassantLoc;
        if (other.moveEvaluation != null)
            this.moveEvaluation = new Evaluation(other.moveEvaluation);
    }

    public static Move copyMove(Move move) {
        if (move == null)
            return null;
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

    //todo set reversible
    public boolean isReversible() {
        return isReversible;
    }

//    private void setReversible() {
//        isReversible = !isCapturing() && movingPieceType != Piece.PAWN && disableCastling.isEmpty();
//    }

//
//    private void setDisableCastling(Board board) {
//        CastlingAbility castlingAbility = board.getCastlingAbility();
//        int opponent = Player.getOpponent(movingPlayer);
//        disableCastling = new ArrayList<>();
//        if (castlingAbility.checkAny(movingPlayer) || castlingAbility.checkAny(opponent)) {
//            for (int player : Player.PLAYERS) {
//                int pieceType = player == movingPlayer ? movingPieceType : capturingPieceType;
//                Location loc = player == movingPlayer ? movingFrom : movingTo;
//                if (castlingAbility.checkAny(player)) {
//                    if (pieceType == Piece.KING) {
//                        disableCastling.add(new Integer[]{player});
//                    } else if (pieceType == Piece.ROOK) {
//                        Piece piece = board.getPiece(loc, true);
//                        if (!piece.getHasMoved() && piece instanceof Rook) {
//                            Rook rook = (Rook) piece;
//                            disableCastling.add(new Integer[]{player, rook.getSideRelativeToKing(board)});
//                        }
//                    }
//                }
//            }
//        }
//    }


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

    public enum MoveFlag {NormalMove, EnPassant, DoublePawnPush, Promotion}

}