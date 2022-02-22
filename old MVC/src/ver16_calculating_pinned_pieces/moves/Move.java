package ver16_calculating_pinned_pieces.moves;

import ver16_calculating_pinned_pieces.Board;
import ver16_calculating_pinned_pieces.GameStatus;
import ver16_calculating_pinned_pieces.Location;
import ver16_calculating_pinned_pieces.types.Pawn;
import ver16_calculating_pinned_pieces.types.Piece;


public class Move {
    private Location movingFrom;
    private Location movingTo;
    private Piece movingFromPiece, movingToPiece;
    private boolean isCapturing;
    private String annotation = "";
    private Board board;
    private String moveFEN = "";
    private boolean isReversible;

    /**
     * FOR TESTING ONLY
     */
    public Move() {
        board = null;
    }

    public Move(Location movingFrom, Location movingTo, boolean isCapturing, Board board) {
        this.board = board;
        this.movingFrom = new Location(movingFrom);
        this.movingTo = new Location(movingTo);
        this.isCapturing = isCapturing;
//        this.movingFromPiece = Piece.copyPiece(board.getPiece(movingFrom));
//        this.movingToPiece = Piece.copyPiece(board.getPiece(movingTo));
        setReversible();
    }

    public Move(Move other) {
        this.board = other.board;
        movingTo = new Location(other.movingTo);
        movingFrom = new Location(other.movingFrom);
        isCapturing = other.isCapturing;
//        this.movingFromPiece = Piece.copyPiece(other.movingFromPiece);
//        this.movingToPiece = Piece.copyPiece(other.movingToPiece);
        setReversible();
    }

    public Move(String text, Board board) {
        for (Move move : board.getAllMovesForCurrentPlayer()) {
            if (move.getAnnotation().equalsIgnoreCase(text)) {
                this.board = board;
                movingTo = new Location(move.movingTo);
                movingFrom = new Location(move.movingFrom);
                isCapturing = move.isCapturing;
//                this.movingFromPiece = Piece.copyPiece(board.getPiece(movingFrom));
//                this.movingToPiece = Piece.copyPiece(board.getPiece(movingTo));
                setReversible();
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
        this.movingFromPiece = other.movingFromPiece;
        this.movingToPiece = other.movingToPiece;
        setReversible();
    }

    public boolean isReversible() {
        return isReversible;
    }

    public int movingPlayer() {
        return movingFromPiece.getPieceColor();
    }

    private void setReversible() {
        isReversible = !(isCapturing || movingFromPiece instanceof Pawn || this instanceof Castling);
    }

    public boolean equals(Move move) {
        return movingFrom.equals(move.movingFrom) && movingTo.equals(move.movingTo);
    }

    public Piece getMovingFromPiece() {
        return movingFromPiece;
    }

    public void setMovingFromPiece(Piece movingFromPiece) {
        this.movingFromPiece = movingFromPiece;
    }

    public Piece getMovingToPiece() {
        return movingToPiece;
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
        moveFEN = board.getFen();
    }

    public String getMoveFEN() {
        return moveFEN;
    }
}
