package ver23_minimax_levels.moves;

import ver23_minimax_levels.MyError;
import ver23_minimax_levels.Location;
import ver23_minimax_levels.model_classes.Board;
import ver23_minimax_levels.model_classes.CastlingAbility;
import ver23_minimax_levels.model_classes.eval_classes.Evaluation;
import ver23_minimax_levels.types.King;
import ver23_minimax_levels.types.Piece;
import ver23_minimax_levels.types.Rook;

import java.util.ArrayList;
import java.util.Objects;


public class Move {
    public static final int NOT_CAPTURING = -1;
    public static final int TEMP_CAPTURING = -2;
    MoveAnnotation moveAnnotation;
    private Location movingFrom;
    private Location movingTo;
    private int capturingPieceType = NOT_CAPTURING;
    private int capturingPieceIndex;
    private int movingPlayer;
    private int movingPieceType;
    private boolean initialize = true;
    private ArrayList<Integer[]> disableCastling;
    private boolean isReversible;
    private Evaluation moveEvaluation;
    //region for undo move
    private Location prevEnPassantTargetLoc;
    private Location prevEnPassantActualLoc;
    private CastlingAbility prevCastlingAbility;
    private ArrayList<Long> prevRepetitionHashList;
    private int prevHalfMoveClock;
    private Long prevBoardHash = null;

    public Move(Location movingFrom, Location movingTo, int movingPlayer, int movingPieceType, int capturingPieceType) {
        this(movingFrom, movingTo, movingPlayer, movingPieceType, capturingPieceType, null);
    }

    public Move(Location movingFrom, Location movingTo, int movingPlayer, int movingPieceType, int capturingPieceType, Board board) {
        assert Piece.checkValidPieceType(movingPieceType);
        assert capturingPieceType == NOT_CAPTURING || capturingPieceType == TEMP_CAPTURING || Piece.checkValidPieceType(capturingPieceType);
        this.movingPlayer = movingPlayer;
        this.movingPieceType = movingPieceType;
        this.capturingPieceType = capturingPieceType;
        this.movingFrom = new Location(movingFrom);
        this.movingTo = new Location(movingTo);
        moveAnnotation = new MoveAnnotation(this);
        if (board != null)
            fullyInitialize(board);
    }

    public Move(Location movingFrom, Location movingTo, int movingPlayer, int movingPieceType, boolean initialize) {
        this(movingFrom, movingTo, movingPlayer, movingPieceType);
        this.initialize = initialize;
    }

    public Move(Location movingFrom, Location movingTo, int movingPlayer, int movingPieceType) {
        this(movingFrom, movingTo, movingPlayer, movingPieceType, null);
    }

    public Move(Location movingFrom, Location movingTo, int movingPlayer, int movingPieceType, Board board) {
        this(movingFrom, movingTo, movingPlayer, movingPieceType, true, board);
    }

    public Move(Location movingFrom, Location movingTo, int movingPlayer, int movingPieceType, boolean initialize, Board board) {
        this(movingFrom, movingTo, movingPlayer, movingPieceType, NOT_CAPTURING, board);
        this.initialize = initialize;
    }
    //endregion

    //todo is this ok? should i b do the same thing i did in copy move?
    public Move(Move other) {
        copyConstructor(other);
    }

    public Move(String text, Board board) {
        for (Move move : board.getAllMovesForCurrentPlayer()) {
            if (move.getAnnotation().equalsIgnoreCase(text)) {
                copyConstructor(move);
                return;
            }
        }
        MyError.error("DIDNT FIND MOVE FROM TEXT");
    }

    public static Move copyMove(Move move) {
        if (move == null)
            return null;
        if (move instanceof Castling)
            return new Castling((Castling) move);
        else if (move instanceof DoublePawnPush)
            return new DoublePawnPush((DoublePawnPush) move);
        else if (move instanceof EnPassant)
            return new EnPassant((EnPassant) move);
        else if (move instanceof PromotionMove)
            return new PromotionMove((PromotionMove) move);
        else
            return new Move(move);
    }

    public void fullyInitialize(Board board) {
        if (initialize) {
            this.prevHalfMoveClock = board.getHalfMoveClock();
            Location actualLoc = board.getEnPassantActualLoc();
            Location targetLoc = board.getEnPassantTargetLoc();
            if (actualLoc != null && targetLoc != null) {
                this.prevEnPassantActualLoc = new Location(actualLoc);
                this.prevEnPassantTargetLoc = new Location(targetLoc);
            }

            this.prevRepetitionHashList = new ArrayList<>(board.getRepetitionHashList());
            this.prevCastlingAbility = new CastlingAbility(board.getCastlingAbility());
//            moveAnnotation = new MoveAnnotation(this);
            setDisableCastling(board);
            setReversible();
        }
    }

    public boolean isCheck() {
        return moveEvaluation != null && moveEvaluation.isGameOver();
    }

    public Evaluation getMoveEvaluation() {
        return moveEvaluation;
    }

    public void setMoveEvaluation(Evaluation moveEvaluation) {
        this.moveEvaluation = moveEvaluation;
        moveEvaluation.setGameStatus(moveEvaluation.getGameStatus());
    }

    public boolean isBoardHashSet() {
        return prevBoardHash != null;
    }

    public long getPrevBoardHash() {
        return prevBoardHash;
    }

