package ver3.model_classes;

import ver3.model_classes.moves.Move;
import ver3.model_classes.pieces.Piece;
import ver3.model_classes.pieces.PieceType;

import java.util.Arrays;
import java.util.Random;


public class Zobrist {
    public static final Random rnd = new Random();
    private static final long[][][] zPieces = initPieces();
    private static final long[][] zEnPassant = initEnPassant();
    private static final long[] zLocations = initLocations();
    private static final long zCastling = random64();
    private static final long[] zPlayers = createRandomArr(2);
    private static final long[] zPiecesTypes = createRandomArr(PieceType.NUM_OF_PIECE_TYPES);

    private static long[] initLocations() {
        long[] ret = new long[Location.NUM_OF_SQUARES];
        Arrays.setAll(ret, i -> random64());
        return ret;
    }

    private static long[][] initEnPassant() {
        long[][] ret = new long[2][8];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = createRandomArr(8);
        }
        return ret;
    }

    public static void main(String[] args) {

    }

    private static void compareTime() {
//        Model b1 = new Model(Positions.getAllPositions().get(0).getFen());
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

    private static long[][][] initPieces() {
        long[][][] ret = new long[2][PieceType.NUM_OF_PIECE_TYPES][Location.NUM_OF_SQUARES];
        for (long[][] player : ret) {
            for (int pieceType = 0; pieceType < player.length; pieceType++) {
                player[pieceType] = createRandomArr(Location.NUM_OF_SQUARES);
            }
        }
        return ret;
    }

    public static long random64() {
        return rnd.nextLong();
    }

    public static long hash(Location loc) {
        return zLocations[loc.asInt()];
    }

    public static long hash(Move move) {
        return combineHashes(hash(move.getMovingFrom()), hash(move.getMovingTo()));
    }

    public static long playerHash(Board board) {
        return playerHash(board.getCurrentPlayer());
    }

    public static long playerHash(Player player) {
        return zPlayers[player.asInt()];
    }

    public static long castlingAbilityHash(Board board) {
        return board.getCastlingAbility() ^ zCastling;
    }

    public static long piecesHash(Board board) {
        long ret = 0;
        for (var playersPieces : board.getPieces())
            for (Bitboard bitboard : playersPieces) {
                ret ^= hash(bitboard);
            }
        return ret;
    }

    private static long hash(Bitboard bitboard) {
        long ret = 0;
        for (Location loc : bitboard.getSetLocs()) {
            ret ^= hash(loc);
        }
        return ret;
    }

    public static long enPassantHash(Board board) {
        long ret = 0;
        Location enPassant = board.getEnPassantTargetLoc();
        Location actualLoc = board.getEnPassantActualLoc();
        if (enPassant != null && actualLoc != null) {
            Player currentPlayer = board.getCurrentPlayer();
            Piece piece = board.getPiece(actualLoc);
            if (piece != null && !piece.isOnMyTeam(currentPlayer))
                ret ^= zEnPassant[currentPlayer.asInt()][enPassant.getCol()];
        }
        return ret;
    }

    public static long hash(Piece piece, Location pieceLoc) {
        return hash(piece.getPlayer(), piece.getPieceType(), pieceLoc);
    }


    public static long hash(Player pieceColor, PieceType pieceType, Location currentLoc) {
        long ret = zPieces[pieceColor.asInt()][pieceType.asInt()][currentLoc.asInt()];

        return ret;
    }

    public static long hash(Location loc, Player player) {
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
