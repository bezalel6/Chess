package ver17_new_movement.moves;

import ver17_new_movement.Board;
import ver17_new_movement.GameStatus;
import ver17_new_movement.Location;
import ver17_new_movement.types.Pawn;
import ver17_new_movement.types.Piece;


public class Move {
    private Location movingFrom;
    private Location movingTo;
    private Piece movingToPiece;
    private boolean isCapturing;
    private String annotation = "";
    private Board board;
    private String moveFEN = "";
    private boolean isReversible = false;
    private int movingPlayer;

    public Move(Location movingFrom, Location movingTo, boolean isCapturing, Board board) {
        this.board = board;
        this.movingFrom = new Location(movingFrom);
        this.movingTo = new Location(movingTo);
        this.isCapturing = isCapturing;
        this.movingToPiece = board.getPiece(movingTo);
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


    public Move(Move other, boolean b) {
        this.board = other.board;
        movingTo = new Location(other.movingTo);
        movingFrom = new Location(other.movingFrom);
        isCapturing = other.isCapturing;
        setReversible();
    }

    public Piece getMovingToPiece() {
        return movingToPiece;
    }

    public void setMovingToPiece(Piece movingToPiece) {
        this.movingToPiece = movingToPiece;
    }

    private void copyConstructor(Move other) {
        this.board = other.board;
        movingTo = new Location(other.movingTo);
        movingFrom = new Location(other.movingFrom);
        isCapturing = other.isCapturing;
        this.movingPlayer = other.movingPlayer;
        setReversible();
    }

    public boolean isReversible() {
        return isReversible;
    }

    public int movingPlayer() {
        return board.getPiece(movingFrom).getPieceColor();
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