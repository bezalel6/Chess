package ver17_new_movement;

import ver17_new_movement.types.Piece.Player;
import ver17_new_movement.types.Piece;

import java.util.ArrayList;

import static ver17_new_movement.Board.GamePhase;
import static ver17_new_movement.types.Piece.*;


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

    public BoardEval(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        double res;
        switch (gameStatus) {
            case OPPONENT_TIMED_OUT:
            case CHECKMATE:
                res = winEval;
                break;
            case STALEMATE:
            case REPETITION:
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

class Tables {

    private PieceTable knight = new PieceTable(
            new int[][]{
                    {-167, -89, -34, -49, 61, -97, -15, -107,},
                    {-73, -41, 72, 36, 23, 62, 7, -17,},
                    {-47, 60, 37, 65, 84, 129, 73, 44,},
                    {-9, 17, 19, 53, 37, 69, 18, 22,},
                    {-13, 4, 16, 13, 28, 19, 21, -8,},
                    {-23, -9, 12, 10, 19, 17, 25, -16,},
                    {-29, -53, -12, -3, -1, 18, -14, -19,},
                    {-105, -21, -58, -33, -17, -28, -19, -23,},
            },
            new int[][]{
                    {-58, -38, -13, -28, -31, -27, -63, -99,},
                    {-25, -8, -25, -2, -9, -25, -24, -52,},
                    {-24, -20, 10, 9, -1, -9, -19, -41,},
                    {-17, 3, 22, 22, 22, 11, 8, -18,},
                    {-18, -6, 16, 25, 16, 17, 4, -18,},
                    {-23, -3, -1, 15, 10, -3, -20, -22,},
                    {-42, -20, -10, -5, -2, -20, -23, -44,},
                    {-29, -51, -23, -15, -22, -18, -50, -64,},
            }
    );
    private PieceTable pawn = new PieceTable(
            new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 0,},
                    {98, 134, 61, 95, 68, 126, 34, -11,},
                    {-6, 7, 26, 31, 65, 56, 25, -20,},
                    {-14, 13, 6, 21, 23, 12, 17, -23,},
                    {-27, -2, -5, 12, 17, 6, 10, -25,},
                    {-26, -4, -4, -10, 3, 3, 33, -12,},
                    {-35, -1, -20, -23, -15, 24, 38, -22,},
                    {0, 0, 0, 0, 0, 0, 0, 0,},
            },
            new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 0,},
                    {178, 173, 158, 134, 147, 132, 165, 187,},
                    {94, 100, 85, 67, 56, 53, 82, 84,},
                    {32, 24, 13, 5, -2, 4, 17, 17,},
                    {13, 9, -3, -7, -7, -8, 3, -1,},
                    {4, 7, -6, 1, 0, -5, -1, -8,},
                    {13, 8, 8, 10, 13, 0, 2, -7,},
                    {0, 0, 0, 0, 0, 0, 0, 0,},
            }
    );
    private PieceTable bishop = new PieceTable(
            new int[][]{
                    {-29, 4, -82, -37, -25, -42, 7, -8,},
                    {-26, 16, -18, -13, 30, 59, 18, -47,},
                    {-16, 37, 43, 40, 35, 50, 37, -2,},
                    {-4, 5, 19, 50, 37, 37, 7, -2,},
                    {-6, 13, 13, 26, 34, 12, 10, 4,},
                    {0, 15, 15, 15, 14, 27, 18, 10,},
                    {4, 15, 16, 0, 7, 21, 33, 1,},
                    {-33, -3, -14, -21, -13, -12, -39, -21,},
            }, new int[][]{
            {-14, -21, -11, -8, -7, -9, -17, -24,},
            {-8, -4, 7, -12, -3, -13, -4, -14,},
            {2, -8, 0, -1, -2, 6, 0, 4,},
            {-3, 9, 12, 9, 14, 10, 3, 2,},
            {-6, 3, 13, 19, 7, 10, -3, -9,},
            {-12, -3, 8, 10, 13, 3, -7, -15,},
            {-14, -18, -7, -1, 4, -9, -15, -27,},
            {-23, -9, -23, -5, -9, -16, -5, -17,},
    }
    );
    private PieceTable rook = new PieceTable(
            new int[][]{
                    {32, 42, 32, 51, 63, 9, 31, 43,},
                    {27, 32, 58, 62, 80, 67, 26, 44,},
                    {-5, 19, 26, 36, 17, 45, 61, 16,},
                    {-24, -11, 7, 26, 24, 35, -8, -20,},
                    {-36, -26, -12, -1, 9, -7, 6, -23,},
                    {-45, -25, -16, -17, 3, 0, -5, -33,},
                    {-44, -16, -20, -9, -1, 11, -6, -71,},
                    {-19, -13, 1, 17, 16, 7, -37, -26,},
            },
            new int[][]{
                    {13, 10, 18, 15, 12, 12, 8, 5,},
                    {11, 13, 13, 11, -3, 3, 8, 3,},
                    {7, 7, 7, 5, 4, -3, -5, -3,},
                    {4, 3, 13, 1, 2, 1, -1, 2,},
                    {3, 5, 8, 4, -5, -6, -8, -11,},
                    {-4, 0, -5, -1, -7, -12, -8, -16,},
                    {-6, -6, 0, 2, -9, -9, -11, -3,},
                    {-9, 2, 3, -1, -5, -13, 4, -20,},
            }
    );
    private PieceTable queen = new PieceTable(
            new int[][]{
                    {-28, 0, 29, 12, 59, 44, 43, 45,},
                    {-24, -39, -5, 1, -16, 57, 28, 54,},
                    {-13, -17, 7, 8, 29, 56, 47, 57,},
                    {-27, -27, -16, -16, -1, 17, -2, 1,},
                    {-9, -26, -9, -10, -2, -4, 3, -3,},
                    {-14, 2, -11, -2, -5, 2, 14, 5,},
                    {-35, -8, 11, 2, 8, 15, -3, 1,},
                    {-1, -18, -9, 10, -15, -25, -31, -50,},
            },
            new int[][]{
                    {-9, 22, 22, 27, 27, 19, 10, 20,},
                    {-17, 20, 32, 41, 58, 25, 30, 0,},
                    {-20, 6, 9, 49, 47, 35, 19, 9,},
                    {3, 22, 24, 45, 57, 40, 57, 36,},
                    {-18, 28, 19, 47, 31, 34, 39, 23,},
                    {-16, -27, 15, 6, 9, 17, 10, 5,},
                    {-22, -23, -30, -16, -16, -23, -36, -32,},
                    {-33, -28, -22, -43, -5, -32, -20, -41,},
            }
    );
    private PieceTable king = new PieceTable(
            new int[][]{
                    {-65, 23, 16, -15, -56, -34, 2, 13,},
                    {29, -1, -20, -7, -8, -4, -38, -29,},
                    {-9, 24, 2, -16, -20, 6, 22, -22,},
                    {-17, -20, -12, -27, -30, -25, -14, -36,},
                    {-49, -1, -27, -39, -46, -44, -33, -51,},
                    {-14, -14, -22, -46, -44, -30, -15, -27,},
                    {1, 7, -8, -64, -43, -16, 9, 8,},
                    {-15, 36, 12, -54, 8, -28, 24, 14,},
            },
            new int[][]{
                    {-74, -35, -18, -18, -11, 15, 4, -17,},
                    {-12, 17, 14, 17, 17, 38, 23, 11,},
                    {10, 17, 23, 15, 20, 45, 44, 13,},
                    {-8, 22, 24, 27, 26, 33, 26, 3,},
                    {-18, -4, 21, 24, 27, 23, 9, -11,},
                    {-19, -3, 11, 21, 23, 16, 7, -9,},
                    {-27, -11, 4, 13, 14, 4, -5, -17,},
                    {-53, -34, -21, -11, -28, -14, -24, -43},
            }
    );

    public int[][] getTable(int type, GamePhase phase) {
        switch (type) {
            case KNIGHT:
                return knight.getByIndex(phase);
            case KING:
                return king.getByIndex(phase);
            case ROOK:
                return rook.getByIndex(phase);
            case QUEEN:
                return queen.getByIndex(phase);
            case BISHOP:
                return bishop.getByIndex(phase);
            default:
                return pawn.getByIndex(phase);
        }
    }

    class PieceTable {
        private int middleGame[][];
        private int endGame[][];

        public PieceTable(int[][] middleGame, int[][] endGame) {
            this.middleGame = middleGame;
            this.endGame = endGame;
        }

        public int[][] getMiddleGame() {
            return middleGame;
        }

        public int[][] getEndGame() {
            return endGame;
        }

        public int[][] getByIndex(GamePhase phase) {
            switch (phase) {
                case MIDDLE_GAME:
                    return middleGame;
                case END_GAME:
                    return endGame;
                default:
                    return null;
            }
        }
    }
}