    public void setPrevBoardHash(long prevBoardHash) {
        this.prevBoardHash = prevBoardHash;
    }

    public ArrayList<Long> getPrevRepetitionHashList() {
        return prevRepetitionHashList;
    }

    public int getPrevHalfMoveClock() {
        return prevHalfMoveClock;
    }

    public int getCapturingPieceType() {
        return capturingPieceType;
    }

    public CastlingAbility getPrevCastlingAbility() {
        return prevCastlingAbility;
    }

    public Location getPrevEnPassantTargetLoc() {
        return prevEnPassantTargetLoc;
    }

    public void setPrevEnPassantTargetLoc(Location prevEnPassantTargetLoc) {
        this.prevEnPassantTargetLoc = new Location(prevEnPassantTargetLoc);
    }

    public Location getPrevEnPassantActualLoc() {
        return prevEnPassantActualLoc;
    }

    public void setPrevEnPassantActualLoc(Location prevEnPassantActualLoc) {
        this.prevEnPassantActualLoc = new Location(prevEnPassantActualLoc);
    }

    final void copyConstructor(Move other) {
        this.movingTo = new Location(other.movingTo);
        this.movingFrom = new Location(other.movingFrom);
        if (other.prevEnPassantActualLoc != null && other.prevEnPassantTargetLoc != null) {
            this.prevEnPassantActualLoc = new Location(other.prevEnPassantActualLoc);
            this.prevEnPassantTargetLoc = new Location(other.prevEnPassantTargetLoc);
        }
        this.prevHalfMoveClock = other.prevHalfMoveClock;
        this.capturingPieceType = other.capturingPieceType;
        this.capturingPieceIndex = other.capturingPieceIndex;
        this.prevBoardHash = other.prevBoardHash;
        this.movingPlayer = other.movingPlayer;
        this.movingPieceType = other.movingPieceType;

        this.prevRepetitionHashList = new ArrayList<>(other.prevRepetitionHashList);
        this.prevCastlingAbility = new CastlingAbility(other.prevCastlingAbility);
        this.moveAnnotation = new MoveAnnotation(other.moveAnnotation);
        this.isReversible = other.isReversible;
        this.disableCastling = other.disableCastling;
        this.initialize = other.initialize;
        if (other.moveEvaluation != null)
            this.moveEvaluation = new Evaluation(other.moveEvaluation);
    }

    public int getMovingPlayer() {
        return movingPlayer;
    }

    //todo set reversible
    public boolean isReversible() {
        return isReversible;
    }

    private void setReversible() {
        isReversible = !isCapturing() && movingPieceType != Piece.PAWN && disableCastling.isEmpty();
    }

    public ArrayList<Integer[]> getDisableCastling() {
        return disableCastling;
    }

    private void setDisableCastling(Board board) {
        CastlingAbility castlingAbility = board.getCastlingAbility();
        int opponent = Piece.Player.getOpponent(movingPlayer);
        disableCastling = new ArrayList<>();
        if (castlingAbility.checkAny(movingPlayer) || castlingAbility.checkAny(opponent)) {
            for (int player : Piece.Player.PLAYERS) {
                int pieceType = player == movingPlayer ? movingPieceType : capturingPieceType;
                Location loc = player == movingPlayer ? movingFrom : movingTo;
                if (castlingAbility.checkAny(player)) {
                    if (pieceType == Piece.KING) {
                        disableCastling.add(new Integer[]{player});
                    } else if (pieceType == Piece.ROOK) {
                        Piece piece = board.getPiece(loc, true);
                        if (!piece.getHasMoved() && piece instanceof Rook) {
//                            assert piece instanceof Rook;
                            Rook rook = (Rook) piece;
                            disableCastling.add(new Integer[]{player, rook.getSideRelativeToKing(board)});
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return capturingPieceType == move.capturingPieceType && movingPlayer == move.movingPlayer && movingPieceType == move.movingPieceType && prevHalfMoveClock == move.prevHalfMoveClock && Objects.equals(movingFrom, move.movingFrom) && Objects.equals(movingTo, move.movingTo) && Objects.equals(prevEnPassantTargetLoc, move.prevEnPassantTargetLoc) && Objects.equals(prevEnPassantActualLoc, move.prevEnPassantActualLoc) && Objects.equals(prevCastlingAbility, move.prevCastlingAbility) && Objects.equals(prevRepetitionHashList, move.prevRepetitionHashList);
    }


    public String getAnnotation() {
        return moveAnnotation.toString();
    }

    public int getMovingPieceType() {
        return movingPieceType;
    }


    public Location getMovingTo() {
        return movingTo;
    }

    public Location getMovingFrom() {
        return movingFrom;
    }

    public boolean isCapturing() {
        return capturingPieceType != NOT_CAPTURING;
    }

    public int getCapturingPieceIndex() {
        return capturingPieceIndex;
    }

    public void setCapturing(Piece piece, Board board) {
        int capturingPieceColor = piece.getPieceColor();
        this.capturingPieceIndex = board.getPlayersPieces(capturingPieceColor).indexOf(piece);
        this.capturingPieceType = piece.getPieceType();
        moveAnnotation.setCapture(capturingPieceColor, capturingPieceType);
    }

    @Override
    public String toString() {
        return getAnnotation();
    }

}