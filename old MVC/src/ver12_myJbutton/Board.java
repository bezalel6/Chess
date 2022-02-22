package ver12_myJbutton;

import ver12_myJbutton.moves.Castling;
import ver12_myJbutton.moves.EnPassant;
import ver12_myJbutton.moves.PromotionMove;
import ver12_myJbutton.types.Piece.Player;
import ver12_myJbutton.types.Piece.types;
import ver12_myJbutton.moves.Move;
import ver12_myJbutton.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static ver12_myJbutton.Model.ANSI_BLACK;
import static ver12_myJbutton.Model.ANSI_WHITE;

public class Board implements Iterable<Piece[]> {
    public static final String ANSI_RESET = "\u001B[0m";
    private Piece[][] logicMat;
    private int rows, cols;
    private Eval boardEval;
    private Player currentPlayer;
    private Model model;
    private String knight = "♘", bishop = "♗", pawn = "♙", king = "♔", queen = "♕", rook = "♖";
    private ArrayList<Move> movesList;
    private int currentMoveIndex;

    public Board(int rows, int cols, Model model) {
        this.rows = rows;
        this.cols = cols;
        logicMat = new Piece[rows][cols];
        this.model = model;
        boardEval = new Eval(this);
        movesList = new ArrayList<>();
        currentMoveIndex = 0;
    }

    public BoardEval getBoardEval(Player player) {
        if (boardEval == null)
            boardEval = new Eval(this);
        return boardEval.getEvaluation(player);
    }

    public BoardEval getBoardEval() {
        if (boardEval == null)
            boardEval = new Eval(this);
        return boardEval.getEvaluation(currentPlayer);
    }

    public Piece[][] getMat() {
        return logicMat;
    }

    public void setPiece(Location loc, Piece piece) {
        logicMat[loc.getRow()][loc.getCol()] = piece;
    }

    public Piece getPiece(int row, int col) {
        return logicMat[row][col];
    }

    public Piece getPiece(Location loc) {
        return logicMat[loc.getRow()][loc.getCol()];
    }

    private Piece[] getPiecesArr(Board board) {
        ArrayList<Piece> ret = new ArrayList<>();
        for (Piece[] row : board.logicMat) {
            for (Piece piece : row) {
                if (piece != null) {
                    ret.add(piece);
                }
            }
        }
        return ret.toArray(new Piece[]{});
    }

    public BoardEval isGameOver() {
        BoardEval tie = checkTie();
        BoardEval win = checkWin();
        BoardEval loss = checkLoss();

        return win.isGameOver() ? win : loss.isGameOver() ? loss : tie;
    }

    private BoardEval checkLoss() {
        ArrayList<Move> moves = getAllMoves(currentPlayer);
        if (isInCheck(currentPlayer)) {
            if (moves.isEmpty()) {
                return new BoardEval(GameStatus.LOSS);
            }
        }
        return new BoardEval();
    }

    public BoardEval checkWin() {
        Player otherPlayer = Player.getOtherColor(currentPlayer);
        if (isInCheck(otherPlayer)) {
            ArrayList<Move> moves = getAllMoves(otherPlayer);
            if (moves.isEmpty()) {
                return new BoardEval(GameStatus.CHECKMATE);
            }
        }
        return new BoardEval();
    }

    public BoardEval checkTie() {
        if (!isInCheck(currentPlayer)) {
            if (getAllMoves(Player.getOtherColor(currentPlayer)).isEmpty()) {
                return new BoardEval(GameStatus.STALEMATE);
            }
            if (!canPlayerMate(currentPlayer) && !canPlayerMate(Player.getOtherColor(currentPlayer))) {
                return new BoardEval(GameStatus.INSUFFICIENT_MATERIAL);
            }
            if (checkRepetition()) {
                return new BoardEval(GameStatus.REPETITION);
            }
        }

        return new BoardEval();
    }

    private boolean checkRepetition() {

        return false;
    }

    public boolean isInCheck(Player player) {
        return isThreatened(getKing(player));
    }

    public boolean isThreatened(Piece piece) {
        for (Piece[] row : logicMat) {
            for (Piece p : row) {
                if (p != null && piece != null && !piece.isOnMyTeam(p)) {
                    ArrayList<Move> canMoveTo = p.canMoveTo(this);
                    if (canMoveTo != null)
                        for (Move move : canMoveTo) {
                            if (move.getMovingTo().equals(piece.getLoc()))
                                return true;
                        }

                }
            }
        }

        return false;
    }

    private boolean canPlayerMate(Player player) {
        ArrayList<Piece> currentPlayerPieces = getPlayersPieces(player);
        if (currentPlayerPieces.size() <= 1)
            return false;
        int numOfKnights = 0, numOfBishops = 0;
        for (Piece piece : currentPlayerPieces) {
            if (piece instanceof Rook || piece instanceof Queen || piece instanceof Pawn)
                return true;
            if (piece instanceof Knight)
                numOfKnights++;
            else if (piece instanceof Bishop)
                numOfBishops++;
        }
        if (numOfKnights > 0 && numOfBishops > 0)
            return true;
        return false;
    }

    public Piece getKing(Player player) {
        for (Piece[] row : logicMat) {
            for (Piece p : row) {
                if (p != null && p instanceof King && p.isOnMyTeam(player))
                    return p;
            }
        }
        return null;
    }

