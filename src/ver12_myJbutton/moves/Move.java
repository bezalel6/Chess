package ver12_myJbutton.moves;

import ver12_myJbutton.Board;
import ver12_myJbutton.GameStatus;
import ver12_myJbutton.Location;
import ver12_myJbutton.types.Pawn;
import ver12_myJbutton.types.Piece;


public class Move {
    private Location movingFrom;
    private Location movingTo;
    private Piece movingFromPiece, movingToPiece;
    private boolean isCapturing;
    private String annotation = "";
    private Board board;

    public Move(Location movingFrom, Location movingTo, boolean isCapturing, Board board) {
        this.board = board;
        this.movingFrom = new Location(movingFrom);
        this.movingTo = new Location(movingTo);
        this.isCapturing = isCapturing;
        this.movingFromPiece = Piece.copyPiece(board.getPiece(movingFrom));
        this.movingToPiece = Piece.copyPiece(board.getPiece(movingTo));
    }

    public Move(Move other) {
        this.board = other.board;
        movingTo = new Location(other.movingTo);
        movingFrom = new Location(other.movingFrom);
        isCapturing = other.isCapturing;
        this.movingFromPiece = Piece.copyPiece(other.movingFromPiece);
        this.movingToPiece = Piece.copyPiece(other.movingToPiece);
    }

    public Move(String text, Board board) {
        for (Move move : board.getAllMovesForCurrentPlayer()) {
            if (move.getAnnotation().equalsIgnoreCase(text)) {
                this.board = board;
                movingTo = new Location(move.movingTo);
                movingFrom = new Location(move.movingFrom);
                isCapturing = move.isCapturing;
                this.movingFromPiece = Piece.copyPiece(board.getPiece(movingFrom));
                this.movingToPiece = Piece.copyPiece(board.getPiece(movingTo));
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
}
