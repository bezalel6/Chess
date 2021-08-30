package ver16_calculating_pinned_pieces;

import ver16_calculating_pinned_pieces.SquaresControl.Square;
import ver16_calculating_pinned_pieces.types.Piece.PieceTypes;
import ver16_calculating_pinned_pieces.types.Piece.*;
import ver16_calculating_pinned_pieces.moves.*;
import ver16_calculating_pinned_pieces.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static ver16_calculating_pinned_pieces.Model.ANSI_BLACK;
import static ver16_calculating_pinned_pieces.Model.ANSI_WHITE;

class SquaresControl {
    private ArrayList<Square>[] squaresControlArr;
    private Board board;

    public SquaresControl(Board board, ArrayList<Piece>[] pieces) {
        this.board = board;
        squaresControlArr = new ArrayList[2];
        init(pieces);

    }

    private void init(ArrayList<Piece>[] pieces) {
        squaresControlArr[Player.WHITE] = generateSquaresFromPieces(pieces[Player.WHITE]);
        squaresControlArr[Player.BLACK] = generateSquaresFromPieces(pieces[Player.BLACK]);
    }

    private ArrayList<Square> generateSquaresFromPieces(ArrayList<Piece> pieces) {
        ArrayList<Square> ret = new ArrayList<>();
        pieces.forEach(piece -> {
            piece.canMoveTo(board).forEach(move -> ret.add(new Square(piece, move.getMovingTo())));
        });
        return ret;
    }

    public void madeMove(Piece piece, Piece otherPiece) {

        if (otherPiece != null) {
            a(otherPiece);
        }
        a(piece);
        addPieceMoves(piece);
    }

    private void addPieceMoves(Piece piece) {
        ArrayList<Square> list = getSquaresControl(piece.getPieceColor());
        piece.canMoveTo(board).forEach(newMove -> list.add(new Square(piece, newMove.getMovingTo())));
    }

    private void a(Piece piece) {
        piece.getPiecesBlockedByMe().forEach(blockedPiece -> {
            ArrayList<Square> tList = getSquaresControl(blockedPiece.getPieceColor());
            blockedPiece.canMoveTo(board).forEach(move -> {
                Square newSquare = new Square(blockedPiece, move.getMovingTo());
                if (shouldAddToList(tList, newSquare))
                    tList.add(newSquare);
            });
        });
        piece.getSkewers().forEach(skewer -> {
            System.out.println("skewer " + skewer);
            Piece blockedPiece = skewer.getPieceBehind();
            ArrayList<Square> tList = getSquaresControl(blockedPiece.getPieceColor());
            blockedPiece.canMoveTo(board).forEach(move -> {
                Square newSquare = new Square(blockedPiece, move.getMovingTo());
                if (shouldAddToList(tList, newSquare))
                    tList.add(newSquare);
            });
        });
        removeSquares(piece);
    }

    private boolean shouldAddToList(ArrayList<Square> list, Square square) {
        for (Square sq : list) {
            if (sq.equals(square)) {
                return false;
            }
        }
        return true;
    }

    /**
     * removes all squares controlled by a certain piece
     *
     * @param piece
     */
    private void removeSquares(Piece piece) {
        ArrayList<Square> list = getSquaresControl(piece.getPieceColor());
        ArrayList<Square> delete = new ArrayList<>();
        list.forEach(square -> {
            if (square.controllingPiece.equals(piece))
                delete.add(square);
        });
        list.removeAll(delete);
    }

    @Override
    public String toString() {
        return "SquaresControl{" +
                "whitePlayerSquaresControl=" + squaresControlArr[Player.WHITE] +
                ", blackPlayerSquaresControl=" + squaresControlArr[Player.BLACK] +
                '}';
    }


    public ArrayList<Square> getSquaresControl(int player) {
        return squaresControlArr[player];
    }

    class Square {
        private Piece controllingPiece;
        private Location loc;

        public Square(Piece controllingPiece, Location loc) {
            this.controllingPiece = controllingPiece;
            this.loc = loc;
        }

        public Piece getControllingPiece() {
            return controllingPiece;
        }

        public void setControllingPiece(Piece controllingPiece) {
            this.controllingPiece = controllingPiece;
        }

        public Location getLoc() {
            return loc;
        }

        public void setLoc(Location loc) {
            this.loc = loc;
        }


        @Override
        public String toString() {
            return "Square{" +
                    "controllingPiece=" + controllingPiece +
                    ", loc=" + loc +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Square square = (Square) o;
            return controllingPiece.equals(square.controllingPiece) && loc.equals(square.loc);
        }
    }
}

public class Board implements Iterable<Piece[]> {
    //region vars
    public static final String ANSI_RESET = "\u001B[0m";
    private Piece[][] logicMat;
    private ArrayList<Piece>[] pieces;
    private SquaresControl squaresControl;
    private Location[] kingsLocs;
    private int rows, cols;
    private Eval boardEval;
    private int currentPlayer;
    private Model model;
    private String knight = "♘", bishop = "♗", pawn = "♙", king = "♔", queen = "♕", rook = "♖";
    private ArrayList<String> movesList;
    private int halfMoveCounter, fullMoveCounter;
    private Location enPassantTargetSquare;
    private Location enPassantActualSquare;
    private FEN fen;

