package ver26_save_minimax_levels.model_classes;

import ver26_save_minimax_levels.Controller;
import ver26_save_minimax_levels.Location;
import ver26_save_minimax_levels.model_classes.pieces.Piece;

import java.util.Random;

import static ver26_save_minimax_levels.model_classes.pieces.Piece.*;

public class Zobrist {
    private static final long[][][][] zPieces = initPieces();
    private static final long[][][][] zCapturedPieces = initPieces();
    private static final long[][] zEnPassant = initEnPassant();
    private static final long[][] zLocations = initLocations();
    private static final long[] zCastling = createRandomArr(4);
    private static final long zBlack2Move = random64();

    private static long[][] initLocations() {
        long[][] ret = new long[Controller.ROWS][Controller.COLS];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = createRandomArr(Controller.COLS);
        }
        return ret;
    }

    private static long[][] initEnPassant() {
        long[][] ret = new long[NUM_OF_PLAYERS][Controller.COLS];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = createRandomArr(Controller.COLS);
        }
        return ret;
    }

    public static void main(String[] args) {

    }

    private static void compareTime() {
//        Board b1 = new Board(Positions.getAllPositions().get(0).getFen());
//        long secs1 = System.nanoTime();
//        System.out.println("board hash: " + hash(b1));
//        secs1 = System.nanoTime() - secs1;
//        System.out.println("took " + secs1 + " nano secs for zobrist hashing");
//        long secs2 = System.nanoTime();
//        System.out.println("fen hash: " + b1.hashCode());
//        secs2 = System.nanoTime() - secs2;
//        System.out.println("took " + secs2 + " nano secs for board hashing");
//
//        System.out.println("zobrist is faster by " + (secs2 - secs1) + "nano seconds");
    }

    public static long[] createRandomArr(int length) {
        long[] ret = new long[length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = random64();
        }
        return ret;
    }

    private static long[][][][] initPieces() {
        long[][][][] ret = new long[NUM_OF_PLAYERS][NUM_OF_PIECE_TYPES][Controller.ROWS][Controller.COLS];
        for (long[][][] player : ret) {
            for (long[][] pieceType : player) {
                for (int i = 0; i < pieceType.length; i++) {
                    pieceType[i] = createRandomArr(Controller.COLS);
                }
            }
        }
        return ret;
    }

    public static long random64() {
        return new Random().nextLong();
    }

    public static long hash(Board board) {
        return hash(board, board.getCurrentPlayer());
    }

    public static long hash(Location loc) {
        return hash(loc.getRow(), loc.getCol());
    }

    public static long hash(int row, int col) {
        return zLocations[row][col];
    }

    public static long hash(Board board, int player) {
        long ret = 0;
        for (var playersPieces : board.getPieces())
            for (Piece piece : playersPieces.values()) {
                ret ^= hash(piece);
            }
        Location enPassant = board.getEnPassantTargetLoc();
        if (enPassant != null) {
            ret ^= zEnPassant[player][enPassant.getCol()];
        }
        boolean[] castlingAbility = board.getCastlingAbility().getCastlingAbility();
        for (int i = 0, castlingAbilityLength = castlingAbility.length; i < castlingAbilityLength; i++) {
            boolean b = castlingAbility[i];
            if (b) {
                ret ^= zCastling[i];
            }
        }
        if (player == Player.BLACK)
            ret ^= zBlack2Move;
        return ret;
    }

    public static long hash(Piece piece) {
        return hash(piece.getPieceColor(), piece.getPieceType(), piece.getStartingLoc(), piece.isCaptured());
    }

    /**
     * assuming piece is not captured
     *
     * @param pieceColor
     * @param pieceType
     * @param startingLoc
     * @return
     */
    public static long hash(int pieceColor, int pieceType, Location startingLoc) {
        return hash(pieceColor, pieceType, startingLoc, false);
    }

    public static long hash(int pieceColor, int pieceType, Location startingLoc, boolean isCaptured) {
        if (isCaptured) {
            return zCapturedPieces[pieceColor][pieceType][startingLoc.getRow()][startingLoc.getCol()];
        }
        return zPieces[pieceColor][pieceType][startingLoc.getRow()][startingLoc.getCol()];
    }

}
