package ver13_FEN;

import ver13_FEN.moves.*;
import ver13_FEN.types.Piece.Player;
import ver13_FEN.types.Piece.types;
import ver13_FEN.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import static ver13_FEN.Model.ANSI_BLACK;
import static ver13_FEN.Model.ANSI_WHITE;


public class Board implements Iterable<Piece[]> {
    public static final String ANSI_RESET = "\u001B[0m";
    private Piece[][] logicMat;
    private int rows, cols;
    private Eval boardEval;
    private Player currentPlayer;
    private Model model;
    private String knight = "♘", bishop = "♗", pawn = "♙", king = "♔", queen = "♕", rook = "♖";
    private ArrayList<String> movesList;
    private int halfMoveCounter, fullMoveCounter;
    private Location enPassantTargetSquare;
    private FEN fen;

    public Board(String fenStr, Model model) {
        this.fen = new FEN(fenStr, this);
        rows = cols = 8;
        setMat(fen);
        this.model = model;
        boardEval = new Eval(this);
        movesList = new ArrayList<>();
    }

    public Location getEnPassantTargetSquare() {
        System.out.println(enPassantTargetSquare);
        return enPassantTargetSquare;
    }

    public void setEnPassantTargetSquare(Location enPassantTargetSquare) {
        if (enPassantTargetSquare == null) {
            this.enPassantTargetSquare = null;
        } else this.enPassantTargetSquare = new Location(enPassantTargetSquare);
    }

    public void setEnPassantTargetSquare(String enPassantTargetSquare) {
        if (enPassantTargetSquare.replaceAll("\\s+", "").equalsIgnoreCase("-")) {
            this.enPassantTargetSquare = null;
        } else this.enPassantTargetSquare = new Location(enPassantTargetSquare);
    }

    public int getHalfMoveCounter() {
        return halfMoveCounter;
    }

    public void setHalfMoveCounter(int halfMoveCounter) {
        this.halfMoveCounter = halfMoveCounter;
    }

    public int getFullMoveCounter() {
        return fullMoveCounter;
    }

    public void setFullMoveCounter(int fullMoveCounter) {
        this.fullMoveCounter = fullMoveCounter;
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

    private void setMat(FEN fen) {
        logicMat = fen.loadFEN();
        currentPlayer = fen.getPlayerToMove();

    }

    public String getFen() {
        return fen.generateFEN();
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
//        BoardEval loss = checkLoss();
//
//        return win.isGameOver() ? win : loss.isGameOver() ? loss : tie;
        return win.isGameOver() ? win : tie;
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
//        if (movesList.size() > 4) {
//            int lastMoveIndex = movesList.size() - 1;
//            String lastMove = movesList.get(lastMoveIndex);
//            String secondMove = movesList.get(lastMoveIndex);
//            String thirdMove = movesList.get(lastMoveIndex - 4);
//            System.out.println(lastMove);
//            System.out.println(secondMove);
//            System.out.println(thirdMove);
//            boolean res = lastMove == secondMove && lastMove == thirdMove;
//            System.out.println(res);
//            return res;
//        }
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
        } else if (move instanceof DoublePawnPush && currentPlayer == move.movingPlayer()) {
            setEnPassantTargetSquare(((DoublePawnPush) move).getEnPassantTargetSquare());
            System.out.println("Set En passant square " + ((DoublePawnPush) move).getEnPassantTargetSquare());
        }
        if (!move.isReversible())
            halfMoveCounter++;
        else halfMoveCounter = 0;

        if (move.getBoard().currentPlayer == Player.BLACK)
            fullMoveCounter++;

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
        } else if (move instanceof DoublePawnPush) {
            setEnPassantTargetSquare((Location) null);
        }
        Location currentPieceLocation = move.getMovingTo();
        Location originalPieceLocation = move.getMovingFrom();
        Piece piece = move.getMovingFromPiece();
        Piece otherPiece = move.getMovingToPiece();
        setPiece(originalPieceLocation, piece);
        setPiece(currentPieceLocation, otherPiece);

        if (!move.isReversible())
            halfMoveCounter--;

        if (move.getBoard().currentPlayer == Player.BLACK)
            fullMoveCounter--;

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
        movesList.add(move.getMoveFEN());
        applyMove(move);
    }

    public boolean isInCheck() {
        return isInCheck(currentPlayer);
    }

    public void setEnPassantTargetLoc(Location loc) {
        enPassantTargetSquare = new Location(loc);
    }
}
