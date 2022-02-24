package ver12.Model;

import ver12.Model.hashing.Zobrist;
import ver12.SharedClasses.Location;
import ver12.SharedClasses.moves.Direction;

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
