package ver27_transpositions.model_classes;

import ver27_transpositions.Controller;
import ver27_transpositions.Location;
import ver27_transpositions.Player;
import ver27_transpositions.model_classes.pieces.Piece;

import java.util.Map;
import java.util.Random;

import static ver27_transpositions.model_classes.pieces.Piece.NUM_OF_PIECE_TYPES;
import static ver27_transpositions.model_classes.pieces.Piece.NUM_OF_PLAYERS;

public class Zobrist {
    private static final long[][][][] zPieces = initPieces();
    private static final long[][][][] zCapturedPieces = initPieces();
    private static final long[][] zEnPassant = initEnPassant();
    private static final long[][] zLocations = initLocations();
    private static final long[] zCastling = createRandomArr(4);
    private static final long[] zPlayer = createRandomArr(2);
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
        for (Map<Location, Piece> playersPieces : board.getPieces())
            for (Piece piece : playersPieces.values()) {
                ret ^= hash(piece);
            }
        Location enPassant = board.getEnPassantTargetLoc();
        if (enPassant != null) {
            Piece piece = board.getPiece(board.getEnPassantActualLoc());
            if (piece != null && !piece.isOnMyTeam(player))
                ret ^= zEnPassant[player][enPassant.getCol()];
        }
        boolean[] castlingAbility = board.getCastlingAbility().getCastlingAbility();
        for (int i = 0, castlingAbilityLength = castlingAbility.length; i < castlingAbilityLength; i++) {
            boolean b = castlingAbility[i];
            if (b) {
                ret ^= zCastling[i];
            }
        }
        if (board.getCurrentPlayer() == Player.BLACK)
            ret ^= zBlack2Move;
        ret ^= zPlayer[player];
        return ret;
    }

    public static long hash(Piece piece) {
        return hash(piece.getPieceColor(), piece.getPieceType(), piece.getLoc(), piece.isCaptured());
    }


    public static long hash(int pieceColor, int pieceType, Location currentLoc, boolean isCaptured) {
        long ret;
        if (isCaptured) {
            ret = zCapturedPieces[pieceColor][pieceType][currentLoc.getRow()][currentLoc.getCol()];
        } else {
            ret = zPieces[pieceColor][pieceType][currentLoc.getRow()][currentLoc.getCol()];
        }
        return ret;
    }

    public static long hash(Location loc, int player) {
        return hash(loc) + zPlayer[player];
    }

}
