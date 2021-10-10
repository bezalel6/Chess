package ver30_flip_board_2.model_classes;

import ver30_flip_board_2.Controller;
import ver30_flip_board_2.Location;
import ver30_flip_board_2.model_classes.moves.Move;
import ver30_flip_board_2.model_classes.pieces.Piece;

import java.util.Map;
import java.util.Random;

import static ver30_flip_board_2.model_classes.pieces.Piece.NUM_OF_PIECE_TYPES;
import static ver30_flip_board_2.model_classes.pieces.Piece.NUM_OF_PLAYERS;

public class Zobrist {
    private static final long[][][][] zPieces = initPieces();
    private static final long[][][][] zCapturedPieces = initPieces();
    private static final long[][] zEnPassant = initEnPassant();
    private static final long[][] zLocations = initLocations();
    private static final long[] zCastling = createRandomArr(4);
    private static final long[] zPlayers = createRandomArr(2);
    private static final long[] zPiecesTypes = createRandomArr(NUM_OF_PIECE_TYPES);

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

    public static long hash(Location loc) {
        return hash(loc.getRow(), loc.getCol());
    }

    public static long hash(int row, int col) {
        return zLocations[row][col];
    }

    public static long hash(Move move) {
        return combineHashes(hash(move.getMovingFrom()), hash(move.getMovingTo()));
    }
//    public static BoardHash hashBoard(Board board) {
//        long ret = 0;
//        int currentPlayer = board.getCurrentPlayer();
//
//        ret ^= piecesHash(board);
//        ret ^= enPassantHash(board, currentPlayer);
//        ret ^= castlingAbilityHash(board);
//        ret ^= player2MoveHash(currentPlayer);
//
//        return ret;
//    }

    public static long playerHash(Board board) {
        return playerHash(board.getCurrentPlayer());
    }

    public static long playerHash(int player) {
        return zPlayers[player];
    }

    public static long castlingAbilityHash(Board board) {
        long ret = 0;
        boolean[] castlingAbility = board.getCastlingAbility().getCastlingAbilityArr();
        for (int i = 0, castlingAbilityLength = castlingAbility.length; i < castlingAbilityLength; i++) {
            boolean b = castlingAbility[i];
            if (b) {
                ret ^= zCastling[i];
            }
        }
        return ret;
    }

    public static long piecesHash(Board board) {
        long ret = 0;
        for (Map<Location, Piece> playersPieces : board.getPieces())
            for (Piece piece : playersPieces.values()) {
                ret ^= hash(piece);
            }
        return ret;
    }

    public static long enPassantHash(Board board) {
        long ret = 0;
        Location enPassant = board.getEnPassantTargetLoc();
        Location actualLoc = board.getEnPassantActualLoc();
        if (enPassant != null && actualLoc != null) {
            int currentPlayer = board.getCurrentPlayer();
            Piece piece = board.getPiece(actualLoc);
            if (piece != null && !piece.isOnMyTeam(currentPlayer))
                ret ^= zEnPassant[currentPlayer][enPassant.getCol()];
        }
        return ret;
    }

    public static long hash(Piece piece) {
        return hash(piece.getPieceColor(), piece.getPieceType(), piece.getLoc(), piece.isCaptured());
    }

    public static long hashPieceByStartingLoc(Piece piece) {
        return hash(piece.getPieceColor(), piece.getPieceType(), piece.getStartingLoc(), piece.isCaptured());
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
        return combineHashes(hash(loc), playerHash(player));
    }

    public static long combineHashes(long h1, long h2) {
        return h1 ^ h2;
    }

    public static long combineHashes(long[] hashes) {
        long ret = 0;
        for (long hash : hashes) {
            ret = combineHashes(ret, hash);
        }
        return ret;
    }

    public static long hashPieceType(int type) {
        return zPiecesTypes[type];
    }
}