class Eval {
    private Board board;
    //region tables
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
        BoardEval checkGameOver = board.isGameOver();
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
        GamePhase phase = board.getGameStage();
        for (Piece[] row : board) {
            for (Piece piece : row) {
                if (piece != null) {
                    int multi;
                    Location loc = piece.getLoc();
                    int[][] table = tables.getTable(piece.getPieceType(), phase);
                    if (player == Player.WHITE) {
//                        loc = Location.convertToMatLoc(loc);
                    }
                    if (piece.isWhite())
                        table = reverse(table);
                    if (player != piece.getPieceColor()) {
                        multi = -1;
                    } else multi = 1;
                    int data = getTableData(table, loc) * multi;
                    ret += data;
                }
            }
        }
        return ret * 0.001;
    }

    private int[][] reverse(int arr[][]) {
        int[][] ret = new int[arr.length][arr[0].length];
        for (int i = 0, j = arr.length - 1; i < arr.length; i++, j--) {
            ret[i] = arr[j];
        }
        return ret;
    }

    private int getTableData(int table[][], Location loc) {
        return table[loc.getRow()][loc.getCol()];
    }

    private int compareMaterial(int player) {
        ArrayList<Piece> currentPlayerPieces = this.board.getPlayersPieces(player);
        ArrayList<Piece> opponentPieces = this.board.getPlayersPieces(Player.getOtherColor(player));
        int playerSum = 0, opponentSum = 0;
        for (Piece piece : currentPlayerPieces)
            playerSum += piece.getWorth();
        for (Piece piece : opponentPieces)
            opponentSum += piece.getWorth();
        return playerSum - opponentSum;
    }

}