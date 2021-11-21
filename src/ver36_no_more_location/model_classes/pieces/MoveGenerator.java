package ver36_no_more_location.model_classes.pieces;

import ver36_no_more_location.Location;
import ver36_no_more_location.Player;
import ver36_no_more_location.model_classes.Bitboard;
import ver36_no_more_location.model_classes.Board;
import ver36_no_more_location.model_classes.CastlingAbility;
import ver36_no_more_location.model_classes.moves.BasicMove;
import ver36_no_more_location.model_classes.moves.Castling;
import ver36_no_more_location.model_classes.moves.Move;

import java.util.ArrayList;
import java.util.Iterator;

public class MoveGenerator {
    private static final int[] directionOffsets = {8, -8, -1, 1, 7, -7, 9, -9};
    private static final int[][] numSquaresToEdge;
    private static final ArrayList<Location>[] knightMoves;
    private static final ArrayList<Location>[] kingMoves;
    private static final int[] rookDirections = {0, 4};
    private static final int[] bishopDirections = {4, 8};
    private static final int[] queenDirections = {0, 8};

    static {
        numSquaresToEdge = new int[Location.NUM_OF_SQUARES][];
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                int up = 7 - row;
                int down = row;
                int left = col;
                int right = 7 - col;

                Location square = Location.getLoc(row, col);
                numSquaresToEdge[square.asInt()] = new int[]{
                        up,
                        down,
                        left,
                        right,
                        Math.min(up, left),
                        Math.min(down, right),
                        Math.min(up, right),
                        Math.min(down, left)
                };
            }
        }

        knightMoves = new ArrayList[Location.NUM_OF_SQUARES];
        int[] knightCombos = {10, 17, 15, 6, -10, -17, -15, -6};
        for (Location loc : Location.allLocs()) {
            ArrayList<Location> locs = new ArrayList<>();
            for (int add : knightCombos) {
                Location tLoc = Location.getLoc(loc, add);
                int maxDistance = loc.getMaxDistance(tLoc);
                if (maxDistance == 2)
                    addIfNnull(locs, tLoc);
            }
            knightMoves[loc.asInt()] = locs;
        }

        kingMoves = new ArrayList[Location.NUM_OF_SQUARES];
        for (Location loc : Location.allLocs()) {
            ArrayList<Location> locs = new ArrayList<>();
            for (int dirOffset : directionOffsets) {
                Location tLoc = Location.getLoc(loc, dirOffset);
                int maxDistance = loc.getMaxDistance(tLoc);
                if (maxDistance == 1)
                    addIfNnull(locs, tLoc);

            }
            kingMoves[loc.asInt()] = locs;
        }
    }

    private static void addIfNnull(ArrayList list, Object obj) {
        if (obj != null) {
            list.add(obj);
        }
    }

    public static ArrayList<Move> generateMoves(Board board) {
        return generateMoves(board, true);
    }

    public static ArrayList<Move> generateMoves(Board board, boolean legalize) {
        ArrayList<Move> ret = new ArrayList<>();

        ret.addAll(generatePawnMoves(board));

        ret.addAll(generateSlidingMoves(board));

        ret.addAll(generateKnightMoves(board));

        ret.addAll(generateKingMoves(board));

        if (legalize)
            legalize(ret, board);
        return ret;
    }

    public static void legalize(ArrayList<Move> moves, Board board) {
        for (Iterator<Move> iterator = moves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();
            if (move instanceof Castling && board.isInCheck()) {
                iterator.remove();
                continue;
            }
            board.applyMove(move);
            if (board.isInCheck(board.getOpponent()))
                iterator.remove();
            board.undoMove();
        }
    }

    public static ArrayList<Move> generateKingMoves(Board board) {
        ArrayList<Move> ret = new ArrayList<>();
        Player currentPlayer = board.getCurrentPlayer();
        Location kingLoc = board.getKing(currentPlayer);
        if (kingLoc == null)
            return ret;
        for (Location movingTo : kingMoves[kingLoc.asInt()]) {
            Piece dest = board.getPiece(movingTo);
            Move move = new Move(kingLoc, movingTo);
            if (dest != null) {
                if (!board.isSamePlayer(kingLoc, movingTo)) {
                    move.setCapturing(dest.getPieceType());
                } else {
                    move = null;
                }
            }
            addIfNnull(ret, move);
        }
        boolean[] castling = CastlingAbility.getCastling(board.getCurrentPlayer(), board.getCastlingAbility());
        for (int side = 0, castlingLength = castling.length; side < castlingLength; side++) {
            boolean canCastle = castling[side];
            if (canCastle) {
                int rookCol = CastlingAbility.ROOK_STARTING_COL[side];
                int diff = kingLoc.getCol() - rookCol;
                int multBySide = side == CastlingAbility.KING_SIDE ? 1 : -1;
                int addToKingLoc = 2 * multBySide;
                Location kingFinalLoc = Location.getLoc(kingLoc, addToKingLoc);
                boolean add = true;
                for (int i = 1; i < Math.abs(diff) - 1; i++) {
                    Location testingLoc = Location.getLoc(kingLoc, i * multBySide);

                    if (testingLoc == null || !board.isSquareEmpty(testingLoc)) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    ret.add(new Castling(kingLoc, kingFinalLoc, side));
                }
            }
        }
        return ret;
    }

    public static ArrayList<Move> generateKnightMoves(Board board) {
        ArrayList<Move> ret = new ArrayList<>();
        Bitboard bitboard = board.getPieceBitBoard(PieceType.KNIGHT);
        for (Location knightLoc : bitboard.getSetLocs()) {
            for (Location loc : knightMoves[knightLoc.asInt()]) {
                Piece dest = board.getPiece(loc);
                if (dest == null) {
                    ret.add(new Move(knightLoc, loc));
                } else if (!board.isSamePlayer(knightLoc, loc)) {
                    ret.add(new Move(knightLoc, loc, dest.getPieceType()));
                }
            }
        }
        return ret;
    }

    public static ArrayList<Move> generatePawnMoves(Board board) {
        ArrayList<Move> ret = new ArrayList<>();
        Bitboard bitboard = board.getPlayersPieces()[PieceType.PAWN.asInt()];
        Player movingPlayer = board.getCurrentPlayer();
        int mult = movingPlayer == Player.WHITE ? -1 : 1;
        for (Location pawnLoc : bitboard.getSetLocs()) {
            int promotionRow = Piece.getStartingRow(movingPlayer.getOpponent());
            assert pawnLoc.getRow() != promotionRow;
            ArrayList<Move> currentPawnMoves = new ArrayList<>();
            boolean promoting = pawnLoc.getRow() + mult == promotionRow;
            Location oneStep = Location.getLoc(pawnLoc, 8 * mult);
            if (board.isSquareEmpty(oneStep)) {
                currentPawnMoves.add(new Move(pawnLoc, oneStep));

                if (!promoting && Piece.getStartingRow(movingPlayer) + mult == pawnLoc.getRow()) {
                    Location doublePawnPush = Location.getLoc(pawnLoc.asInt() + 8 * mult * 2);
                    if (board.isSquareEmpty(doublePawnPush)) {
                        currentPawnMoves.add(new Move(pawnLoc, doublePawnPush) {{
                            setMoveFlag(MoveFlag.DoublePawnPush);
                            setEnPassantLoc(oneStep);
                        }});
                    }
                }
            }

            addIfNnull(currentPawnMoves, checkPawnCapture(board, movingPlayer, pawnLoc, Location.getLoc(pawnLoc, 9 * mult)));

            addIfNnull(currentPawnMoves, checkPawnCapture(board, movingPlayer, pawnLoc, Location.getLoc(pawnLoc, 7 * mult)));

            if (promoting) {
                ArrayList<Move> replacement = new ArrayList<>();
                for (Move move : currentPawnMoves) {
                    boolean set = false;
                    for (PieceType pieceType : PieceType.CAN_PROMOTE_TO) {
                        if (!set) {
                            move.setPromotingTo(pieceType);
                            replacement.add(move);
                            set = true;
                        } else {
                            replacement.add(new Move(move) {{
                                setPromotingTo(pieceType);
                            }});
                        }
                    }
                }
                currentPawnMoves = replacement;
            }
            ret.addAll(currentPawnMoves);
        }
        return ret;
    }

    private static Move checkPawnCapture(Board board, Player movingPlayer, Location movingFrom, Location capLoc) {
        if (capLoc == null)
            return null;
        if (board.isSquareEmpty(capLoc)) {
            if (board.getEnPassantTargetLoc() == capLoc)
                return new Move(movingFrom, capLoc) {{
                    setCapturing(PieceType.PAWN);
                    setMoveFlag(MoveFlag.EnPassant);
                    setIntermediateMove(new BasicMove(board.getEnPassantActualLoc(), capLoc));
                }};
            return null;
        }
        Piece dest = board.getPiece(capLoc);
        if (dest == null || dest.isOnMyTeam(movingPlayer)) {
            return null;
        }
        return new Move(movingFrom, capLoc, dest.getPieceType());
    }

    public static ArrayList<Move> generateSlidingMoves(Board board) {
        ArrayList<Move> ret = new ArrayList<>();
        Bitboard[] pieces = board.getPlayersPieces();

        for (Location rookLoc : pieces[PieceType.ROOK.asInt()].getSetLocs()) {
            ret.addAll(generateSlidingPieceMoves(board, rookLoc, rookDirections));
        }
        for (Location bishopLoc : pieces[PieceType.BISHOP.asInt()].getSetLocs()) {
            ret.addAll(generateSlidingPieceMoves(board, bishopLoc, bishopDirections));
        }
        for (Location queenLoc : pieces[PieceType.QUEEN.asInt()].getSetLocs()) {
            ret.addAll(generateSlidingPieceMoves(board, queenLoc, queenDirections));
        }
        return ret;
    }

    public static ArrayList<Move> generateSlidingPieceMoves(Board board, Location pieceLocation, int[] directions) {
        ArrayList<Move> ret = new ArrayList<>();

        for (int directionIndex = directions[0]; directionIndex < directions[1]; directionIndex++) {
            for (int n = 0; n < numSquaresToEdge[pieceLocation.asInt()][directionIndex]; n++) {

                Location targetSquare = Location.getLoc(pieceLocation.asInt() + directionOffsets[directionIndex] * (n + 1));
                if (targetSquare == null)
                    continue;
                Piece destinationPiece = board.getPiece(targetSquare);

                if (board.isSamePlayer(pieceLocation, targetSquare)) {
                    break;
                }
                boolean isCapture = destinationPiece != null;

                Move move = new Move(pieceLocation, targetSquare);
                if (isCapture) {
                    move.setCapturing(destinationPiece.getPieceType());
                }
                ret.add(move);
                if (isCapture) {
                    break;
                }
            }
        }
        return ret;
    }
}
