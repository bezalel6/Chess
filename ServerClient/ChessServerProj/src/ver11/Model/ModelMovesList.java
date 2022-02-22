package ver11.Model;

import ver11.Model.hashing.Zobrist;
import ver11.SharedClasses.Location;
import ver11.SharedClasses.moves.Move;
import ver11.SharedClasses.moves.MovesList;
import ver11.SharedClasses.pieces.PieceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ModelMovesList extends MovesList {
    private final static boolean UNIQUE_STRS = false;
    private final HashMap<Long, ArrayList<Move>> uniqueMoves = new HashMap<>();

    public Bitboard genDestinationBB() {
        Bitboard ret = new Bitboard();
        this.forEach(move -> ret.set(move.getMovingTo(), true));
        return ret;
    }

    public boolean add(Move adding, PieceType movingPiece) {
        if (doneAdding) {
            throw new Error("FINISHED ADDING TO THIS LIST");
        }
        if (adding == null)
            return false;
//
//        if (!canMakeMove(adding, movingPiece)) {
//            return false;
//        }
        super.add(adding);

        addedMove(adding, movingPiece);

        return true;
    }

    public void addAll(ModelMovesList other, PieceType pieceType) {
        for (Move move : other)
            add(move, pieceType);
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
                    if (Location.row(otherMovingFrom) == Location.row(movingFrom)) {
                        uniqueRow = false;
                    }
                    if (Location.col(otherMovingFrom) == Location.col(movingFrom)) {
                        uniqueCol = false;
                    }
                }
                if (uniqueCol) {
                    uniqueStr = movingFrom.getColString();
                } else if (uniqueRow) {
                    uniqueStr = movingFrom.getRowString();
                }
                move.getMoveAnnotation().setUniqueStr(uniqueStr);
            }
        } else {
            ArrayList<Move> putting = new ArrayList<>();
            putting.add(added);
            uniqueMoves.put(hash, putting);
        }
    }


    public Bitboard genMovingFromBB() {
        Bitboard ret = new Bitboard();
        this.forEach(move -> ret.set(move.getMovingFrom(), true));
        return ret;
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

    @Override
    public void doneAdding() {
        uniqueMoves.clear();
        doneAdding = true;

    }

    public MovesList getCleanList() {
        return new MovesList(this);
    }
}
