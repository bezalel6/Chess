package ver5.model_classes;

import ver5.SharedClasses.Location;
import ver5.SharedClasses.moves.Direction;
import ver5.model_classes.hashing.Zobrist;

import java.util.HashMap;

public class Pins extends HashMap<Long, Bitboard> {

    public Pins() {
    }

    public void addPin(Direction direction, Bitboard bitboard) {
        put(Zobrist.hash(direction), bitboard.cp());
    }


    public boolean isLeavingPin(Direction direction, Location movingTo) {
        Bitboard bitboard = get(Zobrist.hash(direction));
        return bitboard != null && !bitboard.isSet(movingTo);
    }

    public void prettyPrint() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Bitboard bitboard : values()) {
            stringBuilder.append(bitboard.prettyBoard()).append("\n");
        }
        System.out.println(stringBuilder);
    }
}
