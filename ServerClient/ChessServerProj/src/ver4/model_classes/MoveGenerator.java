package ver4.model_classes;

import ver4.SharedClasses.Location;
import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.board_setup.Board;
import ver4.SharedClasses.board_setup.Square;
import ver4.SharedClasses.moves.BasicMove;
import ver4.SharedClasses.moves.Castling;
import ver4.SharedClasses.moves.CastlingRights;
import ver4.SharedClasses.moves.Move;
import ver4.SharedClasses.pieces.Piece;
import ver4.SharedClasses.pieces.PieceType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class MoveGenerator {
    public static final ConcurrentHashMap<Long, ArrayList<Move>> hashMap = new ConcurrentHashMap<>();
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
        for (Location loc : Location.ALL_LOCS) {
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
        for (Location loc : Location.ALL_LOCS) {
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

    private final Model model;
    private final PlayerColor movingPlayerColor;
    private final Board logicBoard;
    private ArrayList<Move> generatedMoves = new ArrayList<>();

    private MoveGenerator(Model model, boolean legalize) {

        this.model = model;
        this.movingPlayerColor = model.getCurrentPlayer();
        this.logicBoard = model.getLogicBoard();
        generatedMoves.clear();
        long hash = model.getBoardHash().getFullHash();
        if (HashManager.MOVE_GENERATION && legalize && hashMap.containsKey(hash)) {
            generatedMoves = hashMap.get(hash);
            return;
        }
        generatePawnMoves();
        generateSlidingMoves();
        generateKnightMoves();
        generateKingMoves();

        if (legalize)
            legalize();

        if (HashManager.MOVE_GENERATION && legalize)
            hashMap.put(hash, generatedMoves);
    }

    private static void addIfNnull(ArrayList list, Object obj) {
        if (obj != null) {
            list.add(obj);
        }
    }

    public static ArrayList<Move> generateMoves(Model model) {
        MoveGenerator mvg = new MoveGenerator(model, true);
        return mvg.getGeneratedMoves();
    }

    public static ArrayList<Move> generatePseudoLegalMoves(Model model) {
        MoveGenerator mvg = new MoveGenerator(model, false);
        return mvg.getGeneratedMoves();
    }

    public void generateSlidingPieceMoves(Location pieceLocation, int[] directions) {

        for (int directionIndex = directions[0]; directionIndex < directions[1]; directionIndex++) {
            for (int n = 0; n < numSquaresToEdge[pieceLocation.asInt()][directionIndex]; n++) {

                Location targetSquare = Location.getLoc(pieceLocation.asInt() + directionOffsets[directionIndex] * (n + 1));
                if (targetSquare == null)
                    continue;
                Piece destinationPiece = logicBoard.getPiece(targetSquare);

                if (model.isSamePlayer(pieceLocation, targetSquare)) {
                    break;
                }
                boolean isCapture = destinationPiece != null;

                Move move = new Move(pieceLocation, targetSquare);
                if (isCapture) {
                    move.setCapturing(destinationPiece.getPieceType());
                }
                generatedMoves.add(move);
                if (isCapture) {
                    break;
                }
            }
        }
    }

    public void generateKingMoves() {
        Location kingLoc = model.getKing(movingPlayerColor);
        if (kingLoc == null)
            return;
        for (Location movingTo : kingMoves[kingLoc.asInt()]) {
            Piece dest = logicBoard.getPiece(movingTo);
            Move move = new Move(kingLoc, movingTo);
            if (dest != null) {
                if (!model.isSamePlayer(kingLoc, movingTo)) {
                    move.setCapturing(dest.getPieceType());
                } else {
                    move = null;
                }
            }
            addIfNnull(generatedMoves, move);
        }
        CastlingRights castlingRights = model.getCastlingRights();
        for (CastlingRights.Side side : CastlingRights.Side.SIDES) {
            if (castlingRights.isEnabled(movingPlayerColor, side)) {
                int rookCol = side.rookStartingCol;
                int diff = kingLoc.getCol() - rookCol;
                int multBySide = side == CastlingRights.Side.KING ? 1 : -1;
                int addToKingLoc = 2 * multBySide;
                Location kingFinalLoc = Location.getLoc(kingLoc, addToKingLoc);
                boolean add = true;
                for (int i = 1; i <= Math.abs(diff) - 1; i++) {
                    Location testingLoc = Location.getLoc(kingLoc, i * multBySide);
                    if (testingLoc == null || !model.isSquareEmpty(testingLoc)) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    generatedMoves.add(new Castling(kingLoc, kingFinalLoc, side));
                }
            }
        }
    }

    private boolean canCastle(Castling castling) {
        if (model.isInCheck())
            return false;

        int mult = castling.getSide() == CastlingRights.Side.KING ? 1 : -1;

        for (int i = 1; i <= 2; i++) {
            if (model.isThreatened(Location.getLoc(castling.getMovingFrom(), i * mult), movingPlayerColor.getOpponent())) {
                return false;
            }
        }
        return true;
    }


    public ArrayList<Move> getGeneratedMoves() {
        return generatedMoves;
    }

    private Move checkPawnCapture(Location movingFrom, Location capLoc) {
        if (capLoc == null || movingFrom.getMaxDistance(capLoc) > 1)
            return null;
        if (model.isSquareEmpty(capLoc)) {
            if (model.getEnPassantTargetLoc() == capLoc)
                return new Move(movingFrom, capLoc) {{
                    setCapturing(PieceType.PAWN);
                    setMoveFlag(MoveFlag.EnPassant);
                    setIntermediateMove(new BasicMove(model.getEnPassantActualLoc(), capLoc));
                }};
            return null;
        }
        Piece dest = logicBoard.getPiece(capLoc);
        if (dest == null || dest.isOnMyTeam(movingPlayerColor)) {
            return null;
        }
        return new Move(movingFrom, capLoc, dest.getPieceType());
    }

    public void generateSlidingMoves() {
        Bitboard[] pieces = model.getPlayersPieces();

        for (Location rookLoc : pieces[PieceType.ROOK.asInt()].getSetLocs()) {
            generateSlidingPieceMoves(rookLoc, rookDirections);
        }
        for (Location bishopLoc : pieces[PieceType.BISHOP.asInt()].getSetLocs()) {
            generateSlidingPieceMoves(bishopLoc, bishopDirections);
        }
        for (Location queenLoc : pieces[PieceType.QUEEN.asInt()].getSetLocs()) {
            generateSlidingPieceMoves(queenLoc, queenDirections);
        }
    }

    public void generateKnightMoves() {
        Bitboard bitboard = model.getPieceBitBoard(PieceType.KNIGHT);
        for (Location knightLoc : bitboard.getSetLocs()) {
            for (Location loc : knightMoves[knightLoc.asInt()]) {
                Piece dest = logicBoard.getPiece(loc);
                if (dest == null) {
                    generatedMoves.add(new Move(knightLoc, loc));
                } else if (!model.isSamePlayer(knightLoc, loc)) {
                    generatedMoves.add(new Move(knightLoc, loc, dest.getPieceType()));
                }
            }
        }
    }

    public void generatePawnMoves() {
        Bitboard bitboard = model.getPlayersPieces()[PieceType.PAWN.asInt()];
        int mult = movingPlayerColor == PlayerColor.WHITE ? -1 : 1;
        for (Location pawnLoc : bitboard.getSetLocs()) {
            int promotionRow = Piece.getStartingRow(movingPlayerColor.getOpponent());
            ArrayList<Move> currentPawnMoves = new ArrayList<>();
            boolean promoting = pawnLoc.getRow() + mult == promotionRow;
            Location oneStep = Location.getLoc(pawnLoc, 8 * mult);

            if (model.isSquareEmpty(oneStep)) {
                Move m = new Move(pawnLoc, oneStep);
                currentPawnMoves.add(m);

                if (!promoting && Piece.getStartingRow(movingPlayerColor) + mult == pawnLoc.getRow()) {
                    Location doublePawnPush = Location.getLoc(pawnLoc.asInt() + 8 * mult * 2);
                    if (model.isSquareEmpty(doublePawnPush)) {
                        m = new Move(pawnLoc, doublePawnPush) {{
                            setEnPassantLoc(oneStep);
                            setMoveFlag(Move.MoveFlag.DoublePawnPush);
                        }};
                        currentPawnMoves.add(m);
                    }
                }
            }

            addIfNnull(currentPawnMoves, checkPawnCapture(pawnLoc, Location.getLoc(pawnLoc, 9 * mult)));

            addIfNnull(currentPawnMoves, checkPawnCapture(pawnLoc, Location.getLoc(pawnLoc, 7 * mult)));

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
            generatedMoves.addAll(currentPawnMoves);
        }
    }

    public void legalize() {
        for (Iterator<Move> iterator = generatedMoves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();
            if (move instanceof Castling) {
                if (!canCastle((Castling) move)) {
                    iterator.remove();
                    continue;
                }
            }
            Square target = model.getSquare(move.getMovingTo());
            if (!target.isEmpty() && target.getPiece().getPieceType() == PieceType.KING) {
                iterator.remove();
                continue;
            }
            model.applyMove(move);
            if (model.isInCheck(movingPlayerColor))
                iterator.remove();
            model.undoMove();
        }
    }


}
