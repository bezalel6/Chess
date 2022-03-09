package ver14.Model.MoveGenerator;

import ver14.Model.*;
import ver14.SharedClasses.Location;
import ver14.SharedClasses.PlayerColor;
import ver14.SharedClasses.board_setup.Board;
import ver14.SharedClasses.moves.BasicMove;
import ver14.SharedClasses.moves.CastlingRights;
import ver14.SharedClasses.moves.Direction;
import ver14.SharedClasses.moves.Move;
import ver14.SharedClasses.pieces.Piece;
import ver14.SharedClasses.pieces.PieceType;

import java.util.ArrayList;
import java.util.Iterator;

public class MoveGenerator {
    //    public static final MyHashMap<ModelMovesList> moveGenerationHashMap = new MyHashMap<>(HashManager.Size.MOVE_GENERATOR);
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

                int square = Location.getLoc(row, col).asInt;
                numSquaresToEdge[square][Direction.U.asInt] = up;
                numSquaresToEdge[square][Direction.D.asInt] = down;
                numSquaresToEdge[square][Direction.L.asInt] = left;
                numSquaresToEdge[square][Direction.R.asInt] = right;

                numSquaresToEdge[square][Direction.U_L.asInt] = Math.min(up, left);
                numSquaresToEdge[square][Direction.U_R.asInt] = Math.min(up, right);

