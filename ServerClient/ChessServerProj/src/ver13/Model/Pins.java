package ver13.Model;

import ver13.Model.hashing.Zobrist;
import ver13.SharedClasses.Location;
import ver13.SharedClasses.moves.Direction;

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
        StringBuilder stringBuilder = new StringBuilder("\n");
        for (Bitboard bitboard : values()) {
            stringBuilder.append(bitboard.prettyBoard()).append("\n");
        }
        System.out.println(stringBuilder);
    }
}