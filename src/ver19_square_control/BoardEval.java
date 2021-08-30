package ver19_square_control;

import ver19_square_control.types.Piece;

import java.util.ArrayList;

import static ver19_square_control.types.Piece.Player;


public class BoardEval {
    private static final double winEval = 1000, tieEval = 0;
    private double eval;
    private GameStatus gameStatus;

    public BoardEval(double eval, GameStatus gameStatus) {
        this.eval = eval;
        this.gameStatus = gameStatus;
    }

    public BoardEval(double eval) {
        this.eval = eval;
        gameStatus = GameStatus.GAME_GOES_ON;
    }

    public BoardEval(GameStatus gameStatus, int side) {
        this.gameStatus = gameStatus;
        gameStatus.setSide(side);
        double res;
        switch (gameStatus) {
            case TIMED_OUT:
            case CHECKMATE:
                res = winEval * gameStatus.getSideMult();
                break;
            case STALEMATE:
            case THREE_FOLD_REPETITION:
            case INSUFFICIENT_MATERIAL:
            case TIME_OUT_VS_INSUFFICIENT_MATERIAL:
                res = tieEval;
                break;
            default:
                res = 0;
        }
        eval = res;
    }

    public BoardEval() {
        this.gameStatus = GameStatus.GAME_GOES_ON;
    }

    public BoardEval(BoardEval other) {
        eval = other.eval;
        gameStatus = other.gameStatus;
    }

    /**
     * side not relevant (draw)
     *
     * @param gameStatus
     */
    public BoardEval(GameStatus gameStatus) {
        this(gameStatus, GameStatus.SIDE_NOT_RELEVANT);
    }

    public double getEval() {
        return eval;
    }

    public void setEval(double eval) {
        this.eval = eval;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public boolean isGameOver() {
        return gameStatus != GameStatus.GAME_GOES_ON;
    }

    @Override
    public String toString() {
        return "Eval{" +
                "eval=" + eval +
                ", gameStatus=" + gameStatus +
                '}';
    }
}

class Eval {
    private Board board;
    //region simple tables
    private int pawnsTable[][] = {
            {0, 0, 0, 0, 0, 0, 0, 0,},
            {50, 50, 50, 50, 50, 50, 50, 50,},
            {10, 10, 20, 30, 30, 20, 10, 10,},
            {5, 5, 10, 25, 25, 10, 5, 5,},
            {0, 0, 0, 20, 20, 0, 0, 0,},
            {5, -5, -10, 0, 0, -10, -5, 5,},
            {5, 10, 10, -20, -20, 10, 10, 5,},
            {0, 0, 0, 0, 0, 0, 0, 0},
    };
    private int knightsTable[][] = {
            {-50, -40, -30, -30, -30, -30, -40, -50,},
            {-40, -20, 0, 0, 0, 0, -20, -40,},
            {-30, 0, 10, 15, 15, 10, 0, -30,},
            {-30, 5, 15, 20, 20, 15, 5, -30,},
            {-30, 0, 15, 20, 20, 15, 0, -30,},
            {-30, 5, 10, 15, 15, 10, 5, -30,},
            {-40, -20, 0, 5, 5, 0, -20, -40,},
            {-50, -40, -30, -30, -30, -30, -40, -50,},
    };
    private int bishopsTable[][] = {
            {-20, -10, -10, -10, -10, -10, -10, -20,},
            {-10, 0, 0, 0, 0, 0, 0, -10,},
            {-10, 0, 5, 10, 10, 5, 0, -10,},
            {-10, 5, 5, 10, 10, 5, 5, -10,},
            {-10, 0, 10, 10, 10, 10, 0, -10,},
            {-10, 10, 10, 10, 10, 10, 10, -10,},
            {-10, 5, 0, 0, 0, 0, 5, -10,},
            {-20, -10, -10, -10, -10, -10, -10, -20,},
    };
    private int rooksTable[][] = {
            {0, 0, 0, 0, 0, 0, 0, 0,},
            {5, 10, 10, 10, 10, 10, 10, 5,},
            {-5, 0, 0, 0, 0, 0, 0, -5,},
            {-5, 0, 0, 0, 0, 0, 0, -5,},
            {-5, 0, 0, 0, 0, 0, 0, -5,},
            {-5, 0, 0, 0, 0, 0, 0, -5,},
            {-5, 0, 0, 0, 0, 0, 0, -5,},
            {0, 0, 0, 5, 5, 0, 0, 0},
    };
    private int queensTable[][] = {
            {-20, -10, -10, -5, -5, -10, -10, -20,},
            {-10, 0, 0, 0, 0, 0, 0, -10,},
            {-10, 0, 5, 5, 5, 5, 0, -10,},
            {-5, 0, 5, 5, 5, 5, 0, -5,},
            {0, 0, 5, 5, 5, 5, 0, -5,},
            {-10, 5, 5, 5, 5, 5, 0, -10,},
            {-10, 0, 5, 0, 0, 0, 0, -10,},
            {-20, -10, -10, -5, -5, -10, -10, -20},
    };
    private int kingsMiddleGameTable[][] = {
            {-30, -40, -40, -50, -50, -40, -40, -30,},
            {-30, -40, -40, -50, -50, -40, -40, -30,},
            {-30, -40, -40, -50, -50, -40, -40, -30,},
            {-30, -40, -40, -50, -50, -40, -40, -30,},
            {-20, -30, -30, -40, -40, -30, -30, -20,},
            {-10, -20, -20, -20, -20, -20, -20, -10,},
            {20, 20, 0, 0, 0, 0, 20, 20,},
            {20, 30, 10, 0, 0, 10, 30, 20},
    };
    private int kingsEndGameTable[][] = {
            {-50, -40, -30, -20, -20, -30, -40, -50,},
            {-30, -20, -10, 0, 0, -10, -20, -30,},
            {-30, -10, 20, 30, 30, 20, -10, -30,},
            {-30, -10, 30, 40, 40, 30, -10, -30,},
            {-30, -10, 30, 40, 40, 30, -10, -30,},
            {-30, -10, 20, 30, 30, 20, -10, -30,},
            {-30, -30, 0, 0, 0, 0, -30, -30,},
            {-50, -30, -30, -30, -30, -30, -30, -50},
    };

