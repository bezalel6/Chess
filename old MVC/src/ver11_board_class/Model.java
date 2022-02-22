package ver11_board_class;

import ver11_board_class.moves.*;
import ver11_board_class.types.Piece.Player;
import ver11_board_class.types.*;

import java.util.ArrayList;
import java.util.ListIterator;

class thread implements Runnable {
    private Thread t;
    private Move move;
    private Controller controller;

    public thread(Move move, Controller controller) {
        this.move = move;
        this.controller = controller;
    }

    @Override
    public void run() {
        controller.aiFoundOption(move);
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }
}

public class Model {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private static int ROWS;
    private static int COLS;
    public Eval eval;
    private Board logicMat;
    private Controller controller;
    private String additions = "";
    private int scanDepth;
    private String knight = "♘", bishop = "♗", pawn = "♙", king = "♔", queen = "♕", rook = "♖";

    public Model(int boardSize, Controller controller) {
        this.controller = controller;
        ROWS = COLS = boardSize;
    }

    public void initGame(int startingPosition) {
        logicMat = loadPos(Positions.posToPieces(Positions.getAllPositions[startingPosition]));
        eval = new Eval(logicMat);
    }

    public Board loadPos(Piece[] pos) {
        Board ret = new Board(ROWS, COLS, this);
        for (Piece piece : pos) {
            if (piece != null)
                ret.setPiece(piece.getLoc(), piece);
        }
        return ret;
    }

    public String makeMove(Move move, Board board) {
        Piece piece = board.getPiece(move.getMovingFrom());
//        if (move instanceof EnPassant) {
//            Location loc = ((EnPassant) move).getCapturingPieceActualLocation();
//            Piece capturingPiece = getPiece(loc, board);
//            controller.updateView(capturingPiece.getLoc(), loc);
//            applyMove(new Move(capturingPiece.getLoc(), loc, false, board), board);
//        }
        if (move instanceof Castling) {
            castle((Castling) move, board);
        }
        board.applyMove(move);
        piece.setMoved(move);
        //board.printBoard();
        return move.getAnnotation();
    }


    private void castle(Castling castling, Board board) {
        Rook rook = castling.getRook();
        Location rookFinalLoc = castling.getRookFinalLoc();
        controller.updateView(rook.getLoc(), rookFinalLoc);
        makeMove(new Move(rook.getLoc(), rookFinalLoc, false, board), board);
    }


    public ArrayList<Location> getPiecesLocations(Player currentPlayer) {
        ArrayList<Location> ret = new ArrayList<>();
        for (Piece[] row : logicMat.getMat()) {
            for (Piece piece : row) {
                if (piece != null)
                    if (piece.isOnMyTeam(currentPlayer))
                        ret.add(piece.getLoc());
            }
        }
        return ret;
    }


    private boolean isLocInMoveList(ArrayList<Move> list, Location loc) {
        for (Move move : list) {
            if (move.getMovingFrom().isEqual(loc))
                return true;
        }
        return false;
    }

    public void checkLegal(ArrayList<Move> movesList, Board board) {
        if (movesList.isEmpty())
            return;
        ListIterator iter = movesList.listIterator();
        ArrayList<Move> delete = new ArrayList<>();
        while (iter.hasNext()) {
            Board copy = new Board(board, this);
            Piece currentPiece = copy.getPiece(movesList.get(0).getMovingFrom());
            Move move = (Move) iter.next();
            Location movingTo = move.getMovingTo();
            if (!isInBounds(movingTo)) {
                delete.add(move);
            }
            Piece destination = copy.getPiece(movingTo);

            if (destination != null && currentPiece.isOnMyTeam(destination)) {
                delete.add(move);
            }
//            if (specialMove != SpecialMoves.NONE) {
//                if (specialMove == SpecialMoves.SHORT_CASTLE) {
//                    Move.Castling kingSide = ((King) currentPiece).kingSide;
//                    if (isSquareThreatened(kingSide.getKingMiddleMove(), currentPiece.getOtherColor(), board) || getPiece(kingSide.getKingMiddleMove(), board) != null) {
//                        delete.add(move);
//
//                    }
//                } else if (specialMove == SpecialMoves.LONG_CASTLE) {
//                    Move.Castling queenSide = ((King) currentPiece).queenSide;
//                    if (isSquareThreatened(queenSide.getKingMiddleMove(), currentPiece.getOtherColor(), board) ||
//                            getPiece(queenSide.getKingMiddleMove(), board) != null || getPiece(queenSide.getRookMiddleLoc(), board) != null) {
//                        delete.add(move);
//
//                    }
//                } else if (specialMove == SpecialMoves.PROMOTION) {
//                    delete.add(move);
//                    iter.add(new Move(move.getMovingFrom(), move.isCapturing(), move.getPiece(), new PromotionMove(move, Piece.types.QUEEN)));
//                    iter.add(new Move(move.getMovingFrom(), move.isCapturing(), move.getPiece(), new PromotionMove(move, Piece.types.BISHOP)));
//                    iter.add(new Move(move.getMovingFrom(), move.isCapturing(), move.getPiece(), new PromotionMove(move, Piece.types.KNIGHT)));
//                    iter.add(new Move(move.getMovingFrom(), move.isCapturing(), move.getPiece(), new PromotionMove(move, Piece.types.ROOK)));
//                }
//            }
//            if (move instanceof EnPassant) {
//                copy.applyMove(((EnPassant) move).getCapturedMoveToBeCaptured(), copy);
//            }
            if (move instanceof Castling) {
                Castling castling = (Castling) move;
                if (copy.isSquareThreatened(castling.getKingMiddleMove(), currentPiece.getOtherColor()) || copy.isSquareThreatened(castling.getKingFinalLoc(), currentPiece.getOtherColor())) {
                    delete.add(move);
                }
            }
            copy.applyMove(move);
            if (copy.isInCheck(currentPiece.getPieceColor())) {
                delete.add(move);
            }
        }
        for (Move move : delete) {
            movesList.remove(move);
        }
    }


