package ver14.Model.Eval;

//import org.apache.commons.lang.ArrayUtils;

import org.apache.commons.lang3.ArrayUtils;
import ver14.Model.AttackedSquares;
import ver14.Model.Bitboard;
import ver14.Model.Model;
import ver14.Model.PiecesBBs;
import ver14.SharedClasses.Game.Evaluation.Evaluation;
import ver14.SharedClasses.Game.Evaluation.EvaluationParameters;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Eval - evaluate a given position for a player color. the evaluation is consistent to both players. meaning that an evaluation for any position
 * is going to be the same for both players, only multiplied by -1 for the other player.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Eval implements Serializable {
    /**
     * The constant endgameMaterialStart.
     */
    private static final double endgameMaterialStart = PieceType.ROOK.value * 2 + PieceType.BISHOP.value + PieceType.KNIGHT.value;
    /**
     * The constant PRINT_REP_LIST.
     */
    public static boolean PRINT_REP_LIST = false;
    /**
     * The Model.
     */
    private final Model model;
    /**
     * The Player to move.
     */
    private final PlayerColor playerToMove;
    /**
     * The Evaluation for.
     */
    private PlayerColor evaluationFor;
    /**
     * The Opponent color.
     */
    private PlayerColor opponentColor;
    /**
     * The Eg weight.
     */
    private double egWeight;
    /**
     * The Evaluation.
     */
    private Evaluation evaluation;

    /**
     * Instantiates a new Eval.
     *
     * @param model         the model
     * @param evaluationFor the evaluation for
     */
    private Eval(Model model, PlayerColor evaluationFor) {
        this(model, evaluationFor, false);
    }

    /**
     * Instantiates a new Eval.
     *
     * @param model                the model
     * @param evaluationFor        the evaluation for
     * @param onlyCheckForGameOver the only check for game over
     */
    private Eval(Model model, PlayerColor evaluationFor, boolean onlyCheckForGameOver) {
        this.model = model;
        this.playerToMove = model.getCurrentPlayer();
        Evaluation checkGameOver = checkGameOver();
        if (checkGameOver.isGameOver() || onlyCheckForGameOver) {
            checkGameOver.setPerspective(evaluationFor);
            evaluation = checkGameOver;
            return;
        }

        this.evaluationFor = evaluationFor;
        this.opponentColor = evaluationFor.getOpponent();
        egWeight = endgameWeight();

        calcEvaluation();


    }

    /**
     * checks if the game is over in the current position.
     *
     * @return if this position is a game over, the game over evaluation. otherwise an empty evaluation.
     */
    private Evaluation checkGameOver() {
//        if the current player can't make any move
        if (!model.anyLegalMove(playerToMove)) {
//            checkmate
            if (model.isInCheck(playerToMove)) {
                return new Evaluation(GameStatus.checkmate(playerToMove.getOpponent(), model.getKing(playerToMove)), playerToMove);
            }
//            stalemate
            return new Evaluation(GameStatus.stalemate(), playerToMove);

        }
//        fifty move rule
        if (model.getHalfMoveClock() >= 100) {
            return new Evaluation(GameStatus.fiftyMoveRule(), playerToMove);
        }
//        threefold repetition
        if (checkRepetition()) {
            return new Evaluation(GameStatus.threeFoldRepetition(), playerToMove);
        }
//        insufficient material
        if (checkForInsufficientMaterial()) {
            return new Evaluation(GameStatus.insufficientMaterial(), playerToMove);
        }
//        not a game over
        return new Evaluation(playerToMove);
    }

    /**
     * Endgame weight.
     *
     * @return the endgame weight for the current position
     */
    private double endgameWeight() {
//        the endgame weight is a function of the material still on the board
        double materialWithoutPawns = materialSumWithoutPandK();
        double multiplier = 1 / endgameMaterialStart;
        return 1 - Math.min(1, materialWithoutPawns * multiplier);
    }

    /**
     * if isn't game over, an evaluation is calculated.
     */
    private void calcEvaluation() {

        evaluation = new Evaluation(PlayerColor.WHITE);
//        Check
        if (model.isInCheck()) {
            evaluation.getGameStatus().setInCheck(model.getKing());
        }

        //Material
        evaluation.addDetail(EvaluationParameters.MATERIAL, materialSum(PlayerColor.WHITE) - materialSum(PlayerColor.BLACK));

        //Piece Tables
        evaluation.addDetail(EvaluationParameters.PIECE_TABLES, materialSum(PlayerColor.WHITE) - materialSum(PlayerColor.BLACK));

//        force king to corner
        evaluation.addDetail(EvaluationParameters.FORCE_KING_TO_CORNER, forceKingToCorner(egWeight, PlayerColor.WHITE) - forceKingToCorner(egWeight, PlayerColor.BLACK));

        evaluation.setPerspective(evaluationFor);

    }

    /**
     * Check for threefold repetition .
     *
     * @return true if this board had a threefold repetition. false otherwise.
     */
    private boolean checkRepetition() {
        var stack = model.getMoveStack();
        if (stack.size() < 8)
            return false;
        ArrayList<Long> list = new ArrayList<>();
//        creating a list of all consecutive reversible moves done in the game history up until now
        for (int i = stack.size() - 1; i >= 0; i -= 2) {
            var move = stack.get(i);
            if (!move.isReversible())
                break;
            long l = move.getCreatedListHashSupplier().get();
            list.add(l);
        }

        if (PRINT_REP_LIST)
            System.out.println(list);

        if (list.size() < 4)
            return false;
        for (int i = 0; i < list.size(); i++) {
            int matches = 0;
            long current = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j) == current) {
                    matches++;
                    if (matches == 2) {//a repetition is found
                        return true;
                    }
                }
            }
        }
        return false;
