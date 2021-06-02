package ver11_board_class.moves;

import ver11_board_class.GameStatus;
import ver11_board_class.types.Pawn;
import ver11_board_class.types.Piece;
import ver11_board_class.Board;
import ver11_board_class.Location;


public class Move {
    private Location movingFrom;
    private Location movingTo;
    private boolean isCapturing;
    private SpecialMove specialMove;
    private String annotation = "";
    private Board board;

    public Move(Location movingFrom, Location movingTo, boolean isCapturing, Board board) {
        this.board = board;
        this.movingFrom = new Location(movingFrom);
        this.movingTo = new Location(movingTo);
        this.isCapturing = isCapturing;
        specialMove = null;
    }

    public Move(Move other, Board board) {
        this.board = new Board(board, other.board.getModel());
        movingTo = new Location(other.movingTo);
        movingFrom = new Location(other.movingFrom);
        isCapturing = other.isCapturing;
    }

    public Move(Move other, String annotation, Board board) {
        this.board = board;
        movingTo = new Location(other.movingTo);
        movingFrom = new Location(other.movingFrom);
        isCapturing = other.isCapturing;
        this.annotation = annotation;
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
        String additions = "";

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