    //endregion
    public Eval(Board board) {
        this.board = board;
    }

    public BoardEval getEvaluation(int player) {
        BoardEval checkGameOver = board.isGameOver(player);
        if (checkGameOver.isGameOver()) return checkGameOver;

        double ret = 0;
        //Material
        ret += compareMaterial(player);

        //Piece Tables
        ret += comparePieceTables(player);

        ret += compareKingSafetyTables(player);
        return new BoardEval(ret, GameStatus.GAME_GOES_ON);
    }


    private double compareKingSafetyTables(int player) {
        return 0;
    }

    private double comparePieceTables(int player) {
        double ret = 0;
        Tables tables = new Tables();
        int phase = board.getGamePhase();

        ArrayList<Piece> playerPieces = board.getPlayersPieces(player);
        for (int i = 0, playerPiecesSize = playerPieces.size(); i < playerPiecesSize; i++) {
            Piece piece = playerPieces.get(i);
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
        for (int i = 0, opponentPiecesSize = opponentPieces.size(); i < opponentPiecesSize; i++) {
            Piece piece = opponentPieces.get(i);
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


    private double getTableData(int table[][], Location loc) {
        double ret = table[loc.getRow()][loc.getCol()];
        ret /= 100;
        return ret;
    }

    private double compareMaterial(int player) {
        double playerSum = 0;
        ArrayList<Piece> playerPieces = board.getPlayersPieces(player);
        for (int i = 0, playerPiecesSize = playerPieces.size(); i < playerPiecesSize; i++) {
            Piece piece = playerPieces.get(i);
            playerSum += piece.isCaptured() ? 0 : piece.getWorth();
        }
        ArrayList<Piece> opponentPieces = board.getPlayersPieces(Player.getOtherColor(player));
        for (int i = 0, opponentPiecesSize = opponentPieces.size(); i < opponentPiecesSize; i++) {
            Piece piece = opponentPieces.get(i);
            double w = piece.isCaptured() ? 0 : piece.getWorth();
            playerSum -= w;
        }
        return playerSum;
    }

}