//        return count - set.size() >= 1;
    }

    /**
     * Check for insufficient material.
     *
     * @return the boolean
     */
    private boolean checkForInsufficientMaterial() {
        return insufficientMaterial(PlayerColor.WHITE, model) &&
                insufficientMaterial(PlayerColor.BLACK, model);
    }

    /**
     * Material sum without pawns and kings. used to calculate the endgame weight
     *
     * @return the sum
     */
    private double materialSumWithoutPandK() {
        double ret = 0;

        int[] pieces = ArrayUtils.addAll(model.getPiecesCount(evaluationFor), model.getPiecesCount(opponentColor));
        for (PieceType type : PieceType.PIECE_TYPES) {
            if (type != PieceType.PAWN && type != PieceType.KING)
                ret += pieces[type.asInt] * type.value;
        }
        return ret;
    }

    /**
     * the sum of all the pieces' values of a player
     *
     * @param playerColor the player color
     * @return the sum in centipawns
     */
    private int materialSum(PlayerColor playerColor) {
        int ret = 0;
        int[] piecesCount = model.getPiecesCount(playerColor);
        for (int i = 0, piecesCountLength = piecesCount.length; i < piecesCountLength; i++) {
            int count = piecesCount[i];
            ret += count * PieceType.getPieceType(i).value;

        }
        return ret;
    }

    /**
     * Force king to corner.
     *
     * @param egWeight    the endgame weight
     * @param playerColor the player color
     * @return the score
     */
    private int forceKingToCorner(double egWeight, PlayerColor playerColor) {

        if (egWeight == 0)
            return 0;
        int ret = 0;
        Location opK = model.getKing(playerColor.getOpponent());

        int opRow = opK.row, opCol = opK.col;
//how far is the opponent's king from the center
        int opDstToCenterCol = Math.max(3 - opCol, opCol - 4);
        int opDstToCenterRow = Math.max(3 - opRow, opRow - 4);
        int opKDstFromCenter = opDstToCenterCol + opDstToCenterRow;
        ret += opKDstFromCenter;

        Location myK = model.getKing(playerColor);

        int myRow = myK.row, myCol = myK.col;

//        how far is my king from the opponent's
        int kingsColDst = Math.abs(myCol - opCol);
        int kingsRowDst = Math.abs(myRow - opRow);
        int kingsDst = kingsColDst + kingsRowDst;
        ret += 14 - kingsDst;

        return (int) (ret * egWeight);
    }
