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


public class Eval implements Serializable {
    private static final double endgameMaterialStart = PieceType.ROOK.value * 2 + PieceType.BISHOP.value + PieceType.KNIGHT.value;
    public static boolean PRINT_REP_LIST = false;
    private final Model model;
    private final PlayerColor playerToMove;
    private PlayerColor evaluationFor;
    private PlayerColor opponentColor;
    private double egWeight;
    private Evaluation evaluation;

    private Eval(Model model, PlayerColor evaluationFor) {
        this(model, evaluationFor, false);
    }

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

//        long hash = model.getBoardHash().getFullHash();
//        if (evaluationHashMap.containsKey(hash)) {
//            evaluation = (Evaluation) evaluationHashMap.get(hash);
//        } else {
        calcEvaluation();
//            evaluationHashMap.put(hash, evaluation);
//        }

        evaluation.assertNotGameOver();

    }

    private Evaluation checkGameOver() {
        if (!model.anyLegalMove(playerToMove)) {
            if (model.isInCheck(playerToMove)) {
                return new Evaluation(GameStatus.checkmate(playerToMove.getOpponent(), model.getKing(playerToMove)), playerToMove);
            }
            return new Evaluation(GameStatus.stalemate(), playerToMove);

        } else if (model.getHalfMoveClock() >= 100) {
            return new Evaluation(GameStatus.fiftyMoveRule(), playerToMove);
        }
//        if (checkRepetition()) {
//            return new Evaluation(GameStatus.threeFoldRepetition(), playerToMove);
//        }
        if (checkForInsufficientMaterial()) {
            return new Evaluation(GameStatus.insufficientMaterial(), playerToMove);
        }
        return new Evaluation(playerToMove);
    }

    private double endgameWeight() {
        double materialWithoutPawns = materialSumWithoutPandK();
        double multiplier = 1 / endgameMaterialStart;
//        double multiplier =1/ endgameMaterialStart;
//        return 1 - materialWithoutPawns * multiplier;
        return 1 - Math.min(1, materialWithoutPawns * multiplier);
    }

    private void calcEvaluation() {

        evaluation = new Evaluation(evaluationFor);
//        Check
        if (model.isInCheck()) {
            evaluation.getGameStatus().setInCheck(model.getKing());
        }

        //Material
        evaluation.addDetail(EvaluationParameters.MATERIAL, materialSum(evaluationFor) - materialSum(opponentColor));

        //Piece Tables
        comparePieceTables();

//        force king to corner
        evaluation.addDetail(EvaluationParameters.FORCE_KING_TO_CORNER, forceKingToCorner(egWeight, evaluationFor) - forceKingToCorner(egWeight, opponentColor));

        //Hanging Pieces
//        retEval.addDetail(HANGING_PIECES, calcHangingPieces(player));

        //Square Control
//        retEval.addDetail(SQUARE_CONTROL, compareSquareControl(player));

//        Movement Ability
//        retEval.addDetail(MOVEMENT_ABILITY, compareMovementAbility(player));

        //King Safety
//        evaluation.addDetail(EvaluationParameters.KING_SAFETY, kingSafety(evaluationFor) - kingSafety(opponentColor));

//        retEval.addDetail(STOCKFISH_SAYS, new Stockfish().getEvalScore(model.getFenStr(), 10));
    }

    private boolean checkForInsufficientMaterial() {
        return insufficientMaterial(PlayerColor.WHITE, model) &&
                insufficientMaterial(PlayerColor.BLACK, model);
    }

    private double materialSumWithoutPandK() {
        double ret = 0;

        int[] pieces = ArrayUtils.addAll(model.getPiecesCount(evaluationFor), model.getPiecesCount(opponentColor));
        for (PieceType type : PieceType.PIECE_TYPES) {
            if (type != PieceType.PAWN && type != PieceType.KING)
                ret += pieces[type.asInt] * type.value;
        }
        return ret;
    }

    private int materialSum(PlayerColor playerColor) {
        int ret = 0;
        int[] piecesCount = model.getPiecesCount(playerColor);
        for (int i = 0, piecesCountLength = piecesCount.length; i < piecesCountLength; i++) {
            int count = piecesCount[i];
            ret += count * PieceType.getPieceType(i).value;

        }
        return ret;
    }

    private void comparePieceTables() {
        int res = 0;
        for (PlayerColor currentlyChecking : PlayerColor.PLAYER_COLORS) {
            int mult = currentlyChecking == evaluationFor ? 1 : -1;
            PiecesBBs playersPieces = model.getPlayersPieces(currentlyChecking);
            Bitboard[] bitboards = playersPieces.getBitboards();
            for (int i = 0, bitboardsLength = bitboards.length; i < bitboardsLength; i++) {
                Bitboard bb = bitboards[i];
                for (Location loc : bb.getSetLocs()) {
                    Tables.PieceTable table = Tables.getPieceTable(PieceType.getPieceType(i));

                    res += table.getValue(egWeight, currentlyChecking, loc) * mult;
                }
            }
        }
        evaluation.addDetail(EvaluationParameters.PIECE_TABLES, res);
    }

    private int forceKingToCorner(double egWeight, PlayerColor playerColor) {
        if (egWeight == 0)
            return 0;
        int ret = 0;
        Location opK = model.getKing(playerColor.getOpponent());

        int opRow = opK.row, opCol = opK.col;

        int opDstToCenterCol = Math.max(3 - opCol, opCol - 4);
        int opDstToCenterRow = Math.max(3 - opRow, opRow - 4);
        int opKDstFromCenter = opDstToCenterCol + opDstToCenterRow;
        ret += opKDstFromCenter;

        Location myK = model.getKing(playerColor);

        int myRow = myK.row, myCol = myK.col;

        int kingsColDst = Math.abs(myCol - opCol);
        int kingsRowDst = Math.abs(myRow - opRow);
        int kingsDst = kingsColDst + kingsRowDst;
        ret += 14 - kingsDst;

//        return ret * 0.01 * egWeight;
        return (int) (ret * egWeight);
    }

