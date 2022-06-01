package ver14.Model.MoveGenerator;

import ver14.Model.*;
import ver14.Model.Eval.Eval;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.Moves.BasicMove;
import ver14.SharedClasses.Game.Moves.CastlingRights;
import ver14.SharedClasses.Game.Moves.Direction;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;

import java.util.ArrayList;


/**
 * Generates moves from a given position and {@link GenerationSettings}
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MoveGenerator {
    /**
     * a precalculated matrix of a square, holds the number of squares to the edge for every direction from any square
     */
    public static final int[][] numSquaresToEdge;
    /**
     * precalculated knight moves from every square on the board
     */
    private static final ArrayList<Location>[] knightMoves;
    /**
     * precalculated king moves from every square on the board
     */
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

    /**
     * The Generation settings.
     */
    private final @GenerationSettings
    int generationSettings;
    /**
     * My pieces.
     */
    private final PiecesBBs myPieces;
    /**
     * The Model.
     */
    private final Model model;
    /**
     * The Moving player color.
     */
    private final PlayerColor movingPlayerColor;
    /**
     * The Logic board.
     */
    private final Board logicBoard;
    /**
     * The Generated moves.
     */
    private final ModelMovesList generatedMoves;

    /**
     * Instantiates a new Move generator.
     *
     * @param model              the model
     * @param generationSettings the generation settings
     */
    private MoveGenerator(Model model, @GenerationSettings int generationSettings) {
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

//        moveGenerationHashMap.put(hash, generatedMoves);
    }

    /**
     * Generate moves from a given position for the current player to move. the moves will be legalized.
     *
     * @param model the model
     * @return a list of all the moves for the current player to move
     */
    public static ModelMovesList generateMoves(Model model) {
        return generateMoves(model, GenerationSettings.LEGALIZE);
    }

    /**
     * Generate moves according to the given {@link #generationSettings}
     *
     * @param model              the model
     * @param generationSettings the generation settings
     * @return the list of moves generated
     */
    public static ModelMovesList generateMoves(Model model, @GenerationSettings int generationSettings) {
        MoveGenerator mvg = new MoveGenerator(model, generationSettings);
        return mvg.getGeneratedMoves();
    }

    /**
     * Gets generated moves.
     *
     * @return the generated moves
     */
    public ModelMovesList getGeneratedMoves() {
        try {
            try {
                generatePawnMoves();
                generateSlidingMoves();
                generateKnightMoves();
                generateKingMoves();
            } catch (FoundLegalMove ex) {//throws when ur looking for any legal move and one is found
                return generatedMoves;
            }
        } finally {
            generatedMoves.finalizeHash();
        }

        if (generationSettings == GenerationSettings.ANNOTATE) {
            generatedMoves.initAnnotation();
        }

        if (generationSettings == GenerationSettings.EVAL || generationSettings == GenerationSettings.ANNOTATE) {
            for (Move move : generatedMoves) {
                model.applyMove(move);
                move.setMoveEvaluation(Eval.getEvaluation(model));
                model.undoMove(move);
            }
        }

        return generatedMoves;
    }

    /**
     * Generate pawn moves.
     *
     * @throws FoundLegalMove when the generation settings are set to find any legal move in the position and one is found
     */
    public void generatePawnMoves() throws FoundLegalMove {
        Bitboard bitboard = myPieces.getBB(PieceType.PAWN);
        int mult = movingPlayerColor.diff;
        Locs setLocs = bitboard.getSetLocs();
        for (int i = 0, setLocsSize = setLocs.size(); i < setLocsSize; i++) {
            Location pawnLoc = setLocs.get(i);
            int promotionRow = movingPlayerColor.getOpponent().startingRow;
            int startingIndex = generatedMoves.size();
            boolean promoting = pawnLoc.row + mult == promotionRow;
            Location oneStep = Location.getLoc(pawnLoc, 8 * mult);

            if (model.isSquareEmpty(oneStep)) {
                Move m = new Move(pawnLoc, oneStep);
                boolean added = generatedMoves.add(m, PieceType.PAWN);

                if (added && !promoting && movingPlayerColor.startingRow + mult == pawnLoc.row) {
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

    /**
     * Generate sliding moves.
     *
     * @throws FoundLegalMove the found legal move
     */
    public void generateSlidingMoves() throws FoundLegalMove {
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

    /**
     * Generate knight moves.
     *
     * @throws FoundLegalMove the found legal move
     */
    public void generateKnightMoves() throws FoundLegalMove {
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

    /**
     * Generate king moves.
     *
     * @throws FoundLegalMove the found legal move
     */
    public void generateKingMoves() throws FoundLegalMove {
        Location kingLoc = myPieces.getBB(PieceType.KING).getLastSetLoc();
        if (kingLoc == null)
            return;
        for (Location destination : kingMoves[kingLoc.asInt]) {
            Piece dest = logicBoard.getPiece(destination);
            Move move = new Move(kingLoc, destination);
            if (dest != null) {
                if (!model.isSamePlayer(kingLoc, destination)) {
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

    /**
     * Check pawn capture move.
     *
     * @param source the source
     * @param capLoc the cap loc
     * @return the move
     */
    private Move checkPawnCapture(Location source, Location capLoc) {
        if (capLoc == null || source.getMaxDistance(capLoc) > 1)
            return null;
        Piece dest = logicBoard.getPiece(capLoc);
        if (dest == null) {
            if (model.getEnPassantTargetLoc() == capLoc)
                return new Move(source, capLoc) {{
                    setCapturing(PieceType.PAWN);
                    setMoveFlag(MoveFlag.EnPassant);
                    setIntermediateMove(new BasicMove(model.getEnPassantActualLoc(), capLoc));
                }};
            return null;
        }
        if (dest.isOnMyTeam(movingPlayerColor)) {
            return null;
        }
        return new Move(source, capLoc, dest.pieceType);
    }

    /**
     * Generate sliding piece moves.
     *
     * @param pieceLocation   the piece location
     * @param movingPieceType the moving piece type
     * @throws FoundLegalMove the found legal move
     */
    public void generateSlidingPieceMoves(Location pieceLocation, PieceType movingPieceType) throws FoundLegalMove {
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

    /**
     * Num squares to edge int.
     *
     * @param loc       the loc
     * @param direction the direction
     * @return the int
     */
    public static int numSquaresToEdge(Location loc, Direction direction) {
        if (direction.getCombination().length != 1)
            return 1;
        return numSquaresToEdge[loc.asInt][direction.asInt];
    }

    /**
     * Gets model.
     *
     * @return the model
     */
    public Model getModel() {
        return model;
    }

//    /**
//     * Legalize.
//     */
//    public void legalize() {
//        generatedMoves.removeIf(move -> !isLegal(move));
//    }

    /**
     * Is legal boolean.
     *
     * @param move        the move
     * @param movingPiece the moving piece
     * @return the boolean
     */
    public boolean isLegal(Move move, PieceType movingPiece) {
        if (movingPiece == PieceType.KING) {
            if (move.getMoveFlag().isCastling && !canCastle(move)) {
                return false;
            }
//            return !AttackedSquares.isAttacked(model, move.getdestination(), movingPlayerColor.getOpponent());
        }

        model.applyMove(move);
        boolean ret = !model.isInCheck(movingPlayerColor);
        model.undoMove(move);
        return ret;
    }

    /**
     * Can castle boolean.
     *
     * @param castling the castling
     * @return the boolean
     */
    private boolean canCastle(Move castling) {
        if (model.isInCheck(movingPlayerColor))
            return false;

        CastlingRights.Side side = castling.getMoveFlag().castlingSide;

        for (int i = 1; i <= side.kingTravelDistance; i++) {
            if (model.isThreatened(Location.getLoc(castling.getSource(), i * side.mult), movingPlayerColor.getOpponent())) {
                return false;
            }
        }
        return true;
    }

//    public void generateRookMoves() {
//        for (Location rookLoc : myPieces.getBB(PieceType.ROOK).getSetLocs()) {
//            generateSlidingPieceMoves(rookLoc, PieceType.ROOK);
//        }
//    }
//
//    public void generateBishopMoves() {
//        for (Location bishopLoc : myPieces.getBB(PieceType.BISHOP).getSetLocs()) {
//            generateSlidingPieceMoves(bishopLoc, PieceType.BISHOP);
//        }
//    }
//
//    public void generateQueenMoves() {
//        for (Location queenLoc : myPieces.getBB(PieceType.QUEEN).getSetLocs()) {
//            generateSlidingPieceMoves(queenLoc, PieceType.QUEEN);
//        }
//    }

}
