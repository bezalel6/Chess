package ver14.Model.hashing;


import ver14.Game.Game;
import ver14.Model.Bitboard;
import ver14.Model.Model;
import ver14.Model.PiecesBBs;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.Moves.CastlingRights;
import ver14.SharedClasses.Game.Moves.Direction;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;

import java.util.Arrays;
import java.util.Random;


public class Zobrist {
    public static final Random rnd = new Random();
    private static final long[][][] zPieces = new long[PlayerColor.NUM_OF_PLAYERS][PieceType.NUM_OF_PIECE_TYPES][Location.NUM_OF_SQUARES];
    private static final long[][] zEnPassant = new long[PlayerColor.NUM_OF_PLAYERS][Game.COLS];
    private static final long[] zLocations = new long[Location.NUM_OF_SQUARES];
    private static final long[][] zCastling = new long[PlayerColor.NUM_OF_PLAYERS][2];
    private static final long[] zPlayers = createRandomArr(PlayerColor.NUM_OF_PLAYERS);
    private static final long[] zPiecesTypes = createRandomArr(PieceType.NUM_OF_PIECE_TYPES);
    private static final long[] zDirections = createRandomArr(Direction.NUM_OF_DIRECTIONS);
    private static final long zBitBoards = random64();
    private static final long zBoolean = random64();


    static {
        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
            zCastling[playerColor.asInt] = createRandomArr(2);
        }

        for (long[][] player : zPieces) {
            for (int pieceType = 0; pieceType < player.length; pieceType++) {
                player[pieceType] = createRandomArr(Location.NUM_OF_SQUARES);
            }
        }

        for (int i = 0; i < zEnPassant.length; i++) {
            zEnPassant[i] = createRandomArr(Game.COLS);
        }

        Arrays.setAll(zLocations, i -> random64());
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


    public static long random64() {
        return rnd.nextLong();
    }

    public static long hash(Move move) {

        return combineHashes(hash(move.getMovingFrom()), hash(move.getMovingTo()));
    }

    public static long combineHashes(long h1, long h2) {
        return h1 ^ h2;
    }

    public static long hash(Location loc) {
        return zLocations[loc.asInt];
    }

    public static long playerHash(Model model) {
        return playerHash(model.getCurrentPlayer());
    }

    public static long playerHash(PlayerColor playerColor) {
        return zPlayers[playerColor.asInt];
    }


    public static long hash(boolean bool) {
        return bool ? zBoolean : 0;
    }

    public static long hashPieceType(PieceType type) {
        return hashPieceType(type.asInt);
    }

    public static long hashPieceType(int type) {
        return zPiecesTypes[type];
    }

    public static long castlingAbilityHash(Model model) {
        long ret = 0;
        CastlingRights castlingRights = model.getCastlingRights();
        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
            for (CastlingRights.Side side : CastlingRights.Side.SIDES) {
                if (castlingRights.isEnabled(playerColor, side)) {
                    ret ^= zCastling[playerColor.asInt][side.asInt];
                }
            }
        }
        return ret;
    }

    public static long piecesHash(Model model) {
        long ret;
        PlayerColor currentPlayer = model.getCurrentPlayer();
        ret = hash(model.getPlayersPieces(currentPlayer), currentPlayer);
        ret ^= hash(model.getPlayersPieces(currentPlayer.getOpponent()), currentPlayer.getOpponent());
        return ret;
    }

    public static long hash(PiecesBBs piecesBBs, PlayerColor playerColor) {
        long ret = 0;
        Bitboard[] bitboards = piecesBBs.getBitboards();
        for (int i = 0, bitboardsLength = bitboards.length; i < bitboardsLength; i++) {
            Bitboard bitboard = bitboards[i];
            ret ^= hash(bitboard, PieceType.getPieceType(i), playerColor);
        }
        return ret;
    }

    private static long hash(Bitboard bitboard, PieceType pieceType, PlayerColor playerColor) {
        return combineHashes(bitboard.getBitBoard(), zBitBoards, hash(pieceType), hash(playerColor));
    }

    public static long combineHashes(long... hashes) {
        long ret = 0;
        for (long hash : hashes) {
            ret = combineHashes(ret, hash);
        }
        return ret;
    }

    public static long hash(PieceType pieceType) {
        return zPiecesTypes[pieceType.asInt];
    }

    public static long hash(PlayerColor playerColor) {
        return zPlayers[playerColor.asInt];
    }

    public static long enPassantHash(Model model) {
        long ret = 0;
        Location enPassant = model.getEnPassantTargetLoc();
        Location actualLoc = model.getEnPassantActualLoc();
        if (enPassant != null && actualLoc != null) {
            PlayerColor currentPlayerColor = model.getCurrentPlayer();
            Piece piece = model.getLogicBoard().getPiece(actualLoc);
            if (piece != null && !piece.isOnMyTeam(currentPlayerColor))
                ret ^= zEnPassant[currentPlayerColor.asInt][enPassant.col];
        }
        return ret;
    }

    public static long hash(Piece piece, Location pieceLoc) {
        return hash(piece.playerColor, piece.pieceType, pieceLoc);
    }

    public static long hash(PlayerColor piecePlayerColor, PieceType pieceType, Location currentLoc) {
        long ret = zPieces[piecePlayerColor.asInt][pieceType.asInt][currentLoc.asInt];

        return ret;
    }

    public static long hash(Location loc, PlayerColor playerColor) {
        return combineHashes(hash(loc), playerHash(playerColor));
    }

    public static long hash(Direction direction) {
        return zDirections[direction.asInt];
    }
}
