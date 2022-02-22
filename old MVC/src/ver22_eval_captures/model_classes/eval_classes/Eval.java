package ver22_eval_captures.model_classes.eval_classes;

import ver22_eval_captures.Location;
import ver22_eval_captures.model_classes.Board;
import ver22_eval_captures.model_classes.GameStatus;
import ver22_eval_captures.moves.Move;
import ver22_eval_captures.types.Piece;

import java.util.ArrayList;

import static ver22_eval_captures.types.Piece.Player;


public class Eval {
    private static final int MAX_DEPTH = 5;
    private final Board board;

    public Eval(Board board) {
        this.board = board;
    }


    public Evaluation getEvaluation(int player) {
        return getEvaluation(player, true, true);
    }

    public Evaluation getEvaluation(int player, boolean search, boolean isMax) {
        Evaluation checkGameOver = board.isGameOver(player);
        if (checkGameOver.isGameOver()) return checkGameOver;

        double evalSum = 0;

        //Material
        evalSum += compareMaterial(player);

        //Piece Tables
        evalSum += comparePieceTables(player);

        evalSum += compareKingSafetyTables(player);

        Evaluation retEval = new Evaluation(evalSum, GameStatus.GAME_GOES_ON);

//        if (search) {
//            double initValue = isMax ? Double.MIN_VALUE : Double.MAX_VALUE;
//            Evaluation bestMove = new Evaluation(initValue);
//
//            int currentPlayer = board.getCurrentPlayer();
//
//            int movingPlayer = isMax ? currentPlayer : Player.getOtherColor(currentPlayer);
//            ArrayList<Move> allMoves = board.getAllMoves(movingPlayer);
//
//            boolean foundCapture = false;
//            for (Move move : allMoves) {
//                if (move.isCapturing()) {
//                    foundCapture = true;
//                    break;
//                }
//            }
//
//            if (foundCapture) {
//                Eval e = board.getEval();
//                for (Move move : allMoves) {
//                    board.applyMove(move);
//                    boolean isCapturing = move.isCapturing();
//                    Evaluation tempEval = e.getEvaluation(player, isCapturing, !isMax);
//                    if (isMax) {
//                        if (tempEval.isGreaterThan(bestMove))
//                            bestMove = new Evaluation(tempEval);
//                    } else {
//                        if (bestMove.isGreaterThan(tempEval))
//                            bestMove = new Evaluation(tempEval);
//                    }
//                    board.undoMove(move);
//                }
//                if (bestMove.getEval() != initValue)
//                    retEval = bestMove;
//            }
//        }
        return retEval;
    }
//    public Evaluation getEvaluation(int player, boolean search, boolean isMax) {
//        Evaluation checkGameOver = board.isGameOver(player);
//        if (checkGameOver.isGameOver()) return checkGameOver;
//
//        double evalSum = 0;
//
//        //Material
//        evalSum += compareMaterial(player);
//
//        //Piece Tables
//        evalSum += comparePieceTables(player);
//
//        evalSum += compareKingSafetyTables(player);
//
//        Evaluation retEval = new Evaluation(evalSum, GameStatus.GAME_GOES_ON);
//
//        if (search) {
//            double initValue = isMax ? Double.MIN_VALUE : Double.MAX_VALUE;
//            Evaluation capturingMovesEval = new Evaluation(initValue);
//            Evaluation normalMovesEval = new Evaluation(initValue);
//
//            int currentPlayer = board.getCurrentPlayer();
//
//            int movingPlayer = isMax ? currentPlayer : Player.getOtherColor(currentPlayer);
////            int movingPlayer = isMax ? player : Player.getOtherColor(player);
//            ArrayList<Move> allMoves = board.getAllMoves(movingPlayer);
//
//            boolean foundCapture = false;
//            for (Move move : allMoves) {
//                if (move.isCapturing()) {
//                    foundCapture = true;
//                    break;
//                }
//            }
//
//            if (foundCapture) {
//                for (Move move : allMoves) {
//                    board.applyMove(move);
//                    boolean isCapturing = move.isCapturing();
//                    Eval e = board.getEval();
//                    Evaluation tempEval = e.getEvaluation(player, isCapturing, !isMax);
//                    if (isMax) {
//                        if (isCapturing) {
//                            if (tempEval.isGreaterThan(capturingMovesEval)) {
//                                capturingMovesEval = new Evaluation(tempEval);
//                            }
//                        } else {
//                            if (tempEval.isGreaterThan(normalMovesEval)) {
//                                normalMovesEval = new Evaluation(tempEval);
//                            }
//                        }
//                    } else {
//                        if (isCapturing) {
//                            if (capturingMovesEval.isGreaterThan(tempEval)) {
//                                capturingMovesEval = new Evaluation(tempEval);
//                            }
//                        } else {
//                            if (normalMovesEval.isGreaterThan(tempEval)) {
//                                normalMovesEval = new Evaluation(tempEval);
//                            }
//                        }
//                    }
//                    board.undoMove(move);
//                }
//                if (capturingMovesEval.getEval() == initValue || normalMovesEval.getEval() == initValue)
//                    return retEval;
//                if (isMax) {
//                    return capturingMovesEval.isGreaterThan(normalMovesEval) ? capturingMovesEval : normalMovesEval;
//                }
//                return capturingMovesEval.isGreaterThan(normalMovesEval) ? normalMovesEval : capturingMovesEval;
//            }
//        }
//        return retEval;
//    }

    private double compareKingSafetyTables(int player) {
        return 0;
    }

    private double comparePieceTables(int player) {
        double ret = 0;
        Tables tables = new Tables();
        int phase = board.getGamePhase();

        ArrayList<Piece> playerPieces = board.getPlayersPieces(player);
        for (Piece piece : playerPieces) {
            if (!piece.isCaptured()) {
                int currentPieceColor = piece.getPieceColor();
                Location loc = piece.getLoc();
                int[][] table = tables.getTable(piece.getPieceType(), phase, currentPieceColor);
                double d = getTableData(table, loc);
                ret += d;
            }
        }
        double op = 0;
        ArrayList<Piece> opponentPieces = board.getPlayersPieces(Player.getOtherColor(player));
        for (Piece piece : opponentPieces) {
            if (!piece.isCaptured()) {
                int currentPieceColor = piece.getPieceColor();
                Location loc = piece.getLoc();
                int[][] table = tables.getTable(piece.getPieceType(), phase, currentPieceColor);
                double d = getTableData(table, loc);
                op += d;
            }
        }
        ret -= op;
        return ret;
    }

    private double getTableData(int[][] table, Location loc) {
        double ret = table[loc.getRow()][loc.getCol()];
        ret /= 100;
        return ret;
    }

    private double compareMaterial(int player) {
        double playerSum = materialSum(board.getPiecesCount(player));
        playerSum -= materialSum(board.getPiecesCount(Player.getOtherColor(player)));
        return playerSum;
    }

    private double materialSum(int[] piecesCountArr) {
        double ret = 0;
        for (int pieceType = 0; pieceType < piecesCountArr.length; pieceType++) {
            ret += Piece.WORTH[pieceType] * piecesCountArr[pieceType];
        }
        return ret;
    }

}