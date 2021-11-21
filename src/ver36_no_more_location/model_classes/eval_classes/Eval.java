package ver36_no_more_location.model_classes.eval_classes;

import ver36_no_more_location.Location;
import ver36_no_more_location.Player;
import ver36_no_more_location.model_classes.Board;
import ver36_no_more_location.model_classes.GameStatus;
import ver36_no_more_location.model_classes.moves.Move;
import ver36_no_more_location.model_classes.pieces.PieceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Eval {
    public static final int NUM_OF_EVAL_PARAMETERS = 9;
    public static final int MATERIAL = 0;
    public static final int PIECE_TABLES = 1;
    public static final int KING_SAFETY = 2;
    public static final int HANGING_PIECES = 3;
    public static final int SQUARE_CONTROL = 4;
    public static final int MOVEMENT_ABILITY = 5;
    public static final int FORCE_KING_TO_CORNER = 6;
    public static final int EG_WEIGHT = 7;
    public static final int STOCKFISH_SAYS = 8;
    public static final String[] EVAL_PARAMETERS_NAMES = initEvalParametersArr();
    public static final int MIDDLE_GAME = 0, ENDGAME = 1, OPENING = 2;
    public static final HashMap<Long, Evaluation> evaluationHashMap = new HashMap<>();
    public static final HashMap<Long, Evaluation> capturesEvaluationHashMap = new HashMap<>();
    private static final HashMap<Long, Evaluation> gameOverHashMap = new HashMap<>();
    private static final double endgameMaterialStart = PieceType.ROOK.value * 2 + PieceType.BISHOP.value + PieceType.KNIGHT.value;
    private final Board board;


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
        ret[EG_WEIGHT] = "Endgame Weight";
        ret[STOCKFISH_SAYS] = "Stockfish Says";
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

    static double materialSumWithoutPAndK(Board board, Player player) {
        double ret = 0;

        int[] pieces = board.getPiecesCount(player);
        for (PieceType type : PieceType.PIECE_TYPES) {
            if (type != PieceType.PAWN && type != PieceType.KING)
                ret += pieces[type.asInt()] * type.value;
        }
        return ret;
    }

    public static double endgameWeight(Board board, Player player) {
        double materialWithoutPawns = materialSumWithoutPAndK(board, player);
        double multiplier = 1 / endgameMaterialStart;
//        double multiplier =1/ endgameMaterialStart;
//        return 1 - materialWithoutPawns * multiplier;
        return 1 - Math.min(1, materialWithoutPawns * multiplier);
    }

    public Evaluation getEvaluation() {
        return getEvaluation(board.getCurrentPlayer());
    }

    public Evaluation getEvaluation(Player player) {
        long hash = board.getBoardHash().getFullHash();
        Evaluation evaluation;
        if (evaluationHashMap.containsKey(hash)) {
            evaluation = evaluationHashMap.get(hash);
        } else {
            evaluation = getEvaluationForWhite();
            evaluationHashMap.put(hash, evaluation);
        }

//        evaluation = getEvaluationForWhite();

        return player == Player.WHITE ? evaluation : evaluation.getEvalForBlack();
    }

    private Evaluation getEvaluationForWhite() {

        Evaluation checkGameOver = checkGameOver();
        if (checkGameOver.isGameOver()) return checkGameOver;

        Evaluation retEval = new Evaluation();
        double egWeight = endgameWeight(board, Player.WHITE);
//        Check
        if (board.isInCheck()) {
            retEval.setGameStatusType(GameStatus.GameStatusType.CHECK);
        }
        retEval.addDebugDetail(EG_WEIGHT, egWeight);

        //Material
        retEval.addDetail(MATERIAL, compareMaterial());

        //Piece Tables
        retEval.addDetail(PIECE_TABLES, comparePieceTables(egWeight));

//        force king to corner
        retEval.addDetail(FORCE_KING_TO_CORNER, compareForceKingToCorner(egWeight));

        //Hanging Pieces
//        retEval.addDetail(HANGING_PIECES, calcHangingPieces(player));

        //Square Control
//        retEval.addDetail(SQUARE_CONTROL, compareSquareControl(player));

//        Movement Ability
//        retEval.addDetail(MOVEMENT_ABILITY, compareMovementAbility(player));

        //King Safety
        retEval.addDetail(KING_SAFETY, compareKingSafety());

//        retEval.addDetail(STOCKFISH_SAYS, new Stockfish().getEvalScore(board.getFenStr(), 10));
        return retEval;
    }

    public Evaluation getCapturesEvaluation() {
        return getCapturesEvaluation(board.getCurrentPlayer());
    }

    public Evaluation getCapturesEvaluation(Player player) {
        long hash = board.getBoardHash().getFullHash();
        if (capturesEvaluationHashMap.containsKey(hash)) {
            return capturesEvaluationHashMap.get(hash);
        }
        Evaluation evaluation = getCapturesEvaluation_(player, true, Double.MIN_VALUE, Double.MAX_VALUE);
        capturesEvaluationHashMap.put(hash, evaluation);
        return evaluation;
    }

    private Evaluation getCapturesEvaluation_(Player player, boolean isMax, double a, double b) {
        Evaluation currentEval = getEvaluation(player);
        ArrayList<Move> allCaptureMoves = board.getAllCaptureMoves();
        for (Move move : allCaptureMoves) {
            board.applyMove(move);
            Evaluation captures = getCapturesEvaluation_(player, !isMax, a, b);
            board.undoMove();
            if (isMax == captures.isGreaterThan(currentEval)) {
                currentEval = new Evaluation(captures);
            }
            if (isMax) {
                a = Math.max(a, currentEval.getEval());
            } else {
                b = Math.min(b, currentEval.getEval());
            }
            if (b <= a) {
                break;
            }
        }
        return currentEval;
    }

    private double forceKingToCorner(double egWeight, Player player) {
        double ret = 0;
        Location opK = board.getKing(player.getOpponent());

        int opRow = Location.row(opK), opCol = Location.col(opK);

        int opDstToCenterCol = Math.max(3 - opCol, opCol - 4);
        int opDstToCenterRow = Math.max(3 - opRow, opRow - 4);
        int opKDstFromCenter = opDstToCenterCol + opDstToCenterRow;
        ret += opKDstFromCenter;

        Location myK = board.getKing(player);

        int myRow = Location.row(myK), myCol = Location.col(myK);

        int kingsColDst = Math.abs(myCol - opCol);
        int kingsRowDst = Math.abs(myRow - opRow);
        int kingsDst = kingsColDst + kingsRowDst;
        ret += 14 - kingsDst;

//        return ret * 0.01 * egWeight;
        return ret * egWeight;
    }

    private double compareForceKingToCorner(double egWeight) {
        return forceKingToCorner(egWeight, Player.WHITE) - forceKingToCorner(egWeight, Player.BLACK);
    }

//
//    private double compareSquareControl(int player) {
//        return squaresControl(player) - squaresControl(Player.getOpponent(player));
//    }

    private double squaresControl(int player) {
        double ret = 0;
//        for (PieceInterface piece : board.getPlayersPieces(player)) {
//            ArrayList<Move> moves = piece.getPseudoMovesBitboard();
//            for (Move move : moves) {
//                ret += close2EnemyScore(move.getMovingTo(), player);
//            }
//        }
        return ret;
    }

//    private double close2EnemyScore(Location loc, Player player) {
//        int opponentStartingRow = getStartingRow(Player.getOpponent(player));
//        int distance = Math.abs(Location.row(loc) - opponentStartingRow);
//        return calcClose(distance);
//    }

    private double compareKingSafety() {

        return kingSafety(Player.WHITE) - kingSafety(Player.BLACK);
    }

    private double kingSafety(Player player) {
        double ret;
        int movesNum = board.getPieceMovesFrom(board.getKing(player), PieceType.QUEEN, player).size();
        ret = movesNum * -0.01;
        return ret;
    }

    private double comparePieceTables(double egWeight) {
        double ret = 0;
//        Map<Location, PieceInterface>[] pieces = board.getPieces();
//        for (int i = 0, piecesLength = pieces.length; i < piecesLength; i++) {
//            int mult = i == Player.WHITE ? 1 : -1;
//            Map<Location, PieceInterface> playersPieces = pieces[i];
//            for (PieceInterface piece : playersPieces.values()) {
//                ret += getTableData(piece, egWeight, piece.getLoc()) * mult;
//            }
//        }
        return ret;
    }

//    private double getTableData(PieceInterface piece, double egWeight, Location loc) {
//        Tables.PieceTable table = Tables.getPieceTable(piece.getPieceType());
//        return table.getValue(egWeight, piece.getPieceColor(), loc);
//    }

    private double compareMaterial() {
        return materialSum(Player.WHITE) - materialSum(Player.BLACK);
    }


    private double materialSum(Player player) {
        double ret = 0;
        int[] piecesCount = board.getPiecesCount(player);
        for (int i = 0, piecesCountLength = piecesCount.length; i < piecesCountLength; i++) {
            int count = piecesCount[i];
            ret += count * PieceType.getPieceType(i).value;

        }
        return ret;
    }


    public Evaluation checkGameOver() {
        long hash = board.getBoardHash().getFullHash();
        if (gameOverHashMap.containsKey(hash)) {
            return gameOverHashMap.get(hash);
        }
        Evaluation ret = isGameOver_();

        gameOverHashMap.put(hash, ret);
        return ret;
    }

    private Evaluation isGameOver_() {
        Player currentPlayer = board.getCurrentPlayer();
        if (!board.anyLegalMove(currentPlayer)) {
            if (board.isInCheck(currentPlayer)) {
                return new Evaluation(new GameStatus(GameStatus.CHECKMATE));
            }
            return new Evaluation(GameStatus.STALEMATE);

        } else if (board.getHalfMoveClock() >= 100) {
            return new Evaluation(GameStatus.FIFTY_MOVE_RULE);
        }
        if (checkRepetition()) {
            return new Evaluation(GameStatus.THREE_FOLD_REPETITION);
        }
        if (checkForInsufficientMaterial()) {
            return new Evaluation(GameStatus.INSUFFICIENT_MATERIAL);
        }
        return new Evaluation();
    }

    private boolean checkForInsufficientMaterial() {
        return insufficientMaterial(Player.WHITE) &&
                insufficientMaterial(Player.BLACK);
    }

    private boolean insufficientMaterial(Player player) {
        return board.getNumOfPieces(player, PieceType.KING) < 1 || (
                board.getNumOfPieces(player, PieceType.PAWN) == 0 &&
                        board.getNumOfPieces(player, PieceType.MINOR_PIECES) <= 1 &&
                        board.getNumOfPieces(player, PieceType.MAJOR_PIECES) == 0);

    }

    private boolean checkRepetition() {
//        if (board.getRepetitionHashList().size() >= 8) {
//            long currentHash = board.getBoardHash().getFullHash();
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


}