    public boolean isInBounds(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < ROWS && loc.getCol() >= 0 && loc.getCol() < COLS;
    }

    public Piece getPiece(Location loc, Board board) {
        if (isInBounds(loc))
            return board.getPiece(loc);
        return null;
    }

    public ArrayList<Move> getMoves(Piece piece, Board board) {

        if (piece == null) {
            return null;
        }
        ArrayList<Move> list = piece.canMoveTo(board);

        checkLegal(list, board);
        return list;
    }

    public Board getBoard() {
        return logicMat;
    }

    public void setBoard(Board board) {
        logicMat = board;
    }

    public void printLogicMat(Board board) {
        String space = " ";
        for (Piece[] row : board.getMat()) {
            for (Piece piece : row) {
                String prt = space;
                System.out.print("|");
                if (piece != null) {
                    if (piece.isWhite())
                        System.out.print(ANSI_WHITE);
                    else System.out.print(ANSI_BLACK);
                    Piece.types type = piece.getType();
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

    // מהלך מינימקס
    public Move getAiMove() {
        System.out.println("Model getAiMove() using MINIMAX");
        scanDepth = controller.getScanDepth();
        return getBestMoveUsingMinimax().getMove();
        //return null;
    }

    // ============================ for minimax ===========================
    private MinimaxMove getBestMoveUsingMinimax() {
        MinimaxMove ret = minimax(logicMat, true, 0, Double.MIN_VALUE, Double.MAX_VALUE);
        System.out.println("minimax move = " + ret);
        return ret;
    }

    public MinimaxMove minimax(Board board, boolean isMax, int depth, double a, double b) {
        MinimaxMove bestMove = new MinimaxMove(null, new BoardEval(isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE));
        Player actualPlayer = controller.getCurrentPlayer();
        Player player = isMax ? actualPlayer : Player.getOtherColor(actualPlayer);
        Board copy = new Board(board, this);

        ArrayList<Move> possibleMoves = copy.getAllMoves(player);
        BoardEval value = copy.getBoardEval();
        if (value.isGameOver() || depth >= scanDepth) {
            return new MinimaxMove(null, value);
        }

        for (int i = 0; i < possibleMoves.size(); i++) {
            Move move = possibleMoves.get(i);
            copy.applyMove(move);
            MinimaxMove val = minimax(copy, !isMax, depth + 1, a, b);
            val.setMove(move);

            copy.undoMove(move);
            if (depth == 0) {
                System.out.println("Move#" + (i + 1) + ": " + move + " val: " + val);
            }
            if (isMax) {
                if (bestMove.getMoveValue().getEval() < val.getMoveValue().getEval()) {
                    bestMove = new MinimaxMove(val);
                }
                a = Math.max(a, bestMove.getMoveValue().getEval());

            } else {
                if (bestMove.getMoveValue().getEval() > val.getMoveValue().getEval()) {
                    bestMove = new MinimaxMove(val);
                }
                b = Math.min(b, bestMove.getMoveValue().getEval());
            }
            if (b <= a) break;

        }
        return bestMove;
    }
}


