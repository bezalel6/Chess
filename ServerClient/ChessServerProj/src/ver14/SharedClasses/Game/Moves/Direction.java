package ver14.SharedClasses.Game.Moves;

import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Direction - represents a moving direction on a board. sort of like a vector.
 * has an {@link #offset} that is added to a certain location or bitboard, in order to achieve movement in that direction.
 * the general direction map looks like this:
 * <table>
 * <tr>
 * <td>-9 </td>
 * <td>-8 </td>
 * <td>-7 </td>
 * </tr>
 * <tr>
 * <td>-1 </td>
 * <td> loc </td>
 * <td > 1 </td>
 * </tr>
 * <tr>
 * <td> 7 </td>
 * <td> 8 </td>
 * <td> 9 </td>
 * </tr>
 * </table>
 * <p>
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum Direction {
    /**
     * one square up the board.
     */
    U(8) {
        @Override
        public Direction opposite() {
            return D;
        }
    },
    /**
     * one square down the board.
     */
    D(-8) {
        @Override
        public Direction opposite() {
            return U;
        }
    },
    /**
     * one square left.
     */
    L(BitData.notAFile, -1) {
        @Override
        public Direction opposite() {
            return R;
        }
    },
    /**
     * one square right.
     */
    R(BitData.notHFile, 1) {
        @Override
        public Direction opposite() {
            return L;
        }
    },
    /**
     * two squares up.
     */
    U_U(U, U) {
        @Override
        public Direction opposite() {
            return D_D;
        }
    },
    /**
     * two squares down.
     */
    D_D(D, D) {
        @Override
        public Direction opposite() {
            return U_U;
        }
    },

    /**
     * one square up and one square right.
     */
    U_R(U, R) {
        @Override
        public Direction opposite() {
            return D_L;
        }
    },
    /**
     * one square up and one square left.
     */
    U_L(U, L) {
        @Override
        public Direction opposite() {
            return D_R;
        }
    },

    /**
     * one square down and one square right.
     */
    D_R(D, R) {
        @Override
        public Direction opposite() {
            return U_L;
        }
    },
    /**
     * one square down and one square left.
     */
    D_L(D, L) {
        @Override
        public Direction opposite() {
            return U_R;
        }
    },
    /**
     * two squares up and one square right.
     */
    U_U_R(U, U, R) {
        @Override
        public Direction opposite() {
            return D_D_R;
        }
    },
    /**
     * two squares up and one square left.
     */
    U_U_L(U, U, L) {
        @Override
        public Direction opposite() {
            return D_D_L;
        }
    },
    /**
     * two squares right and one square up.
     */
    U_R_R(U, R, R) {
        @Override
        public Direction opposite() {
            return D_L_L;
        }
    },
    /**
     * two squares left and one square up.
     */
    U_L_L(U, L, L) {
        @Override
        public Direction opposite() {
            return D_R_R;
        }
    },
    /**
     * two squares down and one square right.
     */
    D_D_R(D, D, R) {
        @Override
        public Direction opposite() {
            return U_U_L;
        }
    },
    /**
     * two squares down and one square left.
     */
    D_D_L(D, D, L) {
        @Override
        public Direction opposite() {
            return U_U_R;
        }
    },
    /**
     * two squares right and one square down.
     */
    D_R_R(D, R, R) {
        @Override
        public Direction opposite() {
            return U_L_L;
        }
    },
    /**
     * two squares left and one square down.
     */
    D_L_L(D, L, L) {
        @Override
        public Direction opposite() {
            return U_R_R;
        }
    };

    /**
     * The constant NUM_OF_DIRECTIONS.
     */
    public static final int NUM_OF_DIRECTIONS;
    /**
     * The constant NUM_OF_KNIGHT_DIRECTIONS.
     */
    public static final int NUM_OF_KNIGHT_DIRECTIONS;
    /**
     * The constant NUM_OF_DIRECTIONS_WO_KNIGHT.
     */
    public static final int NUM_OF_DIRECTIONS_WO_KNIGHT;
    /**
     * The All directions.
     */
    public final static Direction[] ALL_DIRECTIONS = values();
    /**
     * The All used directions.
     */
    public final static List<Direction> ALL_USED_DIRECTIONS = Arrays.stream(values()).filter(d -> d != U_U && d != D_D).collect(Collectors.toList());

    /**
     * the perspective the offset is correct for. if the moving piece's color is not this value the direction need to be flipped.
     */
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

    /**
     * some directions need to filter false positives. for example: moving left one square from the left-most column,
     * will overflow to the previous row. to fix this problem some directions have a andWith value
     * they have to perform a bitwise and with, after every offset. to cancel the false positives. in the left direction example, the andWith is the whole board but the right-most column
     */
    public final long andWith;
    /**
     * The actual offset.
     */
    public final int offset;
    /**
     * an int for quick access by index.
     */
    public final int asInt;

    /**
     * some
     */
    final Direction[] combination;

    Direction(Direction... combination) {
        assert combination.length > 0;
        this.andWith = 0;
        this.offset = Arrays.stream(combination).mapToInt(dir -> dir.offset).sum();
        this.combination = combination;
        this.asInt = ordinal();
    }

    Direction(int offset) {
        this(BitData.everything, offset);
    }

    Direction(long andWith, int offset) {
        this.offset = offset;
        this.andWith = andWith;
        this.combination = new Direction[]{this};
        this.asInt = ordinal();
    }

    /**
     * Gets relative.
     *
     * @param loc1 the loc 1
     * @param loc2 the loc 2
     * @return the relative
     */
    public static Direction getRelative(Location loc1, Location loc2) {
        return getDirectionByOffset(loc1.asInt - loc2.asInt);
    }

    /**
     * Gets direction by offset.
     *
     * @param offset the offset
     * @return the direction by offset
     */
    public static Direction getDirectionByOffset(int offset) {
        offset /= (double) offset / 8;
        return lookup.get(offset);
    }

    /**
     * Get combination direction [ ].
     *
     * @return the direction [ ]
     */
    public Direction[] getCombination() {
        return combination;
    }

    /**
     * gets the correct perspective for the provided player color.this is necessary because for example:
     * a white pawn push({@link #U}) is the exact opposite of a black pawn push ({@link #D}). so the perspective needs to be in relation to the moving color.
     *
     * @param playerColor the player color
     * @return the direction
     */
    public Direction perspective(PlayerColor playerColor) {
        return playerColor == normalPerspective ? this : opposite();
    }

    /**
     * the Opposite direction to this one.
     *
     * @return the direction
     */
    public abstract Direction opposite();
}

