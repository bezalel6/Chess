package ver23_minimax_levels.model_classes.eval_classes;

import ver23_minimax_levels.Location;
import ver23_minimax_levels.model_classes.Board;
import ver23_minimax_levels.model_classes.GameStatus;
import ver23_minimax_levels.model_classes.Square;
import ver23_minimax_levels.moves.Move;
import ver23_minimax_levels.types.Piece;

import java.util.ArrayList;
import java.util.HashMap;

import static ver23_minimax_levels.types.Piece.*;


public class Eval {
    public static final int NUM_OF_EVAL_PARAMETERS = 6;
    public static final int MATERIAL = 0, PIECE_TABLES = 1, KING_SAFETY = 2, HANGING_PIECES = 3, SQUARE_CONTROL = 4, MOVEMENT_ABILITY = 5;
    public static final String[] EVAL_PARAMETERS_NAMES = initEvalParametersArr();
    private static final HashMap<Long, Evaluation> evaluationHashMap = new HashMap<>();
    private static final int NORMAL_CLOSE_2_ENEMY_EVAL = 1;
    private static final double[] CLOSE_2_ENEMY = initClose2EnemyArr();
    private static final int MAX_DEPTH = 10;
    private final Board board;

    public Eval(Board board) {
        this.board = board;
    }

    private static double[] initClose2EnemyArr() {
        double[] ret = new double[ROWS];
        ret[0] = NORMAL_CLOSE_2_ENEMY_EVAL * 2;
        ret[1] = NORMAL_CLOSE_2_ENEMY_EVAL * 1.8;
        ret[2] = ret[3] = ret[4] = NORMAL_CLOSE_2_ENEMY_EVAL * 1.6;
        ret[5] = ret[6] = NORMAL_CLOSE_2_ENEMY_EVAL * 1.2;
        ret[7] = NORMAL_CLOSE_2_ENEMY_EVAL;
        return ret;
    }

    private static String[] initEvalParametersArr() {
        String[] ret = new String[NUM_OF_EVAL_PARAMETERS];
        ret[MATERIAL] = "Material";
        ret[PIECE_TABLES] = "Piece Tables";
        ret[KING_SAFETY] = "King Safety";
        ret[HANGING_PIECES] = "Hanging Pieces";
        ret[SQUARE_CONTROL] = "Square Control";
        ret[MOVEMENT_ABILITY] = "Movement Ability";
        return ret;
    }

    public Evaluation getEvaluation(int player) {
//        long hash = board.getBoardHash();
        //todo player?
//        long hash = board.getBoardHash(player);
//        if (evaluationHashMap.containsKey(hash))
//            return evaluationHashMap.get(hash);
        Evaluation evaluation = getEvaluation_(player);
//        evaluationHashMap.put(hash, new Evaluation(evaluation));
        return evaluation;
    }

    private Evaluation getEvaluation_(int player) {

        Evaluation checkGameOver = board.isGameOver(player);
        if (checkGameOver.isGameOver()) return checkGameOver;

        Evaluation retEval = new Evaluation();

//        Check
        if (board.isInCheck(Player.getOpponent(player))) {
            retEval.setGameStatus(GameStatus.CHECK);
        }
        //Material
        retEval.addDetail(MATERIAL, compareMaterial(player));

        //Piece Tables
        retEval.addDetail(PIECE_TABLES, comparePieceTables(player));

        //Hanging Pieces
//        retEval.addDetail(HANGING_PIECES, calcHangingPieces(player));

        //Square Control
//        retEval.addDetail(SQUARE_CONTROL, compareSquaresControl(player));

//        Movement Ability
//        retEval.addDetail(MOVEMENT_ABILITY, compareMovementAbility(player));

        //King Safety
//        retEval.addDetail(KING_SAFETY, compareKingSafety(player));

        return retEval;
    }

    private double compareMovementAbility(int player) {
        return movementAbility(player) - movementAbility(Player.getOpponent(player));
    }

    private double movementAbility(int player) {
        double ret = 0;
        for (Piece piece : board.getPlayersPieces(player))
            ret += ((double) piece.getPseudoMovesList(board).size()) / 1000;
        return ret;
    }

    private double compareSquaresControl(int player) {
        return squaresControl(player) - squaresControl(Player.getOpponent(player));
    }

    private double squaresControl(int player) {
        double ret = 0;
        for (Piece piece : board.getPlayersPieces(player)) {
            ArrayList<Move> moves = piece.getPseudoMovesList(board);
            for (Move move : moves) {
                ret += close2EnemyScore(move.getMovingTo(), player);
            }
        }
        return ret;
    }

    private double close2EnemyScore(Location loc, int player) {
        int opponentStartingRow = Piece.getStartingRow(Player.getOpponent(player));
        int distance = Math.abs(loc.getRow() - opponentStartingRow);
        return CLOSE_2_ENEMY[distance] / 100;
    }

    private double compareHangingPieces(int player) {
        return calcHangingPieces(player) + calcHangingPieces(Player.getOpponent(player));
    }

    private double calcHangingPieces(int player) {
        double playerSum = 0;
        int divByTurn = board.getCurrentPlayer() == player ? 100 : 1;
        for (Piece piece : board.getPlayersPieces(player)) {
            int attacking = 0;
            int protecting = 0;
            double tSum = 0;
            for (Piece lookingAtMe : board.piecesLookingAt(piece)) {
            }
        }
        return playerSum;
    }

    private double compareKingSafety(int player) {
        double ret = 0;
        int movesNum = board.getPieceMovesFrom(board.getKing(player).getLoc(), QUEEN, player).size();
        ret -= Math.pow(.5 * movesNum, -.1);
        return ret;
    }

    private double comparePieceTables(int player) {
        double ret = 0;
        Tables tables = new Tables();
        int phase = board.getGamePhase();

        for (ArrayList<Piece> playersPieces : board.getPiecesLists()) {
            for (Piece piece : playersPieces) {
                int currentPieceColor = piece.getPieceColor();
                int mult = currentPieceColor == player ? 1 : -1;
                Location loc = piece.getLoc();
                int[][] table = tables.getTable(piece.getPieceType(), phase, currentPieceColor);
                ret += getTableData(table, loc) * mult;
            }
        }
        return ret;
    }

    private double getTableData(int[][] table, Location loc) {
        double ret = table[loc.getRow()][loc.getCol()];
        ret /= 100;
        return ret;
    }

    private double compareMaterial(int player) {
        double playerSum = materialSum(board.getPiecesCount(player));
        playerSum -= materialSum(board.getPiecesCount(Player.getOpponent(player)));
        return playerSum;
    }

    private double slowMaterialCompare(int player) {
        double ret = 0;
        for (Square[] row : board) {
            for (Square square : row) {
                if (!square.isEmpty()) {
                    Piece piece = square.getPiece();
                    if (piece.isOnMyTeam(player))
                        ret += piece.getWorth();
                    else ret -= piece.getWorth();
                }
            }
        }
        return ret;
    }

    private double materialSum(int[] piecesCountArr) {
        double ret = 0;
        for (int pieceType = 0; pieceType < piecesCountArr.length; pieceType++) {
            ret += Piece.WORTH[pieceType] * piecesCountArr[pieceType];
        }
        return ret;
    }

}