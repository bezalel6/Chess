package ver18_icon_manager;

import ver18_icon_manager.moves.*;
import ver18_icon_manager.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static ver18_icon_manager.Model.ANSI_BLACK;
import static ver18_icon_manager.Model.ANSI_WHITE;
import static ver18_icon_manager.types.Piece.*;

public class Board implements Iterable<Piece[]> {
    //region vars
    public static final String ANSI_RESET = "\u001B[0m";
    private Piece[][] logicMat;
    private ArrayList<Piece>[] pieces;
    private ArrayList<Piece>[][][] squareControl;
    private Piece[] kings;
    private Eval boardEval;
    private int currentPlayer;
    private Model model;
    private ArrayList<String> movesList;
    private int halfMoveCounter, fullMoveCounter;
    private Location enPassantTargetLoc;
    private Location enPassantActualLoc;
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

    public CastlingAbility getCastlingAbility() {
        return castlingAbility;
    }

    public void comparePiecesArrAndMat() {
        int num = 0;
        for (ArrayList<Piece> arr : pieces)
            for (Piece p : arr) {
                boolean b = false;
                for (Piece[] row : this) {
                    for (Piece piece : row) {
                        if (piece != null) {
                            if (piece.equals(p)) {
                                b = true;
                                break;
                            }
                        }
                    }
                    if (b)
                        break;
                }
                if (!b) {
                    num++;
                    System.out.println("Didnt find " + p + " in mat");
                }

            }
        if (num != 0)
            System.out.println("Num of not found pieces = " + num);
        else
            System.out.println("found all pieces");
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

    public Location getEnPassantTargetLoc() {
        return enPassantTargetLoc;
    }

    public void setEnPassantTargetLoc(Location enPassantTargetLoc) {
        if (enPassantTargetLoc == null) {
            this.enPassantTargetLoc = null;
        } else this.enPassantTargetLoc = new Location(enPassantTargetLoc);
    }

    public void setEnPassantTargetLoc(String enPassantTargetLoc) {
        if (enPassantTargetLoc.replaceAll("\\s+", "").equalsIgnoreCase("-")) {
            this.enPassantTargetLoc = null;
        } else this.enPassantTargetLoc = new Location(enPassantTargetLoc);
    }

    public Location getEnPassantActualLoc() {
        return enPassantActualLoc;
    }

    public void setEnPassantActualLoc(Location enPassantActualLoc) {

        this.enPassantActualLoc = enPassantActualLoc;
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

    private void replacePiece(Piece piece) {
        if (piece != null) {

            Location pieceLoc = piece.getLoc();
            int index = -1;
            ArrayList<Piece> pieceArrayList = pieces[piece.getPieceColor()];
            for (int i = 0; i < pieceArrayList.size(); i++) {
                Piece p = pieceArrayList.get(i);
                if (p.getLoc().equals(pieceLoc)) {
                    index = i;
                    break;
                }
            }
            if (index != -1)
                pieceArrayList.set(index, piece);
            else {
                System.out.println("didnt find piece in pieces array list");

            }
            piece.setLoc(pieceLoc);
            setPiece(pieceLoc, piece);
        } else {
            System.out.println("replacing piece: piece was null");
        }
    }

    private void setPiece(Location loc, Piece piece) {

        logicMat[loc.getRow()][loc.getCol()] = piece;
    }

    public Piece getPiece(int row, int col) {
        return logicMat[row][col];
    }

    public Piece getPiece(Location loc) {
        return getPiece(loc.getRow(), loc.getCol());
    }


    public BoardEval isGameOver() {
        BoardEval tie = checkTie();
        BoardEval win = checkWin();
        BoardEval loss = checkLoss();
        return win.isGameOver() ? win : loss.isGameOver() ? loss : tie;
//        return win.isGameOver() ? win : tie;
    }

    private BoardEval checkLoss() {
        ArrayList<Move> moves = getAllMoves(currentPlayer);
        if (isInCheck(currentPlayer)) {
            if (moves.isEmpty()) {
                return new BoardEval(GameStatus.CHECKMATE, GameStatus.LOSING_SIDE);
            }
        }
        return new BoardEval();
    }

    public BoardEval checkWin() {
        int otherPlayer = Player.getOtherColor(currentPlayer);
        if (isInCheck(otherPlayer)) {
            ArrayList<Move> moves = getAllMoves(otherPlayer);
            if (moves.isEmpty()) {
                return new BoardEval(GameStatus.CHECKMATE, GameStatus.WINNING_SIDE);
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
        //todo O(no)
//        for (Piece tPiece : pieces[piece.getOpponent()]) {
//            if (Piece.isLocInMovesList(tPiece.getPseudoMovesList(this), pieceLoc))
//                return true;
//        }

        return false;
    }

    public int getGamePhase() {

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
        Location movingFrom = move.getMovingFrom();
        Location movingTo = move.getMovingTo();

        if (move instanceof Castling) {
            castle((Castling) move);
        } else if (move instanceof EnPassant) {
            //TODO BUG
            Location actualLoc = new Location(getEnPassantActualLoc());
            Location targetLoc = new Location(getEnPassantTargetLoc());
            if (actualLoc != null && targetLoc != null) {
                Piece oP = getPiece(actualLoc);
                oP.setLoc(targetLoc);
                oP.setEaten(true);
                setPiece(actualLoc, null);
                move.setCapturing(true);
            } else {
                System.out.println("applying epsn move - one of the squares is null");
            }

        } else if (move instanceof PromotionMove) {
            Piece p = getPiece(movingFrom);
            Piece newPiece = Piece.promotePiece(p, ((PromotionMove) move).getPromotingTo());
            replacePiece(newPiece);
        } else if (move instanceof DoublePawnPush) {
            setEnPassantTargetLoc(((DoublePawnPush) move).getEnPassantTargetSquare());
            setEnPassantActualLoc(move.getMovingTo());
        }
        //region not important
        if (!move.isReversible())
            halfMoveCounter++;
        else halfMoveCounter = 0;

        if (move.getBoard().currentPlayer == Player.BLACK)
            fullMoveCounter++;

        Piece otherPiece = getPiece(movingTo);
        Piece piece = getPiece(movingFrom);

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
        //endregion
        if (otherPiece != null) {
            otherPiece.setEaten(true);
        }
        piece.setLoc(movingTo);
        piece.setMoved(move);
        setPiece(movingTo, piece);
        setPiece(movingFrom, null);
        if (!(move instanceof DoublePawnPush)) {
            setEnPassantTargetLoc((Location) null);
            setEnPassantActualLoc(null);
        }
    }

    private void movePiece(Move move) {
        Location movingFrom = move.getMovingFrom();
        Location movingTo = move.getMovingTo();
        Piece otherPiece = getPiece(movingTo);
        Piece piece = getPiece(movingFrom);
        if (otherPiece != null) {
            otherPiece.setEaten(true);
        }
        setPiece(movingTo, piece);
        piece.setLoc(movingTo);
        piece.setMoved(move);
        setPiece(movingFrom, null);

    }

    public void undoMove(Move move) {
        setEnPassantTargetLoc(move.getPrevEnPassantTargetLoc());
        setEnPassantActualLoc(move.getPrevEnPassantActualLoc());

        Location movingFrom = move.getMovingTo();
        Location movingTo = move.getMovingFrom();

        if (move instanceof Castling) {
            undoCastle((Castling) move);
        } else if (move instanceof PromotionMove) {
            Pawn oldPiece = new Pawn(movingFrom, move.getMovingPlayer(), true);
            replacePiece(oldPiece);
        }

        if (!move.isReversible())
            halfMoveCounter--;
        else halfMoveCounter = 0;

        if (move.getBoard().currentPlayer == Player.BLACK)
            fullMoveCounter--;

        Piece otherPiece = null;
        if (move.isCapturing())
            otherPiece = getEatenPiece(movingFrom, Player.getOtherColor(move.getMovingPlayer()));
        Piece piece = getPiece(movingFrom);

        if (piece instanceof King) {
            CastlingAbility prevCA = move.getPrevCastlingAbility();
            castlingAbility = prevCA;
        }
        if (otherPiece != null) {
            otherPiece.setEaten(false);
        }

        piece.setLoc(movingTo);
        piece.setMoved(move);
        setPiece(movingTo, piece);
        setPiece(movingFrom, otherPiece);
        if (move instanceof EnPassant) {
            Location actualLoc = getEnPassantActualLoc();
            Location targetLoc = getEnPassantTargetLoc();
            if (actualLoc != null && targetLoc != null) {
                movePiece(new Move(targetLoc, actualLoc, this));
            } else {
                System.out.println("undoing epsn move - one of the squares is null");
            }
        }

    }

    private Piece getEatenPiece(Location pieceLoc, int pieceColor) {
        Piece ret = null;
        for (Piece piece : pieces[pieceColor]) {
            if (piece.isEaten() && piece.getLoc().equals(pieceLoc)) {
                ret = piece;
                break;
            }
        }
        return ret;
    }

    private void undoCastle(Castling castling) {
        movePiece(new Move(castling.getCastlingLocs()[Castling.ROOK_FINAL_LOC], castling.getCastlingLocs()[Castling.ROOK_STARTING_LOC], false, this));
    }

    private void castle(Castling castling) {
        movePiece(new Move(castling.getCastlingLocs()[Castling.ROOK_STARTING_LOC], castling.getCastlingLocs()[Castling.ROOK_FINAL_LOC], false, this));
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
    }

    public void unmakeMove(Move move) {
        movesList.remove(move.getMoveFEN());
        undoMove(move);
    }

    public boolean isInCheck() {
        return isInCheck(currentPlayer);
    }

    public Piece[][] getLogicMat() {
        return logicMat;
    }

    //todo DELETE
    public Controller getController() {
        return model.getController();
    }

    static class GamePhase {
        public static final int OPENING = 0, MIDDLE_GAME = 1, END_GAME = 2;
    }
}