    public ArrayList<Piece> getPlayersPieces(Player player) {
        ArrayList<Piece> ret = new ArrayList<>();
        for (Piece[] row : logicMat) {
            for (Piece piece : row) {
                if (piece != null && piece.isOnMyTeam(player))
                    ret.add(piece);
            }
        }
        return ret;
    }

    public ArrayList<Move> getAllMoves(Player currentPlayer) {
        ArrayList<Move> ret = new ArrayList<>();
        for (Piece[] row : logicMat) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.isOnMyTeam(currentPlayer)) {
                        ArrayList<Move> movingTo = piece.canMoveTo(this);
                        model.checkLegal(movingTo, this);
                        ret.addAll(movingTo);
                    }
            }
        }
        return ret;
    }

    public ArrayList<Move> getAllMovesForCurrentPlayer() {
        ArrayList<Move> ret = new ArrayList<>();
        for (Piece[] row : logicMat) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.isOnMyTeam(currentPlayer)) {
                        ArrayList<Move> movingTo = piece.canMoveTo(this);
                        model.checkLegal(movingTo, this);
                        ret.addAll(movingTo);
                    }
            }
        }
        return ret;
    }

    @Override
    public Iterator<Piece[]> iterator() {
        return Arrays.stream(logicMat).iterator();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean isSquareThreatened(Location square, Player threateningPlayer) {
        for (Piece[] row : this) {
            for (Piece p : row) {
                if (p != null && p.isOnMyTeam(threateningPlayer)) {
                    ArrayList<Move> canMoveTo = p.canMoveTo(this);
                    if (canMoveTo != null)
                        for (Move move : canMoveTo) {
                            if (move.getMovingFrom().equals(square))
                                return true;
                        }

                }
            }
        }
        return false;
    }

    public void applyMove(Move move) {
        applyMove(move, true);
    }

    private void applyMove(Move move, boolean setMoved) {
        if (move instanceof Castling) {
            castle((Castling) move);
        } else if (move instanceof EnPassant) {
            EnPassant epsn = (EnPassant) move;
            applyMove(epsn.getCapturedMoveToBeCaptured());
        } else if (move instanceof PromotionMove) {
            move.setMovingFromPiece(Piece.promotePiece(move.getMovingFromPiece(), ((PromotionMove) move).getPromotingTo()));
        }
        Piece piece = move.getMovingFromPiece();
        Location prev = move.getMovingFrom();
        Location movingTo = move.getMovingTo();
        setPiece(movingTo, piece);
        setPiece(prev, null);
        if (setMoved)
            piece.setMoved(move);
        else
            piece.deleteMove();
        piece.setLoc(movingTo);
    }

    public void undoMove(Move move) {
        undoMove(move, true);
    }

    private void undoMove(Move move, boolean setMoved) {
        if (move instanceof Castling) {
            undoCastle((Castling) move);
        } else if (move instanceof EnPassant) {
            EnPassant epsn = (EnPassant) move;
            undoMove(epsn.getCapturedMoveToBeCaptured(), false);
        } else if (move instanceof PromotionMove) {
            move.setMovingFromPiece(new Pawn(move.getMovingFrom(), move.getMovingFromPiece().getPieceColor(), true));
        }
        Location currentPieceLocation = move.getMovingTo();
        Location originalPieceLocation = move.getMovingFrom();
        Piece piece = move.getMovingFromPiece();
        Piece otherPiece = move.getMovingToPiece();
        setPiece(originalPieceLocation, piece);
        setPiece(currentPieceLocation, otherPiece);
        if (setMoved)
            piece.setMoved(move);
        else
            piece.deleteMove();
        piece.setLoc(originalPieceLocation);
    }

    private void undoCastle(Castling castling) {
        applyMove(new Move(castling.getRookFinalLoc(), castling.getRookStartingLoc(), false, this), false);
    }

    private void castle(Castling castling) {
        applyMove(new Move(castling.getRookStartingLoc(), castling.getRookFinalLoc(), false, this));
    }

    public void printBoard() {
        String space = "\u2003";
        for (Piece[] row : this) {
            for (Piece piece : row) {
                String prt = space;
                System.out.print("|");
                if (piece != null) {
                    System.out.print(piece.isWhite() ? ANSI_WHITE : ANSI_BLACK);
                    types type = piece.getType();
                    switch (type) {
                        case PAWN: {
                            prt = pawn;
                            break;
                        }
                        case KNIGHT: {
                            prt = knight;
                            break;
                        }
                        case BISHOP: {
                            prt = bishop;
                            break;
                        }
                        case ROOK: {
                            prt = rook;
                            break;
                        }
                        case QUEEN: {
                            prt = queen;
                            break;
                        }
                        case KING: {
                            prt = king;
                            break;
                        }
                    }
                }
                System.out.print(prt + ANSI_RESET + "|");
            }
            System.out.println();
        }
    }

    public Model getModel() {
        return model;
    }

    public boolean isSquareEmpty(Location loc) {
        return getPiece(loc) == null;
    }

    public void makeMove(Move move) {
        movesList.add(new Move(move));
        currentMoveIndex++;
        applyMove(move);
    }

    public void goToMove(int index) {
        index *= 2;
        if (currentMoveIndex < index) {
            for (int i = 0; i < index; i++) {
                Move move = movesList.get(movesList.size() - i - 1);
                applyMove(move);
            }
        } else
            for (int i = 0; i < index; i++) {
                Move move = movesList.get(movesList.size() - i - 1);
                undoMove(move);
            }
        currentMoveIndex = index;
    }
}
