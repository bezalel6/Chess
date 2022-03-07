package ver14.Model;

import ver14.Model.MoveGenerator.GenerationSettings;
import ver14.Model.MoveGenerator.MoveGenerator;
import ver14.Model.hashing.Zobrist;
import ver14.SharedClasses.Location;
import ver14.SharedClasses.moves.Move;
import ver14.SharedClasses.moves.MovesList;
import ver14.SharedClasses.pieces.PieceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ModelMovesList extends MovesList {
    private final static boolean UNIQUE_STRS = false;
    private final HashMap<Long, ArrayList<Move>> uniqueMoves = new HashMap<>();
    private final MoveGenerator generator;
    private final GenerationSettings generationSettings;

    public ModelMovesList(MoveGenerator generator, GenerationSettings generationSettings) {
        this.generator = generator;
        this.generationSettings = generationSettings;
    }


    public void addAll(ModelMovesList other, PieceType pieceType) {
        for (Move move : other)
            add(move, pieceType);
    }

    public boolean add(Move adding, PieceType movingPiece) {
        if (doneAdding) {
            throw new Error("FINISHED ADDING TO THIS LIST");
        }
        if (adding == null)
            return false;

        assert size() < 2000;
        super.add(adding);

//        addedMove(adding, movingPiece);

        return true;
    }

    private void addedMove(Move added, PieceType movingPiece) {
        if (!UNIQUE_STRS || movingPiece == PieceType.PAWN)
            return;
        long hash = Zobrist.combineHashes(Zobrist.hash(added.getMovingTo()), Zobrist.hash(movingPiece));

        if (uniqueMoves.containsKey(hash)) {
            ArrayList<Move> moves = uniqueMoves.get(hash);
            moves.add(added);
            for (Move move : moves) {
                Location movingFrom = move.getMovingFrom();
                String uniqueStr = movingFrom.toString();
                boolean uniqueRow = true;
                boolean uniqueCol = true;
                for (Move other : moves.stream().filter(m -> !m.equals(added)).collect(Collectors.toCollection(ArrayList::new))) {
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
//                move.getMoveAnnotation().setUniqueStr(uniqueStr);
            }
        } else {
            ArrayList<Move> putting = new ArrayList<>();
            putting.add(added);
            uniqueMoves.put(hash, putting);
        }
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

    @Override
    public void doneAdding() {
        uniqueMoves.clear();
        doneAdding = true;

    }

    public MovesList getCleanList() {
        return new MovesList(this);
    }

}