                numSquaresToEdge[square][Direction.D_L.asInt] = Math.min(down, left);
                numSquaresToEdge[square][Direction.D_R.asInt] = Math.min(down, right);
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
            knightMoves[loc.asInt] = locs;
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
            kingMoves[loc.asInt] = locs;
        }

    }

    private GenerationSettings generationSettings;
    private PiecesBBs myPieces;
    private Model model;
    private PlayerColor movingPlayerColor;
    private Board logicBoard;
    private ModelMovesList generatedMoves;

    private MoveGenerator(Model model, GenerationSettings generationSettings) {
//        long hash = Zobrist.combineHashes(model.getBoardHash().getFullHash(), Zobrist.hash(generationSettings));
//        if (moveGenerationHashMap.containsKey(hash)) {
//            generatedMoves = moveGenerationHashMap.get(hash);
//            return;
//        }
        this.model = model;
        this.movingPlayerColor = model.getCurrentPlayer();
        this.logicBoard = model.getLogicBoard();
        this.myPieces = model.getPlayersPieces(movingPlayerColor);

        this.generationSettings = generationSettings;
        this.generatedMoves = new ModelMovesList(this, generationSettings);

        generatePawnMoves();
        generateSlidingMoves();
        generateKnightMoves();
        generateKingMoves();

        generatedMoves.doneAdding();

        if (generationSettings.legalize)
            legalize();

//        moveGenerationHashMap.put(hash, generatedMoves);
    }

    public void generatePawnMoves() {
        Bitboard bitboard = myPieces.getBB(PieceType.PAWN);
        int mult = movingPlayerColor.diff;
        LocsList setLocs = bitboard.getSetLocs();
        for (int i = 0, setLocsSize = setLocs.size(); i < setLocsSize; i++) {
            Location pawnLoc = setLocs.get(i);
            int promotionRow = movingPlayerColor.getOpponent().startingRow;
//            ModelMovesList currentPawnMoves = new ModelMovesList(this, generationSettings);
            int startingIndex = generatedMoves.size();
            boolean promoting = pawnLoc.row + mult == promotionRow;
            Location oneStep = Location.getLoc(pawnLoc, 8 * mult);

            if (model.isSquareEmpty(oneStep)) {
                Move m = new Move(pawnLoc, oneStep);
                boolean ad = generatedMoves.add(m, PieceType.PAWN);

                if (ad && !promoting && movingPlayerColor.startingRow + mult == pawnLoc.row) {
                    Location doublePawnPush = Location.getLoc(pawnLoc.asInt + 8 * mult * 2);
                    if (model.isSquareEmpty(doublePawnPush)) {
                        m = new Move(pawnLoc, doublePawnPush) {{
                            setEnPassantLoc(oneStep);
                            setMoveFlag(MoveFlag.DoublePawnPush);
                        }};
                        generatedMoves.add(m, PieceType.PAWN);
                    }
                }
            }
            generatedMoves.add(checkPawnCapture(pawnLoc, Location.getLoc(pawnLoc, 9 * mult)), PieceType.PAWN);

            generatedMoves.add(checkPawnCapture(pawnLoc, Location.getLoc(pawnLoc, 7 * mult)), PieceType.PAWN);

            if (promoting) {
                for (int j = startingIndex, size = generatedMoves.size(); j < size; j++) {
                    Move move = generatedMoves.get(j);
                    boolean set = false;
                    for (PieceType pieceType : PieceType.CAN_PROMOTE_TO) {
                        if (!set) {
                            move.setPromotingTo(pieceType);
                            set = true;
                        } else {
                            generatedMoves.add(new Move(move) {{
                                setPromotingTo(pieceType);
                            }}, PieceType.PAWN);
                        }
                    }
                }
            }
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
            for (Location loc : knightMoves[knightLoc.asInt]) {
                Piece dest = logicBoard.getPiece(loc);
                if (dest == null) {
                    generatedMoves.add(new Move(knightLoc, loc), PieceType.KNIGHT);
                } else if (!model.isSamePlayer(knightLoc, loc)) {
                    generatedMoves.add(new Move(knightLoc, loc, dest.pieceType), PieceType.KNIGHT);
                }
            }
        }
    }

    public void generateKingMoves() {
        Location kingLoc = myPieces.getBB(PieceType.KING).getLastSetLoc();
        if (kingLoc == null)
            return;
        for (Location movingTo : kingMoves[kingLoc.asInt]) {
            Piece dest = logicBoard.getPiece(movingTo);
            Move move = new Move(kingLoc, movingTo);
            if (dest != null) {
                if (!model.isSamePlayer(kingLoc, movingTo)) {
                    move.setCapturing(dest.pieceType);
                } else {
                    move = null;
                }
            }
            generatedMoves.add(move, PieceType.KING);

        }
        CastlingRights castlingRights = model.getCastlingRights();
        if (castlingRights.hasAny(movingPlayerColor)) {
            CastlingRights.Side[] sides = CastlingRights.Side.SIDES;
            sides:
            for (int j = 0, sidesLength = sides.length; j < sidesLength; j++) {
                CastlingRights.Side side = sides[j];
                if (castlingRights.isEnabled(movingPlayerColor, side)) {
                    for (int i = 1; i <= side.kingTravelDistance; i++) {
                        Location testingLoc = Location.getLoc(kingLoc, i * side.mult);
                        if (testingLoc == null || !model.isSquareEmpty(testingLoc)) {
                            continue sides;
                        }
                    }
                    generatedMoves.add(Move.castling(kingLoc, side.kingFinalLoc(kingLoc), side), PieceType.KING);
                }
            }
        }
    }

    public void legalize() {
        for (Iterator<Move> iterator = generatedMoves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();
            if (move.getMoveFlag().isCastling) {
                if (!canCastle(move)) {
                    iterator.remove();
                    continue;
                }
            }

            model.applyMove(move);
            if (model.isInCheck(movingPlayerColor))
                iterator.remove();
            model.undoMove();
        }
    }

    private Move checkPawnCapture(Location movingFrom, Location capLoc) {
        if (capLoc == null || movingFrom.getMaxDistance(capLoc) > 1)
            return null;
        Piece dest = logicBoard.getPiece(capLoc);
        if (dest == null) {
            if (model.getEnPassantTargetLoc() == capLoc)
                return new Move(movingFrom, capLoc) {{
                    setCapturing(PieceType.PAWN);
                    setMoveFlag(MoveFlag.EnPassant);
                    setIntermediateMove(new BasicMove(model.getEnPassantActualLoc(), capLoc));
                }};
            return null;
        }
        if (dest.isOnMyTeam(movingPlayerColor)) {
            return null;
        }
        return new Move(movingFrom, capLoc, dest.pieceType);
    }

    public void generateSlidingPieceMoves(Location pieceLocation, PieceType movingPieceType) {
        for (Direction direction : movingPieceType.getAttackingDirections()) {
            for (int n = 1; n <= numSquaresToEdge[pieceLocation.asInt][direction.asInt]; n++) {
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
                    move.setCapturing(destinationPiece.pieceType);
                }
                generatedMoves.add(move, movingPieceType);
                if (isCapture) {
                    break;
                }
            }
        }
    }

    private boolean canCastle(Move castling) {
        if (model.isInCheck(movingPlayerColor))
            return false;

        CastlingRights.Side side = castling.getMoveFlag().castlingSide;

        int mult = side.mult;

        for (int i = 1; i <= side.kingTravelDistance; i++) {
            if (model.isThreatened(Location.getLoc(castling.getMovingFrom(), i * mult), movingPlayerColor.getOpponent())) {
                return false;
            }
        }
        return true;
    }

    public static ModelMovesList generateMoves(Model model) {
        return generateMoves(model, GenerationSettings.defaultSettings);
    }

    public static ModelMovesList generateMoves(Model model, GenerationSettings generationSettings) {
        MoveGenerator mvg = new MoveGenerator(model, generationSettings);
        return mvg.getGeneratedMoves();
    }

    public ModelMovesList getGeneratedMoves() {
        return generatedMoves;
    }

    public void generateRookMoves() {
        for (Location rookLoc : myPieces.getBB(PieceType.ROOK).getSetLocs()) {
            generateSlidingPieceMoves(rookLoc, PieceType.ROOK);
        }
    }

    public void generateBishopMoves() {
        for (Location bishopLoc : myPieces.getBB(PieceType.BISHOP).getSetLocs()) {
            generateSlidingPieceMoves(bishopLoc, PieceType.BISHOP);
        }
    }

    public void generateQueenMoves() {
        for (Location queenLoc : myPieces.getBB(PieceType.QUEEN).getSetLocs()) {
            generateSlidingPieceMoves(queenLoc, PieceType.QUEEN);
        }
    }

}
