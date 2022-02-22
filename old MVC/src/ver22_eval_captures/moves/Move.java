package ver22_eval_captures.moves;

import ver22_eval_captures.Error;
import ver22_eval_captures.Location;
import ver22_eval_captures.model_classes.Board;
import ver22_eval_captures.model_classes.CastlingAbility;
import ver22_eval_captures.types.Pawn;
import ver22_eval_captures.types.Piece;

import java.util.ArrayList;
import java.util.Objects;


public class Move {
    public static final int NOT_CAPTURING_HASH = 999999;
    public static final int TEMP_CAPTURING_HASH = -999999;

    private Location movingFrom;
    private Location movingTo;

    private String annotation = "";
    private String moveFEN = "";

    private Board board;
    private Piece movingPiece;
    private int capturingPieceHash = NOT_CAPTURING_HASH;

    //region for undo move
    private Location prevEnPassantTargetLoc;
    private Location prevEnPassantActualLoc;
    private CastlingAbility prevCastlingAbility;
    private ArrayList<String> prevRepetitionFenList;
    private int prevHalfMoveClock;
    //endregion

    public Move(Location movingFrom, Location movingTo, int capturingPieceHash, Board board) {
        this.board = board;

        this.capturingPieceHash = capturingPieceHash;
        this.prevHalfMoveClock = board.getHalfMoveClock();

        this.movingPiece = board.getPiece(movingFrom);

        this.movingFrom = new Location(movingFrom);
        this.movingTo = new Location(movingTo);
        Location actualLoc = board.getEnPassantActualLoc();
        Location targetLoc = board.getEnPassantTargetLoc();
        if (actualLoc != null && targetLoc != null) {
            this.prevEnPassantActualLoc = new Location(actualLoc);
            this.prevEnPassantTargetLoc = new Location(targetLoc);
        }

        this.prevRepetitionFenList = new ArrayList<>(board.getRepetitionFenList());
        this.prevCastlingAbility = new CastlingAbility(board.getCastlingAbility());
    }

    public Move(Location movingFrom, Location movingTo, Board board) {
        this(movingFrom, movingTo, NOT_CAPTURING_HASH, board);
    }

    public Move(Location movingFrom, int r, int c, Board board) {
        this(movingFrom, new Location(r, c), NOT_CAPTURING_HASH, board);
    }

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
        Error.error("DIDNT FIND MOVE FROM TEXT");
    }

    public Move(Move move, int capturingPieceHash) {
        this(move);
        this.capturingPieceHash = capturingPieceHash;
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

    public ArrayList<String> getPrevRepetitionFenList() {
        return prevRepetitionFenList;
    }

    public int getPrevHalfMoveClock() {
        return prevHalfMoveClock;
    }

    public int getCapturingPieceHash() {
        return capturingPieceHash;
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
        this.board = other.board;
        this.movingTo = new Location(other.movingTo);
        this.movingFrom = new Location(other.movingFrom);
        if (other.prevEnPassantActualLoc != null && other.prevEnPassantTargetLoc != null) {
            this.prevEnPassantActualLoc = new Location(other.prevEnPassantActualLoc);
            this.prevEnPassantTargetLoc = new Location(other.prevEnPassantTargetLoc);
        }
        this.annotation = other.annotation;
        this.movingPiece = Piece.copyPiece(other.movingPiece);
        this.prevHalfMoveClock = other.prevHalfMoveClock;
        this.capturingPieceHash = other.capturingPieceHash;

        this.prevRepetitionFenList = new ArrayList<>(other.prevRepetitionFenList);
        this.prevCastlingAbility = new CastlingAbility(other.prevCastlingAbility);
    }

    public int getMovingPlayer() {
        //todo ↓
        return getMovingPiece().getPieceColor();
    }

    private Piece getMovingPiece() {
        if (movingPiece == null)
            movingPiece = board.getPiece(movingFrom);
        if (movingPiece == null)
            movingPiece = board.getPieceNotNull(movingTo);
        return movingPiece;
    }

    //todo set reversible
    public boolean isReversible() {
        return !isCapturing() && !(getMovingPiece() instanceof Pawn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return capturingPieceHash == move.capturingPieceHash && movingFrom.equals(move.movingFrom) && movingTo.equals(move.movingTo) && movingPiece.equals(move.movingPiece);
    }

    public Board getBoard() {
        return board;
    }

    public String getBasicMoveAnnotation() {
        return movingFrom.toString() + movingTo.toString();
    }

    public String getAnnotation() {
        if (!Objects.equals(annotation, ""))
            return annotation;
        if (movingPiece == null)
            return getBasicMoveAnnotation();
        movingPiece = getMovingPiece();
        String pieceAnnotation = movingPiece.getAnnotation();
        String captures = "";
        String additions;

        if (movingPiece instanceof Pawn)
            pieceAnnotation = "";

        if (isCapturing()) {
            captures = "x";
            pieceAnnotation = movingPiece.getAnnotation();
        }
        pieceAnnotation += captures;
//        GameStatus status = board.getBoardEval().getGameStatus();
//        additions = GameStatus.getGameStatusAnnotation(status);
        pieceAnnotation += movingTo.toString();
//        annotation = (pieceAnnotation + additions);
        annotation = (pieceAnnotation);
        return annotation;
    }

    public Location getMovingTo() {
        return movingTo;
    }

    public Location getMovingFrom() {
        return movingFrom;
    }

    public boolean isCapturing() {
        return capturingPieceHash != NOT_CAPTURING_HASH;
    }

    public void setCapturing(int capturingPieceHash) {
        this.capturingPieceHash = capturingPieceHash;
    }

    @Override
    public String toString() {
        return getAnnotation();
    }

    public void setFEN() {
        moveFEN = board.getFenStr();
    }

    public String getMoveFEN() {
        return moveFEN;
    }
}