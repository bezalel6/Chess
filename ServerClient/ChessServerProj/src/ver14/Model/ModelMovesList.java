package ver14.Model;

import ver14.Model.MoveGenerator.GenerationSettings;
import ver14.Model.MoveGenerator.MoveGenerator;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Game.moves.MoveAnnotation;
import ver14.SharedClasses.Game.moves.MovesList;
import ver14.SharedClasses.Game.pieces.Piece;
import ver14.SharedClasses.Game.pieces.PieceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class ModelMovesList extends MovesList {
    private final MoveGenerator generator;
    private final GenerationSettings generationSettings;
    private HashMap<Integer, ArrayList<Move>> uniqueMoves = null;
    private ArrayList<Move> rejectedPseudoLegal = new ArrayList<>();


    public ModelMovesList(MoveGenerator generator, GenerationSettings generationSettings) {
        this.generator = generator;
        this.generationSettings = generationSettings;
    }

    public void addAll(ModelMovesList other, PieceType pieceType) {
        for (Move move : other)
            add(move, pieceType);
    }

    public boolean add(Move adding, PieceType movingPiece) {
        if (adding == null)
            return false;
        adding.setCreatorList(this);

        adding.setMovingColor(generator.getModel().getCurrentPlayer());

        if (generationSettings.anyLegal) {
            if (generator.isLegal(adding)) {
                super.add(adding);
                throw ListEx.FoundLegalMove;
            }
        } else {
            if (!generationSettings.legalize || generator.isLegal(adding)) {
                super.add(adding);
            } else if (rejectedPseudoLegal != null) {
                rejectedPseudoLegal.add(adding);
            }
        }

        return true;
    }

    public void initAnnotation() {
        uniqueMoves = new HashMap<>();
        Stream.concat(this.stream(), rejectedPseudoLegal.stream()).forEach(move -> {
            Piece movingPiece = generator.getModel().getSquare(move.getMovingFrom()).getPiece();
            assert movingPiece != null;
//            if (movingPiece.pieceType == PieceType.PAWN) {
//                return;
//            }
            int hash = move.getMovingTo().hash(movingPiece.pieceType);
            if (movingPiece.pieceType != PieceType.PAWN && uniqueMoves.containsKey(hash)) {
                ArrayList<Move> moves = uniqueMoves.get(hash);
                moves.add(move);
                for (Move unUniqueMove : moves) {
                    Location movingFrom = unUniqueMove.getMovingFrom();
                    String uniqueStr = movingFrom.toString();
                    boolean uniqueRow = true;
                    boolean uniqueCol = true;
                    for (Move other : moves.stream().filter(m -> !m.equals(unUniqueMove)).toList()) {
                        Location otherMovingFrom = other.getMovingFrom();
                        if (otherMovingFrom.row == movingFrom.row) {
                            uniqueRow = false;
                        }
                        if (otherMovingFrom.col == movingFrom.col) {
                            uniqueCol = false;
                        }
                    }

                    if (uniqueCol) {
                        uniqueStr = movingFrom.getColString();
                    } else if (uniqueRow) {
                        uniqueStr = movingFrom.getRowString();
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

    public void prettyPrint() {
        Bitboard from = genMovingFromBB();
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

    public Bitboard genMovingFromBB() {
        Bitboard ret = new Bitboard();
        this.forEach(move -> ret.set(move.getMovingFrom(), true));
        return ret;
    }

    public Bitboard genDestinationBB() {
        Bitboard ret = new Bitboard();
        this.forEach(move -> ret.set(move.getMovingTo(), true));
        return ret;
    }
//
//    @Override
//    public void doneAdding() {
//        uniqueMoves.clear();
//    }

    public MovesList getCleanList() {
        return new MovesList(this);
    }

    public static class ListEx extends RuntimeException {
        public static ListEx FoundLegalMove = new ListEx("found legal move");

        public ListEx(String message) {
            super(message);
        }
    }

}
