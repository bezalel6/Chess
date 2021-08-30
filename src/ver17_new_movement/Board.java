package ver17_new_movement;


import ver17_new_movement.types.Piece.Player;
import ver17_new_movement.moves.*;
import ver17_new_movement.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static ver17_new_movement.Model.ANSI_BLACK;
import static ver17_new_movement.Model.ANSI_WHITE;
import static ver17_new_movement.types.Piece.*;

public class Board implements Iterable<Piece[]> {
    //region vars
    public static final String ANSI_RESET = "\u001B[0m";
    private Piece[][] logicMat;
    private ArrayList<Piece>[] pieces;
    private Piece[] kings;
    private Eval boardEval;
    private int currentPlayer;
    private Model model;
    private ArrayList<String> movesList;
    private int halfMoveCounter, fullMoveCounter;
    private Location enPassantTargetSquare;
    private Location enPassantActualSquare;
    private FEN fen;
    private CastlingAbility castlingAbility;

    //endregion
    public Board(String fenStr, Model model) {
        this.fen = new FEN(fenStr, this);
        setMat(fen);
        this.model = model;
        boardEval = new Eval(this);
        movesList = new ArrayList<>();
        initPiecesArrays();
    }
//
//    public SquaresControl getSquaresControl() {
//        return squaresControl;
//    }

    public CastlingAbility getCastlingAbility() {
        return castlingAbility;
    }

    public void printNumOfControlledSquares() {
//        System.out.println("white: " + squaresControl.getSquaresControl(Player.WHITE).size() + "\n" + "black: " + squaresControl.getSquaresControl(Player.BLACK).size());

    }

    private void initPiecesArrays() {
        kings = new Piece[2];
        pieces = new ArrayList[2];
        pieces[0] = new ArrayList<>();
        pieces[1] = new ArrayList<>();
        for (Piece[] row : logicMat)
            for (Piece piece : row)
                if (piece != null) {
                    pieces[piece.getPieceColor()].add(piece);
                    if (piece instanceof King)
                        kings[piece.getPieceColor()] = piece;
                }
    }

    private Piece[][] copyMat(Piece[][] source) {
        Piece[][] ret = new Piece[8][8];
        for (int i = 0, sourceLength = source.length; i < sourceLength; i++) {
            Piece[] row = source[i];
            for (int j = 0, rowLength = row.length; j < rowLength; j++) {
                Piece piece = row[j];
                ret[i][j] = Piece.copyPiece(piece);
            }
        }
        return ret;
    }

