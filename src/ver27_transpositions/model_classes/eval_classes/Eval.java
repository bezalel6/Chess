package ver27_transpositions.model_classes.eval_classes;

import ver27_transpositions.Location;
import ver27_transpositions.Player;
import ver27_transpositions.model_classes.Board;
import ver27_transpositions.model_classes.GameStatus;
import ver27_transpositions.model_classes.Square;
import ver27_transpositions.model_classes.moves.Move;
import ver27_transpositions.model_classes.pieces.King;
import ver27_transpositions.model_classes.pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ver27_transpositions.model_classes.pieces.Piece.*;


public class Eval {
    public static final int NUM_OF_EVAL_PARAMETERS = 8;
    public static final int MATERIAL = 0;
    public static final int PIECE_TABLES = 1;
    public static final int KING_SAFETY = 2;
    public static final int HANGING_PIECES = 3;
    public static final int SQUARE_CONTROL = 4;
    public static final int MOVEMENT_ABILITY = 5;
    public static final int FORCE_KING_TO_CORNER = 6;
    public static final int EG_WEIGHT = 7;
    public static final String[] EVAL_PARAMETERS_NAMES = initEvalParametersArr();
    public static final int MIDDLE_GAME = 0, ENDGAME = 1, OPENING = 2;
    private static final HashMap<Long, Evaluation> evaluationHashMap = new HashMap<>();
    private static final HashMap<Long, Evaluation> capturesEvaluationHashMap = new HashMap<>();
    private static final HashMap<Long, Evaluation> gameOverHashMap = new HashMap<>();
    private static final double endgameMaterialStart = Piece.getPieceWorth(ROOK) * 2 + Piece.getPieceWorth(BISHOP) + Piece.getPieceWorth(KNIGHT);
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
        double ret = 0;

        int[] pieces = board.getPiecesCount(player);
        for (int type : PIECES_TYPES) {
            if (type != PAWN)
                ret += pieces[type] * Piece.getPieceWorth(type);
        }
        return ret;
    }

    public static double endgameWeight(Board board, int player) {
        double materialWithoutPawns = materialSumWithoutPawns(board, player);
        double multiplier = 1 / endgameMaterialStart;
//        return 1 - materialWithoutPawns * multiplier;
        return 1 - Math.min(1, materialWithoutPawns * multiplier);
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
        evaluationHashMap.put(hash, evaluation);
        return evaluation;
    }

    private Evaluation getEvaluation_(int player) {

        Evaluation checkGameOver = checkGameOver(player);
        if (checkGameOver.isGameOver()) return checkGameOver;

        Evaluation retEval = new Evaluation();
        double egWeight = endgameWeight(board, player);
//        Check
        if (board.isInCheck(player)) {
            retEval.setGameStatusType(GameStatus.GameStatusType.CHECK);
        }
        retEval.addDebugDetail(EG_WEIGHT, egWeight);

        //Material
        retEval.addDetail(MATERIAL, compareMaterial(player));

        //Piece Tables
        retEval.addDetail(PIECE_TABLES, comparePieceTables(player, egWeight));

//        force king to corner
        retEval.addDetail(FORCE_KING_TO_CORNER, forceKingToCornerEndgameEval(player, egWeight));
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

    public Evaluation getCapturesEvaluation() {
        return getCapturesEvaluation(board.getCurrentPlayer());
    }

    public Evaluation getCapturesEvaluation(int player) {
        long hash = board.getBoardHash(player);
        if (capturesEvaluationHashMap.containsKey(hash)) {
            return capturesEvaluationHashMap.get(hash);
        }
        Evaluation evaluation = getCapturesEvaluation_(player, true);
        capturesEvaluationHashMap.put(hash, evaluation);
        return evaluation;
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


    private double forceKingToCornerEndgameEval(int player, double egWeight) {
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

        return ret * 0.01 * egWeight;
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

    private double comparePieceTables(int player, double egWeight) {
        double ret = 0;
        Map<Location, Piece>[] pieces = board.getPieces();
        for (int i = 0, piecesLength = pieces.length; i < piecesLength; i++) {
            int mult = i == player ? 1 : -1;
            Map<Location, Piece> playersPieces = pieces[i];
            for (Piece piece : playersPieces.values()) {
                ret += getTableData(piece, egWeight, piece.getLoc()) * mult;
            }
        }
        return ret;
    }

    private double getTableData(Piece piece, double egWeight, Location loc) {
        Tables.PieceTable table = Tables.getPieceTable(piece.getPieceType());
        double ret = table.getValue(egWeight, piece.getPieceColor(), loc);
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
            ret += count * Piece.getPieceWorth(i);

        }
        return ret;
    }

    public Evaluation checkGameOver() {
        return checkGameOver(board.getCurrentPlayer());
    }

    public Evaluation checkGameOver(int player) {
        long hash = board.getBoardHash(player);
        if (gameOverHashMap.containsKey(hash)) {
            return gameOverHashMap.get(hash);
        }
        Evaluation ret = isGameOver_();
        if (player != ret.getGameStatus().getWinningPlayer()) {
            ret.flipSide();
        }
        gameOverHashMap.put(hash, ret);
        return ret;
    }

    private Evaluation isGameOver_() {
        int currentPlayer = board.getCurrentPlayer();
        int winningPlayer = board.getOpponent();
//        int player = Player.getOpponent(currentPlayer);
        if (board.getAllMoves(currentPlayer).isEmpty()) {
            if (board.isInCheck(currentPlayer)) {
                return new Evaluation(new GameStatus(GameStatus.CHECKMATE), winningPlayer);
            }
            return new Evaluation(GameStatus.STALEMATE);

        } else if (board.getHalfMoveClock() >= 100) {
            return new Evaluation(GameStatus.FIFTY_MOVE_RULE);
        } else if (checkRepetition(currentPlayer)) {
            return new Evaluation(GameStatus.THREE_FOLD_REPETITION);
        } else if (checkForInsufficientMaterial()) {
            return new Evaluation(GameStatus.INSUFFICIENT_MATERIAL);
        }
        return new Evaluation();
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
        if (board.getRepetitionHashList().size() >= 8) {
            long currentHash = board.getBoardHash(player);
            int numOfMatches = 0;
//            int minMatch = currentPlayer == player ? 3 : 2;
            int minMatch = 3;
            for (long hash : board.getRepetitionHashList()) {
                if (currentHash == hash) {
                    numOfMatches++;
                    if (numOfMatches >= minMatch)
                        return true;
                }
            }
        }
        return false;
    }


}