package ver26_save_minimax_levels.model_classes.eval_classes;

import ver26_save_minimax_levels.Location;
import ver26_save_minimax_levels.model_classes.Board;
import ver26_save_minimax_levels.model_classes.GameStatus;
import ver26_save_minimax_levels.model_classes.Square;
import ver26_save_minimax_levels.model_classes.pieces.King;
import ver26_save_minimax_levels.model_classes.pieces.Piece;
import ver26_save_minimax_levels.moves.Move;

import java.util.ArrayList;
import java.util.HashMap;

import static ver26_save_minimax_levels.model_classes.pieces.Piece.*;


public class Eval {
    public static final int NUM_OF_EVAL_PARAMETERS = 7;
    public static final int MATERIAL = 0, PIECE_TABLES = 1, KING_SAFETY = 2, HANGING_PIECES = 3, SQUARE_CONTROL = 4, MOVEMENT_ABILITY = 5, FORCE_KING_TO_CORNER = 6;
    public static final String[] EVAL_PARAMETERS_NAMES = initEvalParametersArr();
    private static final HashMap<Long, Evaluation> evaluationHashMap = new HashMap<>();
    private static final HashMap<Long, Evaluation> gameOverHashMap = new HashMap<>();
    private final Board board;
    private double egWeight;

    public Eval(Board board) {
        this.board = board;
    }

    private static String[] initEvalParametersArr() {
        String[] ret = new String[NUM_OF_EVAL_PARAMETERS];
        ret[MATERIAL] = "Material";
        ret[PIECE_TABLES] = "Piece Tables";
        ret[KING_SAFETY] = "King Safety";
        ret[HANGING_PIECES] = "Hanging Pieces";
        ret[SQUARE_CONTROL] = "Square Control";
        ret[MOVEMENT_ABILITY] = "Movement Ability";
        ret[FORCE_KING_TO_CORNER] = "Force King To Corner Endgame Eval";
        return ret;
    }


    private static double calcClose(int distance) {
        double num = Math.exp(distance);
        if (distance <= 4) {
            num = Math.exp(distance);
        }
        num = Math.floor(num);
        return num + "".length();
    }

    static double materialSumWithoutPawns(Board board, int player) {

    }

    public Evaluation getEvaluation() {
        return getEvaluation(board.getCurrentPlayer());
    }

    public Evaluation getEvaluation(int player) {
        long hash = board.getBoardHash(player);
        if (evaluationHashMap.containsKey(hash)) {
            return evaluationHashMap.get(hash);
        }
        Evaluation evaluation = getEvaluation_(player);
        evaluationHashMap.put(hash, new Evaluation(evaluation));
        return evaluation;
    }

    public Evaluation getCapturesEvaluation() {
        return getCapturesEvaluation(board.getCurrentPlayer());
    }

    public Evaluation getCapturesEvaluation(int player) {
        return getCapturesEvaluation_(player, true);
    }

    private Evaluation getCapturesEvaluation_(int player, boolean isMax) {
        Evaluation currentEval = getEvaluation(player);
        ArrayList<Move> allCaptureMoves = board.getAllCaptureMoves();
        for (Move move : allCaptureMoves) {
            board.applyMove(move);
            Evaluation captures = getCapturesEvaluation_(player, !isMax);
            board.undoMove(move);
            if (isMax == captures.isGreaterThan(currentEval)) {
                currentEval = new Evaluation(captures);
            }
        }
        return currentEval;
    }

    private Evaluation getEvaluation_(int player) {

        Evaluation checkGameOver = isGameOver();
        if (checkGameOver.isGameOver()) return checkGameOver;

        Evaluation retEval = new Evaluation();
        egWeight = GamePhase.endgameWeight(board, player);
//        Check
        if (board.isInCheck(player)) {
            retEval.setGameStatus(GameStatus.CHECK);
        }
        //Material
        retEval.addDetail(MATERIAL, compareMaterial(player));

        //Piece Tables
        retEval.addDetail(PIECE_TABLES, comparePieceTables(player));

//        force king to corner
        retEval.addDetail(FORCE_KING_TO_CORNER, forceKingToCornerEndgameEval(player));
        //Hanging Pieces
//        retEval.addDetail(HANGING_PIECES, calcHangingPieces(player));

        //Square Control
//        retEval.addDetail(SQUARE_CONTROL, compareSquareControl(player));

//        Movement Ability
//        retEval.addDetail(MOVEMENT_ABILITY, compareMovementAbility(player));

        //King Safety
//        retEval.addDetail(KING_SAFETY, compareKingSafety(player));

        return retEval;
    }

