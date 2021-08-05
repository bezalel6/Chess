package ver14_correct_piece_location;

import ver14_correct_piece_location.types.Piece.Player;
import ver14_correct_piece_location.moves.Castling;
import ver14_correct_piece_location.moves.EnPassant;
import ver14_correct_piece_location.moves.MinimaxMove;
import ver14_correct_piece_location.moves.Move;
import ver14_correct_piece_location.types.Piece;


import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ListIterator;

public class Model {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private static int ROWS;
    private static int COLS;
    public Eval eval;
    private Board logicBoard;
    private Controller controller;
    private int scanDepth;
    private int scanTime;
    private ZonedDateTime minimaxStartedTime;

    public Model(int boardSize, Controller controller) {
        this.controller = controller;
        ROWS = COLS = boardSize;
    }

    public void initGame(int startingPosition) {
        logicBoard = new Board(Positions.getAllPositions().get(startingPosition).getFen(), this);
        eval = new Eval(logicBoard);
    }

    public String makeMove(Move move, Board board) {
        String moveAnnotation = move.getAnnotation();
        board.makeMove(move);
        return moveAnnotation;
    }

    public ArrayList<Location> getPiecesLocations(Player currentPlayer) {
        ArrayList<Location> ret = new ArrayList<>();
        for (Piece[] row : logicBoard) {
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
            if (move.getMovingFrom().equals(loc))
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
            Piece currentPiece = board.getPiece(movesList.get(0).getMovingFrom());
            Move move = (Move) iter.next();
            Location movingTo = move.getMovingTo();
            if (!isInBounds(movingTo)) {
                delete.add(move);
            }
            Piece destination = board.getPiece(movingTo);

            if (destination != null && currentPiece.isOnMyTeam(destination)) {
                delete.add(move);
            }
            if (move instanceof Castling) {
                Castling castling = (Castling) move;
                if (board.isInCheck() || board.isSquareThreatened(castling.getKingMiddleMove(), currentPiece.getOtherColor()) || board.isSquareThreatened(castling.getKingFinalLoc(), currentPiece.getOtherColor())) {
                    delete.add(move);
                    continue;
                }
            } else if (move instanceof EnPassant) {
                //TODO smn
            }
            board.applyMove(move);
            move.setFEN();
            if (board.isInCheck(currentPiece.getPieceColor())) {
                delete.add(move);
            }
            board.undoMove(move);
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
        return logicBoard;
    }

    public void setBoard(Board board) {
        this.logicBoard = board;
    }

    // מהלך מינימקס
    public Move getAiMove() {
        System.out.println("Model getAiMove() using MINIMAX");
        scanDepth = controller.getScanDepth();
        scanTime = controller.getScanTime();
        return getBestMoveUsingMinimax().getMove();
    }

    // ============================ for minimax ===========================
    private MinimaxMove getBestMoveUsingMinimax() {
        minimaxStartedTime = ZonedDateTime.now();
        MinimaxMove ret = minimax(logicBoard, true, 0, Double.MIN_VALUE, Double.MAX_VALUE, null);
        System.out.println("minimax move = " + ret);
        return ret;
    }

    public MinimaxMove minimax(Board board, boolean isMax, int depth, double a, double b, MinimaxMove found) {
        MinimaxMove bestMove = new MinimaxMove(null, new BoardEval(isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE), 9999);
        Player actualPlayer = controller.getCurrentPlayer();
        Player player = isMax ? actualPlayer : Player.getOtherColor(actualPlayer);
        ArrayList<Move> possibleMoves = board.getAllMoves(player);
        BoardEval value = board.getBoardEval();
        long seconds = minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.SECONDS);
        if (value.isGameOver() || seconds >= scanTime || depth == scanDepth) {
            return new MinimaxMove(null, value, depth);
        }
//        if (found != null && found.getMove() != null) {
//            if (isMax && found.getMoveDepth() >= depth && found.getMoveValue().getEval() >= value.getEval()) {
//                return found;
//            } else if (found.getMoveDepth() <= depth && found.getMoveValue().getEval() <= value.getEval()) {
//                return found;
//            }
//        }
        for (int i = 0; i < possibleMoves.size(); i++) {
            Move move = possibleMoves.get(i);
            board.applyMove(move);
            MinimaxMove val = minimax(board, !isMax, depth + 1, a, b, bestMove);
            val.setMove(move);
            board.undoMove(move);
//            if (depth == 0) {
//                System.out.println("Move#" + (i + 1) + ": " + val);
//            }
            double bestMoveEval = bestMove.getMoveValue().getEval(), valMoveValue = val.getMoveValue().getEval();
            int bestMoveDepth = bestMove.getMoveDepth(), valMoveDepth = val.getMoveDepth();
            if (isMax) {
                if (bestMoveEval < valMoveValue) {
                    bestMove = new MinimaxMove(val);
                } else if (depth == 0 && bestMoveEval == valMoveValue) {
                    if (bestMoveDepth > valMoveDepth)
                        bestMove = new MinimaxMove(val);
                }
                a = Math.max(a, bestMove.getMoveValue().getEval());

            } else {
                if (bestMoveEval > valMoveValue) {
                    bestMove = new MinimaxMove(val);
                }
                b = Math.min(b, bestMove.getMoveValue().getEval());
            }
            if (b <= a) break;

        }
        return bestMove;
    }

    public void printAllPossibleMoves() {
        for (Move move : logicBoard.getAllMovesForCurrentPlayer()) {
            System.out.println(move);
        }
    }
}


