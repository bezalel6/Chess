package ver14.Model;

import ver14.Model.MoveGenerator.GenerationSettings;
import ver14.Model.MoveGenerator.MoveGenerator;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.Moves.MoveAnnotation;
import ver14.SharedClasses.Game.Moves.MovesList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;


/**
 * Model moves list - represents a list of moves with a few features unique to the server side.
 * <br/>
 * calculating move annotation is done using the {@link Model}. and {@link MoveGenerator}
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ModelMovesList extends MovesList {
    /**
     * The Generator.
     */
    private final MoveGenerator generator;
    /**
     * The Generation settings.
     */
    private final int generationSettings;
    /**
     * The Unique moves.
     */
    private HashMap<Integer, ArrayList<Move>> uniqueMoves = null;
    /**
     * The Rejected pseudo legal.
     */
    private ArrayList<Move> rejectedPseudoLegal = new ArrayList<>();


    /**
     * Instantiates a new Model moves list.
     *
     * @param generator          the generator
     * @param generationSettings the generation settings
     */
    public ModelMovesList(MoveGenerator generator, @GenerationSettings int generationSettings) {
        this.generator = generator;
        this.generationSettings = generationSettings;
    }

    /**
     * Add all.
     *
     * @param other     the other
     * @param pieceType the piece type
     * @throws FoundLegalMove the found legal move
     */
    public void addAll(ModelMovesList other, PieceType pieceType) throws FoundLegalMove {
        for (Move move : other)
            add(move, pieceType);
    }

    /**
     * Add boolean.
     *
     * @param adding      the adding
     * @param movingPiece the moving piece
     * @return the boolean
     * @throws FoundLegalMove the found legal move
     */
    public boolean add(Move adding, PieceType movingPiece) throws FoundLegalMove {
        if (adding == null)
            return false;
        adding.setCreatedListHashSupplier(this::getFinalHash);

        adding.setMovingColor(generator.getModel().getCurrentPlayer());

        if ((generationSettings == GenerationSettings.ANY_LEGAL)) {
            if (generator.isLegal(adding, movingPiece)) {
                super.add(adding);
                throw new FoundLegalMove();
            }
        } else {
            if ((generationSettings & GenerationSettings.LEGALIZE) == 0 || (generator.isLegal(adding, movingPiece) && (generationSettings != GenerationSettings.QUIESCE || isQuiescence(adding, movingPiece)))) {
                super.add(adding);
            } else if (rejectedPseudoLegal != null) {
                rejectedPseudoLegal.add(adding);
            }
        }

        return true;
    }

    /**
     * Is quiescence boolean.
     *
     * @param move   the move
     * @param moving the moving
     * @return the boolean
     */
    private boolean isQuiescence(Move move, PieceType moving) {
        return move.isCapturing();
    }

    /**
     * Initializes moves notation.
     */
    public void initAnnotation() {
        uniqueMoves = new HashMap<>();
        Stream.concat(this.stream(), rejectedPseudoLegal.stream()).forEach(move -> {
            Piece movingPiece = generator.getModel().getSquare(move.getSource()).getPiece();
            assert movingPiece != null;
//            if (movingPiece.pieceType == PieceType.PAWN) {
//                return;
//            }
            int hash = move.getDestination().hash(movingPiece.pieceType);
            if (movingPiece.pieceType != PieceType.PAWN && uniqueMoves.containsKey(hash)) {
                ArrayList<Move> moves = uniqueMoves.get(hash);
                moves.add(move);
                for (Move unUniqueMove : moves) {
                    Location source = unUniqueMove.getSource();
                    String uniqueStr = source.toString();
                    boolean uniqueRow = true;
                    boolean uniqueCol = true;
                    for (Move other : moves.stream().filter(m -> !m.equals(unUniqueMove)).toList()) {
                        Location othersource = other.getSource();
                        if (othersource.row == source.row) {
                            uniqueRow = false;
                        }
                        if (othersource.col == source.col) {
                            uniqueCol = false;
                        }
                    }

                    if (uniqueCol) {
                        uniqueStr = source.getColString();
                    } else if (uniqueRow) {
                        uniqueStr = source.getRowString();
                    }
                    uniqueStr = uniqueStr.toLowerCase();
                    unUniqueMove.setMoveAnnotation(MoveAnnotation.annotate(unUniqueMove, movingPiece, uniqueStr));
                }
            } else {
                uniqueMoves.put(hash, new ArrayList<>() {{
                    add(move);
                }});
                move.setMoveAnnotation(MoveAnnotation.annotate(move, movingPiece));
            }
        });

    }

    /**
     * Pretty print.
     */
    public void prettyPrint() {
        Bitboard from = gensourceBB();
        Bitboard to = genDestinationBB();
        StringBuilder stringBuilder = new StringBuilder();
        String RESET = "\033[0m";
        String RED = "\033[0;31m";
        String BLUE = "\u001B[34m";
        for (int i = 0; i < Location.NUM_OF_SQUARES; i++) {
            stringBuilder.append("|");
            String s = "0";
            Location loc = Location.getLoc(i);
            if (from.isSet(loc)) {
                s = RED + "1" + RESET;
            } else if (to.isSet(loc)) {
                s = BLUE + "2" + RESET;
            }
            stringBuilder.append(s).append("| ");
            if ((i + 1) % 8 == 0)
                stringBuilder.append("\n");
        }
        System.out.println("\n" + stringBuilder);
    }

    /**
     * Gen source bb bitboard.
     *
     * @return the bitboard
     */
    public Bitboard gensourceBB() {
        Bitboard ret = new Bitboard();
        this.forEach(move -> ret.set(move.getSource(), true));
        return ret;
    }

    /**
     * Gen destination bb bitboard.
     *
     * @return the bitboard
     */
    public Bitboard genDestinationBB() {
        Bitboard ret = new Bitboard();
        this.forEach(move -> ret.set(move.getDestination(), true));
        return ret;
    }
//
//    @Override
//    public void doneAdding() {
//        uniqueMoves.clear();
//    }

    /**
     * Gets clean list.
     *
     * @return the clean list
     */
    public MovesList getCleanList() {
        return new MovesList(this);
    }

}
