package ver24_flip_board.model_classes.eval_classes;

import ver24_flip_board.Location;
import ver24_flip_board.model_classes.Board;
import ver24_flip_board.model_classes.GameStatus;
import ver24_flip_board.model_classes.Square;
import ver24_flip_board.moves.Move;
import ver24_flip_board.model_classes.pieces.Piece;

import java.util.ArrayList;

import static ver24_flip_board.model_classes.pieces.Piece.*;


public class Eval {
    public static final int NUM_OF_EVAL_PARAMETERS = 6;
    public static final int MATERIAL = 0, PIECE_TABLES = 1, KING_SAFETY = 2, HANGING_PIECES = 3, SQUARE_CONTROL = 4, MOVEMENT_ABILITY = 5;
    public static final String[] EVAL_PARAMETERS_NAMES = initEvalParametersArr();
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
        return ret;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(i + " got " + calcClose(i));
        }

    }

    private static double calcClose(int distance) {
        double num = Math.exp(distance);
        if (distance <= 4) {
            num = Math.exp(distance);
        }
        num = Math.floor(num);
        return num + "".length();
    }


    public Evaluation getEvaluation(int player) {
        Evaluation evaluation = getEvaluation_(player);
        return evaluation;
    }

    public Evaluation getCapturesEvaluation(int player) {
        return getCapturesEvaluation_(player, true);
    }

    private Evaluation getCapturesEvaluation_(int player, boolean isMax) {
        Evaluation currentEval = getEvaluation(player);
        ArrayList<Move> allCaptureMoves = board.getAllCaptureMoves();
        for (Move move : allCaptureMoves) {
//            board.getController().drawMove(move);
//            System.out.println(move);
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

//        Check
        if (board.isInCheck(player)) {
            retEval.setGameStatus(GameStatus.CHECK);
        }
        //Material
        retEval.addDetail(MATERIAL, compareMaterial(player));

        //Piece Tables
        retEval.addDetail(PIECE_TABLES, comparePieceTables(player));

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
        GamePhase phase = new GamePhase();

        for (ArrayList<Piece> playersPieces : board.getPiecesLists()) {
            for (Piece piece : playersPieces) {
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
        double ret = Tables.getValueForPhase(table, phase, piece.getPieceColor(), loc);
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

    public Evaluation isGameOver() {
        int player = board.getCurrentPlayer();
        int side = player == board.getCurrentPlayer() ? GameStatus.LOSING_SIDE : GameStatus.WINNING_SIDE;
        if (board.getAllMoves(player).isEmpty()) {
            if (board.isInCheck(player)) {
                return new Evaluation(GameStatus.CHECKMATE, side);
            }
            return new Evaluation(GameStatus.STALEMATE);
        } else if (board.getHalfMoveClock() >= 100)
            return new Evaluation(GameStatus.FIFTY_MOVE_RULE);
        if (checkRepetition(player))
            return new Evaluation(GameStatus.THREE_FOLD_REPETITION);
        if (checkForInsufficientMaterial())
            return new Evaluation(GameStatus.INSUFFICIENT_MATERIAL);
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

    public class GamePhase {
        public static final int MIDDLE_GAME = 0, ENDGAME = 1, OPENING = 2;
        private double mg;
        private double eg;

        public GamePhase() {
            mg = 1;
            eg = egScaleFactor();
        }

        public double egScaleFactor() {
            double sf = 0;
            int queens = board.bothPlayersNumOfPieces(QUEEN);
            int rooks = board.bothPlayersNumOfPieces(ROOK);
            int bishops = board.bothPlayersNumOfPieces(BISHOP);
            int knights = board.bothPlayersNumOfPieces(KNIGHT);
            int pawns = board.bothPlayersNumOfPieces(PAWN);
            if (queens <= 1) {
                sf += queens == 0 ?
                        0.5 : 0.3;
            }
            if (bishops + knights <= 4) {
                sf += bishops + knights <= 2 ?
                        0.32 : bishops + knights == 3 ?
                        0.27 : 0.22;
            }
            if (rooks <= 2) {
                sf += queens + rooks <= 2 ?
                        0.85 : rooks <= 1 ?
                        rooks == 0 ?
                                0.7 : 0.65 : 0.5;
            }
            if (sf > 0)
                sf -= (0.1 / 8) * pawns;
            return sf;
        }

        public double getMg() {
            return mg;
        }

        public double getEg() {
            return eg;
        }
    }

}