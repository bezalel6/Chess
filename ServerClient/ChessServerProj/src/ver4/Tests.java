package ver4;

import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.evaluation.Evaluation;
import ver4.SharedClasses.moves.Move;
import ver4.model_classes.*;
import ver4.model_classes.eval_classes.Eval;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class Tests {
    private static final int POSITIONS_COUNT_DEPTH = 5;
    private static final boolean PRINT_POSITIONS_MOVES = false;
    private static final boolean MULTITHREADING_POS = true;

    public static void main(String[] args) {
//        minimax();
//        fiftyMoves();
        attackedSquares();
//        printNumOfPositions(new Model());
//        evaluation();
//        threefold();
//        fen();
//        castling();
    }


    private static void fen() {
        Model model = new Model("rnbkqbnr/pppppppp/8/8/8/8/PPPP2P1/RNBKQBNR w KQkq - 0 1");
        System.out.println("Fen = " + FEN.generateFEN(model));
//        FEN.loadFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", null);
    }

    private static void threefold() {
        Model model = new Model();
        String[] pgn = {"Nc3", "Nc6", "Nb1", "Nb8", "Nc3", "Nc6", "Nb1", "Nb8"};
        int index = 0;
        while (!Eval.isGameOver(model)) {
            var moves = model.generateAllMoves();
            int finalIndex = index;
            Move applying = moves.stream()
                    .filter(move ->
                            move.getAnnotation().startsWith(pgn[finalIndex]))
                    .findAny()
                    .orElse(null);
            index++;
            if (applying == null) {
                System.out.println("didnt find. moves = " + moves);
                assert false;
            }
            System.out.println("Move: " + applying + " Model:\n");
            model.printBoard();

            model.applyMove(applying);

            if (applying.getThreefoldStatus() == Move.ThreefoldStatus.CAN_CLAIM) {
                System.out.println("Found threefold!");
                break;
            }
        }
        System.out.println(Eval.getEvaluation(model));
    }

    private static void evaluation() {
        Model model = new Model();
        Evaluation evaluation = Eval.getEvaluation(model);
        System.out.println("Evaluation = " + evaluation);
    }

    private static void fiftyMoves() {
        Model model = new Model();
        int i = 0;
        while (!Eval.isGameOver(model)) {
            Move move = model.generateAllMoves()
                    .stream()
                    .filter(Move::isReversible)
                    .findAny()
                    .orElse(null);
            if (move == null) {
                System.out.println("Couldnt find a reversible move.");
                break;
            }
            System.out.println("Move " + (++i) + " = " + move);
            model.applyMove(move);

        }
        System.out.println(model);
    }

    private static void minimax() {
        String f = null;
        Minimax minimax = new Minimax(new Model(f), 10);
        new Model().printBoard();
        System.out.println("Minimax = \n" + minimax.getBestMove());
    }

    private static void attackedSquares() {
        String fen = "3k4/8/8/8/N7/8/8/3K4 w - - 0 1";
        Model model = new Model(fen);
        System.out.println("Model: \n");
        model.printBoard();
        System.out.println();
        Bitboard attacked = AttackedSquares.getAttackedSquares(model, PlayerColor.WHITE);
        System.out.println(attacked.prettyBoard());
    }

    private static int[] testPositions(int depth, Model model) {
        ZonedDateTime minimaxStartedTime = ZonedDateTime.now();
        int res = countPositions(depth, model, MULTITHREADING_POS);
        int time = (int) minimaxStartedTime.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
        return new int[]{res, time};
    }

    public static int countPositions(int depth, Model model, boolean isRoot) {
        if (depth == 0)
            return 1;
        ArrayList<Move> moves = model.generateAllMoves();
        int size = moves.size();
        int num;
        if (isRoot) {
            num = IntStream.range(0, size).parallel().map(i -> executePos(depth, new Model(model), moves.get(i))).sum();
        } else {
            num = moves.stream().mapToInt(move -> executePos(depth, model, move)).sum();
        }
        return num;
    }

    private static int executePos(int depth, Model model, Move move) {
        model.applyMove(move);
        int res = countPositions(depth - 1, model, false);
        if (PRINT_POSITIONS_MOVES && depth == POSITIONS_COUNT_DEPTH)
            System.out.println(move.getMovingFrom().toString() + "" + move.getMovingTo().toString() + ": " + res);
        model.undoMove();
        return res;
    }

    public static void printNumOfPositions(Model model) {
        new Thread(() -> {
            int numOfVar = 1;
            for (int depth = 1; depth <= POSITIONS_COUNT_DEPTH; depth++) {
                int res = 0, time = 0;
                for (int j = 0; j < numOfVar; j++) {
                    int[] arr = testPositions(depth, model);
                    res = arr[0];
                    time += arr[1];
                }
                time /= numOfVar;
                System.out.println("Depth: " + depth + " Result: " + res + " positions Time: " + time + " milliseconds");
            }
        }).start();

    }

}
