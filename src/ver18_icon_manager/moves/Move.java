package ver18_icon_manager.moves;

import ver18_icon_manager.Board;
import ver18_icon_manager.GameStatus;
import ver18_icon_manager.Location;
import ver18_icon_manager.types.Pawn;
import ver18_icon_manager.types.Piece;


public class Move {
    private Location movingFrom;
    private Location movingTo;
    private boolean isCapturing;
    private String annotation = "";
    private Board board;
    private String moveFEN = "";
    private boolean isReversible = false;
    private int movingPlayer;
    //region for undo move
    private Location prevEnPassantTargetLoc;
    private Location prevEnPassantActualLoc;
    private CastlingAbility prevCastlingAbility;
    //endregion

    public Move(Location movingFrom, Location movingTo, boolean isCapturing, Board board) {
        this.board = board;
        this.movingFrom = new Location(movingFrom);
        this.movingTo = new Location(movingTo);
        this.isCapturing = isCapturing;
        Piece piece = board.getPiece(movingFrom);
        this.movingPlayer = piece == null ? -1 : piece.getPieceColor();
        Location actualLoc = board.getEnPassantActualLoc();
        Location targetLoc = board.getEnPassantTargetLoc();
        if (actualLoc != null && targetLoc != null) {
            prevEnPassantActualLoc = new Location(actualLoc);
            prevEnPassantTargetLoc = new Location(targetLoc);
        }
        prevCastlingAbility = new CastlingAbility(board.getCastlingAbility());
        setReversible();
    }

    /**
     * is capturing = false by default
     *
     * @param movingFrom
     * @param movingTo
     */
    public Move(Location movingFrom, Location movingTo, Board board) {
        this(movingFrom, movingTo, false, board);
    }

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
        System.out.println("DIDNT FIND MOVE FROM TEXT");
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

    private void copyConstructor(Move other) {
        this.board = other.board;
        movingTo = new Location(other.movingTo);
        movingFrom = new Location(other.movingFrom);
        isCapturing = other.isCapturing;
        this.movingPlayer = other.movingPlayer;
        this.prevEnPassantActualLoc = other.prevEnPassantActualLoc;
        this.prevEnPassantTargetLoc = other.prevEnPassantTargetLoc;
        prevCastlingAbility = new CastlingAbility(other.prevCastlingAbility);

        setReversible();
    }

    public boolean isReversible() {
        return isReversible;
    }

    public int getMovingPlayer() {
        //todo ↓
        if (movingPlayer == -1)
            movingPlayer = board.getPiece(movingFrom).getPieceColor();
        return movingPlayer;
    }

    //todo set reversible
    private void setReversible() {
    }

    public boolean equals(Move move) {
        return movingFrom.equals(move.movingFrom) && movingTo.equals(move.movingTo);
    }


    public Board getBoard() {
        return board;
    }

    public String getAnnotation() {
        setAnnotation();
        return annotation;
    }

    private void setAnnotation() {
        Piece piece = board.getPiece(movingFrom);
        if (piece == null)
            piece = board.getPiece(movingTo);
        String pieceAnnotation = piece.getAnnotation();
        String captures = "";
        String additions;

        if (piece instanceof Pawn)
            pieceAnnotation = "";

        if (isCapturing) {
            captures = "x";
            pieceAnnotation = piece.getAnnotation();
        }
        pieceAnnotation += captures;
        GameStatus status = board.getBoardEval().getGameStatus();
        additions = GameStatus.getGameStatusAnnotation(status);
        pieceAnnotation += movingTo.toString();
        String retStr = (pieceAnnotation + additions);

        annotation = retStr;
    }

    public Location getMovingTo() {
        return movingTo;
    }

    public Location getMovingFrom() {
        return movingFrom;
    }

    public boolean isCapturing() {
        return isCapturing;
    }

    public void setCapturing(boolean capturing) {
        isCapturing = capturing;
    }

    @Override
    public String toString() {
        return "Move{" +
                "movingTo=" + movingTo +
                ", movingFrom=" + movingFrom +
                ", isCapturing=" + isCapturing +
                '}';
    }

    public void setFEN() {
        moveFEN = board.getFenStr();
    }

    public String getMoveFEN() {
        return moveFEN;
    }
}