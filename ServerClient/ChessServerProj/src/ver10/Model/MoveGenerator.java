package ver10.Model;

import ver10.SharedClasses.Location;
import ver10.SharedClasses.PlayerColor;
import ver10.SharedClasses.board_setup.Board;
import ver10.SharedClasses.moves.*;
import ver10.SharedClasses.pieces.Piece;
import ver10.SharedClasses.pieces.PieceType;
import ver10.Model.hashing.HashManager;
import ver10.Model.hashing.my_hash_maps.MyHashMap;

import java.util.ArrayList;
import java.util.Iterator;

public class MoveGenerator {
    public static final MyHashMap moveGenerationHashMap = new MyHashMap(HashManager.Size.MOVE_GENERATOR);
    public static final int[][] numSquaresToEdge;
    private static final ArrayList<Location>[] knightMoves;
    private static final ArrayList<Location>[] kingMoves;

    static {
        numSquaresToEdge = new int[Location.NUM_OF_SQUARES][Direction.NUM_OF_DIRECTIONS_WO_KNIGHT];
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                int up = 7 - row;
                int down = row;
                int left = col;
                int right = 7 - col;

                int square = Location.getLoc(row, col).asInt();
                numSquaresToEdge[square][Direction.U.asInt()] = up;
                numSquaresToEdge[square][Direction.D.asInt()] = down;
                numSquaresToEdge[square][Direction.L.asInt()] = left;
                numSquaresToEdge[square][Direction.R.asInt()] = right;

                numSquaresToEdge[square][Direction.U_L.asInt()] = Math.min(up, left);
                numSquaresToEdge[square][Direction.U_R.asInt()] = Math.min(up, right);

                numSquaresToEdge[square][Direction.D_L.asInt()] = Math.min(down, left);
                numSquaresToEdge[square][Direction.D_R.asInt()] = Math.min(down, right);
            }
        }

        knightMoves = new ArrayList[Location.NUM_OF_SQUARES];
        for (Location loc : Location.ALL_LOCS) {
            ArrayList<Location> locs = new ArrayList<>();
            for (Direction direction : PieceType.KNIGHT.getAttackingDirections()) {
                Location tLoc = Location.getLoc(loc, direction);
                int maxDistance = loc.getMaxDistance(tLoc);
                if (maxDistance == 2 && tLoc != null) {
                    locs.add(tLoc);
                }

            }
            knightMoves[loc.asInt()] = locs;
        }

        kingMoves = new ArrayList[Location.NUM_OF_SQUARES];
        for (Location loc : Location.ALL_LOCS) {
            ArrayList<Location> locs = new ArrayList<>();
            for (Direction direction : PieceType.KING.getAttackingDirections()) {
                Location tLoc = Location.getLoc(loc, direction);
                int maxDistance = loc.getMaxDistance(tLoc);
                if (maxDistance == 1 && tLoc != null)
                    locs.add(tLoc);

            }
            kingMoves[loc.asInt()] = locs;
        }
    }

    private PiecesBBs myPieces;
    private Model model;
    private PlayerColor movingPlayerColor;
    private Board logicBoard;
    private ModelMovesList generatedMoves;

    private MoveGenerator(Model model) {
        long hash = model.getBoardHash().getFullHash();
        if (moveGenerationHashMap.containsKey(hash)) {
            generatedMoves = (ModelMovesList) moveGenerationHashMap.get(hash);
            return;
        }
        this.model = model;
        this.movingPlayerColor = model.getCurrentPlayer();
        this.logicBoard = model.getLogicBoard();
        this.myPieces = model.getPlayersPieces(movingPlayerColor);

        generatedMoves = new ModelMovesList();

        generatePawnMoves();
        generateSlidingMoves();
        generateKnightMoves();
        generateKingMoves();

        generatedMoves.doneAdding();

        legalize();

        moveGenerationHashMap.put(hash, generatedMoves);
    }

    public void generatePawnMoves() {
        Bitboard bitboard = model.getPlayersPieces(movingPlayerColor).getBB(PieceType.PAWN);
        int mult = movingPlayerColor == PlayerColor.WHITE ? -1 : 1;
        for (Location pawnLoc : bitboard.getSetLocs()) {
            int promotionRow = Piece.getStartingRow(movingPlayerColor.getOpponent());
            ModelMovesList currentPawnMoves = new ModelMovesList();
            boolean promoting = pawnLoc.getRow() + mult == promotionRow;
            Location oneStep = Location.getLoc(pawnLoc, 8 * mult);

            if (model.isSquareEmpty(oneStep)) {
                Move m = new Move(pawnLoc, oneStep);
                //fixme if doublepawnpush is getting out of check
                boolean ad = currentPawnMoves.add(m, PieceType.PAWN);

                if (ad && !promoting && Piece.getStartingRow(movingPlayerColor) + mult == pawnLoc.getRow()) {
                    Location doublePawnPush = Location.getLoc(pawnLoc.asInt() + 8 * mult * 2);
                    if (model.isSquareEmpty(doublePawnPush)) {
                        m = new Move(pawnLoc, doublePawnPush) {{
                            setEnPassantLoc(oneStep);
                            setMoveFlag(MoveFlag.DoublePawnPush);
                        }};
                        currentPawnMoves.add(m, PieceType.PAWN);
                    }
                }
            }
            currentPawnMoves.add(checkPawnCapture(pawnLoc, Location.getLoc(pawnLoc, 9 * mult)), PieceType.PAWN);

            currentPawnMoves.add(checkPawnCapture(pawnLoc, Location.getLoc(pawnLoc, 7 * mult)), PieceType.PAWN);

            currentPawnMoves.doneAdding();

            if (promoting) {
                ModelMovesList replacement = new ModelMovesList();
                for (Move move : currentPawnMoves) {
                    boolean set = false;
                    for (PieceType pieceType : PieceType.CAN_PROMOTE_TO) {
                        if (!set) {
                            move.setPromotingTo(pieceType);
                            replacement.add(move, PieceType.PAWN);
                            set = true;
                        } else {
                            replacement.add(new Move(move) {{
                                setPromotingTo(pieceType);
                            }}, PieceType.PAWN);
                        }
                    }
                }
                replacement.doneAdding();
                currentPawnMoves = replacement;
            }
            generatedMoves.addAll(currentPawnMoves, PieceType.PAWN);
        }
    }

    public void generateSlidingMoves() {
        for (Location rookLoc : myPieces.getBB(PieceType.ROOK).getSetLocs()) {
            generateSlidingPieceMoves(rookLoc, PieceType.ROOK);
        }
        for (Location bishopLoc : myPieces.getBB(PieceType.BISHOP).getSetLocs()) {
            generateSlidingPieceMoves(bishopLoc, PieceType.BISHOP);
        }
        for (Location queenLoc : myPieces.getBB(PieceType.QUEEN).getSetLocs()) {
            generateSlidingPieceMoves(queenLoc, PieceType.QUEEN);
        }
    }

    public void generateKnightMoves() {
        Bitboard bitboard = myPieces.getBB(PieceType.KNIGHT);
        for (Location knightLoc : bitboard.getSetLocs()) {
            for (Location loc : knightMoves[knightLoc.asInt()]) {
                Piece dest = logicBoard.getPiece(loc);
                if (dest == null) {
                    generatedMoves.add(new Move(knightLoc, loc), PieceType.KNIGHT);
                } else if (!model.isSamePlayer(knightLoc, loc)) {
                    generatedMoves.add(new Move(knightLoc, loc, dest.getPieceType()), PieceType.KNIGHT);
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
            generatedMoves.add(move, PieceType.KING);

        }
        CastlingRights castlingRights = model.getCastlingRights();
        if (castlingRights.hasAny(movingPlayerColor))
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
                        generatedMoves.add(new Castling(kingLoc, kingFinalLoc, side), PieceType.KING);
                    }
                }
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
//            Square target = model.getSquare(move.getMovingTo());
//            if (!target.isEmpty() && target.getPiece().getPieceType() == PieceType.KING) {
//                iterator.remove();
//                continue;
//            }
            model.applyMove(move);
            if (model.isInCheck(movingPlayerColor))
                iterator.remove();
            model.undoMove();
        }
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

    public void generateSlidingPieceMoves(Location pieceLocation, PieceType movingPieceType) {
        for (Direction direction : movingPieceType.getAttackingDirections()) {
            for (int n = 1; n <= numSquaresToEdge[pieceLocation.asInt()][direction.asInt()]; n++) {
                Location targetSquare = Location.getLoc(pieceLocation, n, direction);
                if (targetSquare == null)
                    break;
                Piece destinationPiece = logicBoard.getPiece(targetSquare);

                if (model.isSamePlayer(pieceLocation, targetSquare)) {
                    break;
                }
                boolean isCapture = destinationPiece != null;

                Move move = new Move(pieceLocation, targetSquare);
                if (isCapture) {
                    move.setCapturing(destinationPiece.getPieceType());
                }
                generatedMoves.add(move, movingPieceType);
                if (isCapture) {
                    break;
                }
            }
        }
    }

    private boolean canCastle(Castling castling) {
        if (model.isInCheck(movingPlayerColor))
            return false;

        int mult = castling.getSide() == CastlingRights.Side.KING ? 1 : -1;

        for (int i = 1; i <= 2; i++) {
            if (model.isThreatened(Location.getLoc(castling.getMovingFrom(), i * mult), movingPlayerColor.getOpponent())) {
                return false;
            }
        }
        return true;
    }

    public static ModelMovesList generateMoves(Model model) {
        MoveGenerator mvg = new MoveGenerator(model);
        return mvg.getGeneratedMoves();
    }

    public ModelMovesList getGeneratedMoves() {
        return generatedMoves;
    }

}
