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

    public BoardEval getEvaluation(int player) {
        return getEvaluation(player, true);
    }

    public BoardEval getEvaluation(int player, boolean isMax) {
        BoardEval checkGameOver = board.isGameOver(player);
        if (checkGameOver.isGameOver()) return checkGameOver;

        double ret = 0;

        //Material
        ret += compareMaterial(player);

        //Piece Tables
        ret += comparePieceTables(player);

        ret += compareKingSafetyTables(player);

        BoardEval retEval = new BoardEval(ret, GameStatus.GAME_GOES_ON);

        BoardEval captureMovesEval = null;
//
//        ArrayList<Move> allMoves = board.getAllMoves(isMax&&board.getCurrentPlayer()==player ? player : Player.getOtherColor(player));
//
//        for (Move move : allMoves) {
//            if (move.isCapturing()) {
//                board.applyMove(move);
//                BoardEval eval = board.getBoardEval(player);
//                if(captureMovesEval==null){
//                    if
//                }
//                board.undoMove(move);
//            }
//        }

//        return captureMovesEval == null ? retEval : captureMovesEval;
        return retEval;
    }

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