    //endregion
    public Board(String fenStr, Model model) {
        kingsLocs = new Location[2];
        this.fen = new FEN(fenStr, this);
        rows = cols = 8;
        setMat(fen);
        this.model = model;
        boardEval = new Eval(this);
        movesList = new ArrayList<>();
        initPiecesArr();
        squaresControl = new SquaresControl(this, pieces);
    }

    public SquaresControl getSquaresControl() {
        return squaresControl;
    }

    //    public Board(Board other) {
//        logicMat = copyMat(other.logicMat);
//        rows = cols = 8;
//        boardEval = new Eval(this);
//        currentPlayer = (other.currentPlayer);
//        model = other.model;
//        movesList = new ArrayList<>(other.movesList);
//        halfMoveCounter = other.halfMoveCounter;
//        fullMoveCounter = other.fullMoveCounter;
//        enPassantActualSquare = new Location(other.enPassantActualSquare);
//        enPassantTargetSquare = new Location(other.enPassantTargetSquare);
//        fen = new FEN(other.fen.generateFEN(), this);
//        initPiecesLists();
//    }
    public void printNumOfControlledSquares() {
        System.out.println("white: " + squaresControl.getSquaresControl(Player.WHITE).size() + "\n" + "black: " + squaresControl.getSquaresControl(Player.BLACK).size());

    }

    private void initPiecesArr() {
        pieces = new ArrayList[2];
        pieces[0] = new ArrayList<>();
        pieces[1] = new ArrayList<>();
        for (Piece[] row : logicMat)
            for (Piece piece : row)
                if (piece != null)
                    pieces[piece.getPieceColor()].add(piece);
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
        currentPlayer = fen.getPlayerToMove();

    }

    public void setKingLoc(int player, Location loc) {
        kingsLocs[player] = loc;
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
        //        BoardEval loss = checkLoss();
        //
        //        return win.isGameOver() ? win : loss.isGameOver() ? loss : tie;
        return win.isGameOver() ? win : tie;
    }

//    private BoardEval checkLoss() {
//        ArrayList<Move> moves = getAllMoves(currentPlayer);
//        if (isInCheck(currentPlayer)) {
//            if (moves.isEmpty()) {
//                return new BoardEval(GameStatus.LOSS);
//            }
//        }
//        return new BoardEval();
//    }

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

    public boolean isThreatened(Piece piece) {
        ArrayList<Square> opponentControlledSquares = squaresControl.getSquaresControl(piece.getOtherColor());
        Location pieceLoc = piece.getLoc();
        for (Square square : opponentControlledSquares) {
            if (square.getLoc().equals(pieceLoc)) return true;
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
        return getPiece(kingsLocs[player]);
    }

    public ArrayList<Piece> getPlayersPieces(int player) {
        ArrayList<Piece> ret = new ArrayList<>();
        for (Piece[] row : logicMat) {
            for (Piece piece : row) {
                if (piece != null && piece.isOnMyTeam(player))
                    ret.add(piece);
            }
        }
        return ret;
    }

    public ArrayList<Move> getAllMoves(int currentPlayer) {
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

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean isSquareThreatened(Location square, int threateningPlayer) {
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


    void applyMove(Move move) {
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
            move.setMovingFromPiece(Piece.promotePiece(move.getMovingFromPiece(), ((PromotionMove) move).getPromotingTo()));
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
        //todo maybe update king loc if it doesn't happened automatically
        if (move.isCapturing()) {
            otherPiece.eaten();
            pieces[otherPiece.getPieceColor()].remove(piece);
        }
        setPiece(movingTo, piece);
        piece.setMoved(move);
        piece.setLoc(movingTo);
        setPiece(prev, null);
        if (!(move instanceof DoublePawnPush))
            setEnPassantTargetSquare((Location) null);
        squaresControl.madeMove(piece, otherPiece);
    }

    public void undoMove(Move move) {
        if (move instanceof Castling) {
            undoCastle((Castling) move);
        } else if (move instanceof EnPassant) {
            Location actualSquare = getEnPassantActualSquare();
            Location targetSquare = getEnPassantTargetSquare();
            if (actualSquare == null || targetSquare == null) {
                System.out.println("actual square = " + actualSquare + " target square = " + targetSquare);
            } else
                undoMove(new Move(targetSquare, actualSquare, false, this));
        } else if (move instanceof PromotionMove) {
            move.setMovingFromPiece(new Pawn(move.getMovingFrom(), move.getMovingFromPiece().getPieceColor(), true));
        } else if (move instanceof DoublePawnPush) {
            setEnPassantTargetSquare((Location) null);
            System.out.println("deleted en passant target square " + ((DoublePawnPush) move).getEnPassantTargetSquare());
        }
        Location currentPieceLocation = move.getMovingTo();
        Location originalPieceLocation = move.getMovingFrom();
        Piece piece = getPiece(currentPieceLocation);
        setPiece(originalPieceLocation, piece);
        setPiece(currentPieceLocation, null);

        if (!move.isReversible())
            halfMoveCounter--;

        if (move.getBoard().currentPlayer == Player.BLACK)
            fullMoveCounter--;
        piece.setMoved(move);
        piece.deleteMove();
        piece.setLoc(originalPieceLocation);
    }

    private void undoCastle(Castling castling) {
        applyMove(new Move(castling.getRookFinalLoc(), castling.getRookStartingLoc(), false, this));
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
                    PieceTypes type = piece.getType();
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

    enum GamePhase {OPENING, MIDDLE_GAME, END_GAME}
}