//
//    private double compareSquareControl(int player) {
//        return squaresControl(player) - squaresControl(Player.getOpponent(player));
//    }

    private static boolean insufficientMaterial(PlayerColor playerColor, Model model) {
        return (
                model.getNumOfPieces(playerColor, PieceType.PAWN) == 0 &&
                        model.getNumOfPieces(playerColor, PieceType.MINOR_PIECES) <= 1 &&
                        model.getNumOfPieces(playerColor, PieceType.MAJOR_PIECES) == 0);

    }

    public static boolean isSufficientMaterial(PlayerColor checkingFor, Model model) {
        return !insufficientMaterial(checkingFor, model);
    }

    public static Evaluation getEvaluation(Model model, PlayerColor playerColor) {
        return new Eval(model, playerColor).evaluation;
    }

    /**
     * evaluation for current player
     *
     * @param model
     * @return
     */
    public static Evaluation getEvaluation(Model model) {
        return new Eval(model, model.getCurrentPlayer()).evaluation;
    }
//    private static final double KING_SAFETY_WEIGHT = -0.01;

    public static boolean isGameOver(Model model) {
        return new Eval(model, model.getCurrentPlayer(), true).evaluation.isGameOver();
    }

    private static double calcClose(int distance) {
        double num = Math.exp(distance);
        if (distance <= 4) {
            num = Math.exp(distance);
        }
        num = Math.floor(num);
        return num + "".length();
    }

    private boolean checkRepetition() {
        if (true)
            return false;
        var stack = model.getMoveStack();

        ArrayList<Long> list = new ArrayList<>();
        for (int i = stack.size() - 1; i >= 0; i -= 2) {
            var move = stack.get(i);
            if (!move.isReversible())
                break;
            long l = move.getCreatorList().getHash();
            list.add(l);
//            if (i == 0) {
//                list.add(model.getFirstPositionMovesHash());
//            }
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
//                    if (matches == 1 && j >= 2) {
//                        return true;
//                    }
                    if (matches == 2) {
                        return true;
                    }
                }
            }
        }
        return false;
//        return count - set.size() >= 1;
    }

    private int kingSafety(PlayerColor playerColor) {
        int ret;
        int movesNum = AttackedSquares.getPieceAttacksFrom(PieceType.QUEEN, model.getPieceBitBoard(playerColor, PieceType.KING), playerColor.getOpponent(), model).getSetLocs().size();
        ret = movesNum * -1;
        return ret;
    }

    private double squaresControl(int player) {
        double ret = 0;
//        for (PieceInterface piece : model.getPlayersPieces(player)) {
//            ArrayList<Move> moves = piece.getPseudoMovesBitboard();
//            for (Move move : moves) {
//                ret += close2EnemyScore(move.getMovingTo(), player);
//            }
//        }
        return ret;
    }


}