    public Location getEnPassantTargetSquare() {
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

    public Location getEnPassantActualSquare() {
        return enPassantActualSquare;
    }

    public void setEnPassantActualSquare(Location enPassantActualSquare) {

        this.enPassantActualSquare = enPassantActualSquare;
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

    public BoardEval getBoardEval(int player) {
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
        castlingAbility = new CastlingAbility(fen.getCastlingAbilityStr());
        currentPlayer = fen.getPlayerToMove();

    }

    public FEN getFen() {
        return fen;
    }

    public String getFenStr() {
        return fen.generateFEN();
    }

    public void setPiece(Location loc, Piece piece) {
        logicMat[loc.getRow()][loc.getCol()] = piece;
    }

    public Piece getPiece(int row, int col) {
        return logicMat[row][col];
    }

    public Piece getPiece(Location loc) {
        return getPiece(loc.getRow(), loc.getCol());
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
//        return win.isGameOver() ? win : tie;
    }

    private BoardEval checkLoss() {
//        ArrayList<Move> moves = getAllMoves(currentPlayer);
//        if (isInCheck(currentPlayer)) {
//            if (moves.isEmpty()) {
//                return new BoardEval(GameStatus.LOSS);
//            }
//        }
        return new BoardEval();
    }

    public BoardEval checkWin() {
        int otherPlayer = Player.getOtherColor(currentPlayer);
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
            if (getAllMoves(currentPlayer).isEmpty()) {
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

    public boolean isInCheck(int player) {
        return isThreatened(getKing(player));
    }

    /**
     * checking if any of the opponent's pieces pseudo moves are to
     *
     * @param piece
     * @return
     */
    public boolean isThreatened(Piece piece) {
        Location pieceLoc = piece.getLoc();
        for (Piece tPiece : pieces[piece.getOpponent()]) {
            if (Piece.isLocInMovesList(tPiece.getPseudoMoves(this), pieceLoc))
                return true;
        }

        return false;
    }

    public GamePhase getGameStage() {

        return GamePhase.MIDDLE_GAME;
    }

    private boolean canPlayerMate(int player) {
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

    public Piece getKing(int player) {
        return kings[player];
    }

    public ArrayList<Piece> getPlayersPieces(int player) {
        return pieces[player];
    }

    public ArrayList<Move> getAllMoves(int player) {
        ArrayList<Move> ret = new ArrayList<>();
        for (Piece piece : pieces[player])
            ret.addAll(piece.canMoveTo(this));
        return ret;
    }

    public ArrayList<Move> getAllMovesForCurrentPlayer() {
        return getAllMoves(currentPlayer);
    }

    @Override
    public Iterator<Piece[]> iterator() {
        return Arrays.stream(logicMat).iterator();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }


    public void applyMove(Move move) {
        if (move instanceof Castling) {
            castle((Castling) move);
        } else if (move instanceof EnPassant) {
            Location actualSquare = getEnPassantActualSquare();
            Location targetSquare = getEnPassantTargetSquare();
            if (actualSquare != null && targetSquare != null) {
                applyMove(new Move(actualSquare, targetSquare, false, this));
                move.setCapturing(true);
            }
        } else if (move instanceof PromotionMove) {
            Piece newPiece = Piece.promotePiece(getPiece(move.getMovingFrom()), ((PromotionMove) move).getPromotingTo());
            setPiece(newPiece.getLoc(), newPiece);
        } else if (move instanceof DoublePawnPush) {
            setEnPassantTargetSquare(((DoublePawnPush) move).getEnPassantTargetSquare());
            setEnPassantActualSquare(move.getMovingTo());
        }
        if (!move.isReversible())
            halfMoveCounter++;
        else halfMoveCounter = 0;

        if (move.getBoard().currentPlayer == Player.BLACK)
            fullMoveCounter++;

        Location prev = move.getMovingFrom();
        Location movingTo = move.getMovingTo();
        Piece otherPiece = getPiece(movingTo);
        Piece piece = getPiece(prev);
        if (piece instanceof King) {
            castlingAbility.setCastlingAbility(piece.getPieceColor());
        } else if (piece instanceof Rook) {
            int clr = piece.getPieceColor();
            Piece king = getKing(clr);
            int side = QUEEN_SIDE;
            if (king.getStartingLoc().getCol() < piece.getStartingLoc().getCol() * piece.getDifference()) {
                side = KING_SIDE;
            }
            castlingAbility.setCastlingAbility(piece.getPieceColor(), side);
        }
        if (otherPiece != null) {
            otherPiece.setEaten(true);
//            pieces[otherPiece.getPieceColor()].remove(otherPiece);
        }
        setPiece(movingTo, piece);
        piece.setLoc(movingTo);
        piece.setMoved(move);
        setPiece(prev, null);
        if (!(move instanceof DoublePawnPush))
            setEnPassantTargetSquare((Location) null);

    }

    public void undoMove(Move move) {
        if (move instanceof Castling) {
            undoCastle((Castling) move);
        } else if (move instanceof EnPassant) {
            Location actualSquare = getEnPassantActualSquare();
            Location targetSquare = getEnPassantTargetSquare();
            if (actualSquare != null && targetSquare != null) {
                applyMove(new Move(targetSquare, actualSquare, false, this));
            }
        } else if (move instanceof PromotionMove) {
            Pawn oldPiece = new Pawn(move.getMovingFrom(), move.movingPlayer(), true);
            setPiece(oldPiece.getLoc(), oldPiece);
        } else if (move instanceof DoublePawnPush) {
//            setEnPassantTargetSquare(((DoublePawnPush) move).getEnPassantTargetSquare());
//            setEnPassantActualSquare(move.getMovingTo());
            //todo ↓
            setEnPassantTargetSquare((Location) null);
            setEnPassantActualSquare(null);
        }
        if (!move.isReversible())
            halfMoveCounter--;
        else halfMoveCounter = 0;

        if (move.getBoard().currentPlayer == Player.BLACK)
            fullMoveCounter--;

        Location prev = move.getMovingTo();
        Location movingTo = move.getMovingFrom();
        Piece otherPiece = move.getMovingToPiece();
        Piece piece = getPiece(prev);
        if (piece instanceof King) {
            //todo set the correct castling ability
            castlingAbility.setCastlingAbility(piece.getPieceColor());
        } else if (piece instanceof Rook) {
            int clr = piece.getPieceColor();
            Piece king = getKing(clr);
            int side = QUEEN_SIDE;
            if (king.getStartingLoc().getCol() < piece.getStartingLoc().getCol() * piece.getDifference()) {
                side = KING_SIDE;
            }
            castlingAbility.setCastlingAbility(piece.getPieceColor(), side);
        }
        if (otherPiece != null) {
            otherPiece.setEaten(false);
//            pieces[otherPiece.getPieceColor()].add(otherPiece);
        }
        setPiece(movingTo, piece);
        setPiece(prev, otherPiece);
        piece.setLoc(movingTo);
        piece.setMoved(move);

    }

    private void undoCastle(Castling castling) {
        applyMove(new Move(castling.getCastlingLocs()[Castling.ROOK_FINAL_LOC], castling.getCastlingLocs()[Castling.ROOK_STARTING_LOC], false, this));
    }

    private void castle(Castling castling) {
        applyMove(new Move(castling.getCastlingLocs()[Castling.ROOK_STARTING_LOC], castling.getCastlingLocs()[Castling.ROOK_FINAL_LOC], false, this));
    }

    public void printBoard() {
        String knight = "♘", bishop = "♗", pawn = "♙", king = "♔", queen = "♕", rook = "♖";

        String space = "\u2003";
        for (Piece[] row : this) {
            for (Piece piece : row) {
                String prt = space;
                System.out.print("|");
                if (piece != null) {
                    System.out.print(piece.isWhite() ? ANSI_WHITE : ANSI_BLACK);
                    int type = piece.getPieceType();
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
        //todo delete en passant target square
//        setEnPassantTargetSquare((Location) null);
    }

    public boolean isInCheck() {
        return isInCheck(currentPlayer);
    }

    public void setEnPassantTargetLoc(Location loc) {
        enPassantTargetSquare = new Location(loc);
    }

    public Piece[][] getLogicMat() {
        return logicMat;
    }

    //todo DELETE
    public Controller getController() {
        return model.getController();
    }

    enum GamePhase {OPENING, MIDDLE_GAME, END_GAME}
}
