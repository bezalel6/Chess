package ver21_square_class.model_classes.eval_classes;

import ver21_square_class.Location;
import ver21_square_class.model_classes.Board;
import ver21_square_class.model_classes.GameStatus;
import ver21_square_class.types.Piece;

import java.util.ArrayList;

import static ver21_square_class.types.Piece.Player;


public class Eval {
    private static final int MAX_DEPTH = 5;
    private final Board board;

    public Eval(Board board) {
        this.board = board;
    }

    public BoardEval getEvaluation(int player) {
        return getEvaluation(player, 0);
    }

    public BoardEval getEvaluation(int player, int depth) {
        BoardEval checkGameOver = board.isGameOver(player);
        if (checkGameOver.isGameOver()) return checkGameOver;

        double ret = 0;

        //Material
        ret += compareMaterial(player);

        //Piece Tables
        ret += comparePieceTables(player);

        ret += compareKingSafetyTables(player);

        BoardEval retEval = new BoardEval(ret, GameStatus.GAME_GOES_ON);

//        if (depth <= MAX_DEPTH) {
//            ArrayList<Move> allCaptureMoves = board.getAllCaptureMoves(depth % 2 == 0 ? player : Player.getOtherColor(player));
//            for (Move move : allCaptureMoves) {
//                board.applyMove(move);
//                BoardEval e = board.getEval().getEvaluation(player, depth + 1);
//                double wE = retEval.getEval(), cE = e.getEval();
//                if (wE > cE)
//                    retEval = new BoardEval(e);
//                board.undoMove(move);
//            }
//        }

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
        double playerSum = 0;
        ArrayList<Piece> playerPieces = board.getPlayersPieces(player);
        for (Piece piece : playerPieces) {
            playerSum += piece.isCaptured() ? 0 : piece.getWorth();
        }
        ArrayList<Piece> opponentPieces = board.getPlayersPieces(Player.getOtherColor(player));
        for (Piece piece : opponentPieces) {
            double w = piece.isCaptured() ? 0 : piece.getWorth();
            playerSum -= w;
        }
        return playerSum;
    }

}