    private double forceKingToCornerEndgameEval(int player) {
        double ret = 0;
        King opK = board.getKing(Player.getOpponent(player));

        int opRow = opK.getLoc().getRow(), opCol = opK.getLoc().getCol();

        int opDstToCenterCol = Math.max(3 - opCol, opCol - 4);
        int opDstToCenterRow = Math.max(3 - opRow, opRow - 4);
        int opKDstFromCenter = opDstToCenterCol + opDstToCenterRow;
        ret += opKDstFromCenter;

        King myK = board.getKing(player);

        int myRow = myK.getLoc().getRow(), myCol = myK.getLoc().getCol();

        int kingsColDst = Math.abs(myCol - opCol);
        int kingsRowDst = Math.abs(myRow - opRow);
        int kingsDst = kingsColDst + kingsRowDst;
        ret += 14 - kingsDst;

        return ret * 0.10 * new GamePhase().endgameWeight();
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

    private double compareSquareControl(int player) {
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
        return calcClose(distance);
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
                if (lookingAtMe.isOnMyTeam(player)) {
                    protecting++;
                } else {
                    tSum -= piece.getWorth();
                    attacking++;
                }
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
        for (var playersPieces : board.getPieces()) {
            for (Piece piece : playersPieces.values()) {
                int currentPieceColor = piece.getPieceColor();
                int mult = currentPieceColor == player ? 1 : -1;
                Location loc = piece.getLoc();
                ret += getTableData(piece, phase, loc) * mult;
            }
        }
        return ret;
    }

    private double getTableData(Piece piece, GamePhase phase, Location loc) {
        Tables.PieceTable table = Tables.getPieceTable(piece.getPieceType());
        double ret = Tables.getValueForPhase(table, , piece.getPieceColor(), loc);
        return ret;
    }

    private double compareMaterial(int player) {
        return materialSum(player) - materialSum(Player.getOpponent(player));
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

    private double materialSum(int player) {
        double ret = 0;
        int[] piecesCount = board.getPiecesCount(player);
        for (int i = 0, piecesCountLength = piecesCount.length; i < piecesCountLength; i++) {
            int count = piecesCount[i];
            ret += count * WORTH[i];
        }
        return ret;
    }

    public Evaluation isGameOver() {
        int player = board.getCurrentPlayer();
        long hash = board.getBoardHash(player);
        if (gameOverHashMap.containsKey(hash)) {
            return gameOverHashMap.get(hash);
        }
        Evaluation ret;
        int side = player == board.getCurrentPlayer() ? GameStatus.LOSING_SIDE : GameStatus.WINNING_SIDE;
        if (board.getAllMoves(player).isEmpty()) {
            if (board.isInCheck(player)) {
                ret = new Evaluation(GameStatus.CHECKMATE, side);
            } else {
                ret = new Evaluation(GameStatus.STALEMATE);
            }
        } else if (board.getHalfMoveClock() >= 100) {
            ret = new Evaluation(GameStatus.FIFTY_MOVE_RULE);
        } else if (checkRepetition(player)) {
            ret = new Evaluation(GameStatus.THREE_FOLD_REPETITION);
        } else if (checkForInsufficientMaterial()) {
            ret = new Evaluation(GameStatus.INSUFFICIENT_MATERIAL);
        } else {
            ret = new Evaluation();
        }
        gameOverHashMap.put(hash, ret);
        return ret;
    }

    private boolean checkForInsufficientMaterial() {
        return insufficientMaterial(Player.WHITE) &&
                insufficientMaterial(Player.BLACK);
    }

    private boolean insufficientMaterial(int player) {
        return board.getNumOfPieces(player, KING) < 1 || (
                board.getNumOfPieces(player, PAWN) == 0 &&
                        board.getNumOfPieces(player, MINOR_PIECES) <= 1 &&
                        board.getNumOfPieces(player, MAJOR_PIECES) == 0);

    }

    private boolean checkRepetition(int player) {
//        if (board.getRepetitionHashList().size() >= 8) {
//            long currentHash = board.hashBoard(player);
//            int numOfMatches = 0;
////            int minMatch = currentPlayer == player ? 3 : 2;
//            int minMatch = 3;
//            for (long hash : board.getRepetitionHashList()) {
//                if (currentHash == hash) {
//                    numOfMatches++;
//                    if (numOfMatches >= minMatch)
//                        return true;
//                }
//            }
//        }
        return false;
    }

    public static class GamePhase {
        public static final int MIDDLE_GAME = 0, ENDGAME = 1, OPENING = 2;
        private static final double endgameMaterialStart = Piece.getPieceWorth(ROOK) * 2 + Piece.getPieceWorth(BISHOP) + Piece.getPieceWorth(KNIGHT);

        public static double endgameWeight(Board board, int player) {
            double sf = 0;
            int materialWithoutPawns = board.getNumOfPieces(player, )
            return sf;
        }
    }

}