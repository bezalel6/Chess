package ver11.SharedClasses.moves;

import ver11.SharedClasses.Location;
import ver11.SharedClasses.PlayerColor;

import java.util.Arrays;
import java.util.HashMap;


public enum Direction {
    //region directions
    U(8) {
        @Override
        public Direction opposite() {
            return D;
        }
    },
    D(-8) {
        @Override
        public Direction opposite() {
            return U;
        }
    },
    L(s.notAFile, -1) {
        @Override
        public Direction opposite() {
            return R;
        }
    },
    R(s.notHFile, 1) {
        @Override
        public Direction opposite() {
            return L;
        }
    },
    U_U(U, U) {
        @Override
        public Direction opposite() {
            return D_D;
        }
    }, D_D(D, D) {
        @Override
        public Direction opposite() {
            return U_U;
        }
    },

    U_R(U, R) {
        @Override
        public Direction opposite() {
            return D_L;
        }
    },
    U_L(U, L) {
        @Override
        public Direction opposite() {
            return D_R;
        }
    },

    D_R(D, R) {
        @Override
        public Direction opposite() {
            return U_L;
        }
    },
    D_L(D, L) {
        @Override
        public Direction opposite() {
            return U_R;
        }
    },
    //region knight directions
    U_U_R(U, U, R) {
        @Override
        public Direction opposite() {
            return D_D_R;
        }
    },
    U_U_L(U, U, L) {
        @Override
        public Direction opposite() {
            return D_D_L;
        }
    },
    U_R_R(U, R, R) {
        @Override
        public Direction opposite() {
            return D_L_L;
        }
    },
    U_L_L(U, L, L) {
        @Override
        public Direction opposite() {
            return D_R_R;
        }
    },
    D_D_R(D, D, R) {
        @Override
        public Direction opposite() {
            return U_U_L;
        }
    },
    D_D_L(D, D, L) {
        @Override
        public Direction opposite() {
            return U_U_R;
        }
    },
    D_R_R(D, R, R) {
        @Override
        public Direction opposite() {
            return U_L_L;
        }
    },
    D_L_L(D, L, L) {
        @Override
        public Direction opposite() {
            return U_R_R;
        }
    };

    public static final int NUM_OF_DIRECTIONS;
    public static final int NUM_OF_KNIGHT_DIRECTIONS;
    public static final int NUM_OF_DIRECTIONS_WO_KNIGHT;
    public final static Direction[] ALL_DIRECTIONS = values();
    //endregion
    //endregion
    final static PlayerColor normalPerspective = PlayerColor.WHITE;
    private final static HashMap<Integer, Direction> lookup;

    static {
        NUM_OF_DIRECTIONS = values().length;
        NUM_OF_KNIGHT_DIRECTIONS = Arrays.stream(values()).mapToInt(d -> d.name().replace("_", "").length() == 3 ? 1 : 0).sum();
        NUM_OF_DIRECTIONS_WO_KNIGHT = NUM_OF_DIRECTIONS - NUM_OF_KNIGHT_DIRECTIONS;

        lookup = new HashMap<>();
        for (Direction direction : ALL_DIRECTIONS) {
            lookup.put(direction.offset, direction);
        }
    }

    //todo null to save space
    public final long andWith;//todo check if its postfix/prefix
    public final int offset;
    public final Direction[] combination;

    Direction(Direction... combination) {
        assert combination.length > 0;
        this.andWith = 0;
        this.offset = Arrays.stream(combination).mapToInt(dir -> dir.offset).sum();
        this.combination = combination;
    }

    Direction(int offset) {
        this(s.everything, offset);
    }

    Direction(long andWith, int offset) {
        this.offset = offset;
        this.andWith = andWith;
        this.combination = new Direction[]{this};
    }

    public static Direction getRelative(Location loc1, Location loc2) {
        return getDirectionByOffset(loc1.asInt() - loc2.asInt());
    }

    public static Direction getDirectionByOffset(int offset) {
        offset /= (double) offset / 8;
        return lookup.get(offset);
    }

    public int asInt() {
        return ordinal();
    }

    public Direction perspective(PlayerColor playerColor) {
        return playerColor == normalPerspective ? this : opposite();
    }

    public abstract Direction opposite();
}

class s {
    public static final long notAFile = 0xfefefefefefefefeL;
    public static final long notHFile = 0x7f7f7f7f7f7f7f7fL;
    public static final long everything = 0xffffffffffffffffL;
}