//
//    private double compareSquareControl(int player) {
//        return squaresControl(player) - squaresControl(Player.getOpponent(player));
//    }

    /**
     * does a player has insufficient mating material.
     *
     * @param playerColor the player color
     * @param model       the model
     * @return the boolean
     */
    private static boolean insufficientMaterial(PlayerColor playerColor, Model model) {
        return (
                model.getNumOfPieces(playerColor, PieceType.PAWN) == 0 &&
                        model.getNumOfPieces(playerColor, PieceType.MINOR_PIECES) <= 1 &&
                        model.getNumOfPieces(playerColor, PieceType.MAJOR_PIECES) == 0);

    }

    /**
     * Is sufficient material boolean.
     *
     * @param checkingFor the checking for
     * @param model       the model
     * @return the boolean
     */
    public static boolean isSufficientMaterial(PlayerColor checkingFor, Model model) {
        return !insufficientMaterial(checkingFor, model);
    }

    /**
     * Gets evaluation.
     *
     * @param model       the model
     * @param playerColor the player color
     * @return the evaluation
     */
    public static Evaluation getEvaluation(Model model, PlayerColor playerColor) {
        return new Eval(model, playerColor).evaluation;
    }

    /**
     * evaluation for current player
     *
     * @param model the model
     * @return evaluation evaluation
     */
    public static Evaluation getEvaluation(Model model) {
        return new Eval(model, model.getCurrentPlayer()).evaluation;
    }

    /**
     * Is game over boolean.
     *
     * @param model the model
     * @return the boolean
     */
    public static boolean isGameOver(Model model) {
        return new Eval(model, model.getCurrentPlayer(), true).evaluation.isGameOver();
    }

    /**
     * Calc close double.
     *
     * @param distance the distance
     * @return the double
     */
    private static double calcClose(int distance) {
        double num = Math.exp(distance);
        if (distance <= 4) {
            num = Math.exp(distance);
        }
        num = Math.floor(num);
        return num + "".length();
    }

    /**
     * calculates piece tables evaluation for a player
     *
     * @param clr the clr
     * @return the int
     */
    private int pieceTables(PlayerColor clr) {
        int res = 0;
        PiecesBBs playersPieces = model.getPlayersPieces(clr);
        Bitboard[] bitboards = playersPieces.getBitboards();
        for (int i = 0, bitboardsLength = bitboards.length; i < bitboardsLength; i++) {
            Bitboard bb = bitboards[i];
            for (Location loc : bb.getSetLocs()) {
                Tables.PieceTable table = Tables.getPieceTable(PieceType.getPieceType(i));

                res += table.getValue(egWeight, clr, loc);
            }
        }
        return res;
    }

    /**
     * King safety int.
     *
     * @param playerColor the player color
     * @return the int
     */
    private int kingSafety(PlayerColor playerColor) {
        int ret;
        int movesNum = AttackedSquares.getPieceAttacksFrom(PieceType.QUEEN, model.getPieceBitBoard(playerColor, PieceType.KING), playerColor.getOpponent(), model).getSetLocs().size();
        ret = movesNum * -1;
        return ret;
    }

    /**
     * Squares control double.
     *
     * @param player the player
     * @return the double
     */
    private double squaresControl(int player) {
        double ret = 0;
//        for (PieceInterface piece : model.getPlayersPieces(player)) {
//            ArrayList<Move> moves = piece.getPseudoMovesBitboard();
//            for (Move move : moves) {
//                ret += close2EnemyScore(move.getdestination(), player);
//            }
//        }
        return ret;
    }


}